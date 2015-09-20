package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.chat.MessageType;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.lobby.RoomLobby;
import com.neikeq.kicksemu.game.rooms.enums.*;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.io.logging.Level;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.ThreadUtils;
import io.netty.util.concurrent.ScheduledFuture;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Room {

    private int id = -1;
    private int host = -1;
    private int master = -1;
    private int trainingFactor = 0;

    private byte minLevel = 1;
    private byte maxLevel = 60;

    private long timeStart = 0;
    private long timeLastJoin = 0;

    private String name = "";
    private String password = "";

    private RoomBall ball = RoomBall.STAR;
    private RoomMap map = RoomMap.A_BACK_STREET;
    private RoomMode roomMode = RoomMode.AI_GOALKEEPER;
    private RoomSize maxSize = RoomSize.SIZE_4V4;
    private RoomState state = RoomState.WAITING;
    private RoomAccessType accessType = RoomAccessType.FREE;

    private final RoomLobby roomLobby = new RoomLobby();
    private final SwapLocker swapLocker = new SwapLocker();

    private final Map<Integer, Session> players = new LinkedHashMap<>();

    private final List<Integer> confirmedPlayers = new ArrayList<>();
    private final List<Integer> redTeam = new ArrayList<>();
    private final List<Integer> blueTeam = new ArrayList<>();
    private final List<Integer> observers = new ArrayList<>();

    private final List<Short> redTeamPositions = new ArrayList<>();
    private final List<Short> blueTeamPositions = new ArrayList<>();

    private ScheduledFuture<?> countdownTimeoutFuture;
    private ScheduledFuture<?> loadingTimeoutFuture;

    final Object locker = new Object();

    void removeRoom() {
        synchronized (locker) {
            RoomManager.removeRoom(getId());
        }
    }

    public void tryJoinRoom(Session session, String password) {
        int playerId = session.getPlayerId();

        short result = 0;

        synchronized (locker) {
            if (isNotFull()) {
                if (isWaiting()) {
                    // Check password (moderators can bypass this)
                    if (getAccessType() != RoomAccessType.PASSWORD ||
                            password.equals(getPassword()) ||
                            PlayerInfo.isModerator(playerId)) {
                        short level = PlayerInfo.getLevel(playerId);

                        // If player level is allowed in room settings
                        if (isLevelAllowed(level)) {
                            // Join the room
                            addPlayer(session);
                        } else {
                            result = -8; // Invalid level
                        }
                    } else {
                        result = -5; // Wrong password
                    }
                } else {
                    result = -6; // Match already started
                }
            } else {
                result = -4; // Room is full
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.joinRoom(this, session.getPlayerId(), result));
        }
    }

    public void addPlayer(Session session) {
        synchronized (locker) {
            int playerId = session.getPlayerId();

            // Remove player from the main lobby
            LobbyManager.removePlayer(playerId);

            // If this is the first player in the room, make it room master and host
            if (getPlayers().size() < 1) {
                master = playerId;
                setHost(playerId);
            }

            // Add player to players list and room lobby
            getPlayers().put(playerId, session);
            getRoomLobby().addPlayer(playerId);

            // Add player to the correct team
            addPlayerToTeam(playerId);

            // If player is in observer mode add it to the observers list
            if (session.isObserver()) {
                addObserver(playerId);
            }

            swapLocker.lockPlayer(playerId);

            session.setRoomId(getId());
            session.send(joinRoomMessage(this, session.getPlayerId(), (short) 0));
            onPlayerJoined(session);
        }
    }
    
    public void removePlayer(Session session, RoomLeaveReason reason) {
        synchronized (locker) {
            int playerId = session.getPlayerId();

            // Remove player from players list and room lobby
            getPlayers().remove(playerId);
            getRoomLobby().removePlayer(playerId);

            // Remove player from his team list
            removePlayerFromTeam(playerId);

            // Remove player from observers (if he is in)
            observers.remove(Integer.valueOf(playerId));

            // If room is empty, remove it
            if (getPlayers().size() < 1) {
                removeRoom();
            } else {
                // If the leaver was room master, set a new one
                if (playerId == getMaster()) {
                    updateMaster();
                }

                // If the leaver was room host, set a new one
                if (playerId == getHost()) {
                    updateHost();
                    onHostLeaved(playerId);
                }
            }

            // Notify the session
            session.onLeavedRoom();
            session.sendAndFlush(leaveRoomMessage(playerId, reason));

            onPlayerLeaved(session, reason);
        }
    }

    public RoomTeam swapPlayerTeam(int playerId, RoomTeam currentTeam) {
        RoomTeam targetTeam = currentTeam == RoomTeam.RED ? RoomTeam.BLUE :  RoomTeam.RED;

        synchronized (locker) {
            if (!isTeamFull(targetTeam)) {
                removePlayerFromTeam(playerId, currentTeam);
                addPlayerToTeam(playerId, targetTeam);

                return targetTeam;
            } else {
                return currentTeam;
            }
        }
    }

    void addPlayerToTeam(int playerId) {
        if (getRedTeam().size() > getBlueTeam().size()) {
            if (!getBlueTeam().contains(playerId)) {
                addPlayerToBlueTeam(playerId);
            }
        } else {
            if (!getRedTeam().contains(playerId)) {
                addPlayerToRedTeam(playerId);
            }
        }
    }

    void addPlayerToTeam(int playerId, RoomTeam team) {
        switch (team) {
            case RED:
                addPlayerToRedTeam(playerId);
                break;
            case BLUE:
                addPlayerToBlueTeam(playerId);
                break;
            default:
        }
    }

    void addPlayerToRedTeam(int playerId) {
        getRedTeam().add(playerId);

        short position = getPlayers().get(playerId).getCache().getPosition();
        getRedTeamPositions().add(position);
    }

    void addPlayerToBlueTeam(int playerId) {
        getBlueTeam().add(playerId);

        short position = getPlayers().get(playerId).getCache().getPosition();
        getBlueTeamPositions().add(position);
    }

    private void removePlayerFromTeam(int playerId) {
        if (getRedTeam().contains(playerId)) {
            removePlayerFromRedTeam(playerId);
        } else {
            removePlayerFromBlueTeam(playerId);
        }
    }

    private void removePlayerFromTeam(int playerId, RoomTeam team) {
        switch (team) {
            case RED:
                removePlayerFromRedTeam(playerId);
                break;
            case BLUE:
                removePlayerFromBlueTeam(playerId);
                break;
            default:
        }
    }

    private void removePlayerFromRedTeam(int playerId) {
        int index = getRedTeam().indexOf(playerId);

        if (index >= 0) {
            getRedTeam().remove(index);
            getRedTeamPositions().remove(index);
        }
    }

    private void removePlayerFromBlueTeam(int playerId) {
        int index = getBlueTeam().indexOf(playerId);

        if (index >= 0) {
            getBlueTeam().remove(index);
            getBlueTeamPositions().remove(index);
        }
    }

    public void startCountdown() {
        synchronized (locker) {
            if (players.size() > 1) {
                // There must be a delay between the last player joined the room and
                // the countdown starts. Otherwise it will freeze on 'Connecting...'.
                final long delay = DateUtils.currentTimeMillis() - timeLastJoin;
                final long minDelay = 1000;

                if (delay < minDelay) {
                    ThreadUtils.sleep(minDelay - delay);
                }
            }

            setState(RoomState.COUNT_DOWN);
            getConfirmedPlayers().clear();

            sendBroadcast(MessageBuilder.startCountDown((byte) -1));

            countdownTimeoutFuture = getPlayers().get(host).getChannel().eventLoop()
                    .schedule(() -> {
                        synchronized (locker) {
                            if (this.state() == RoomState.COUNT_DOWN) {
                                List<Integer> failedPlayers = new ArrayList<>(players.keySet());
                                confirmedPlayers.forEach(failedPlayers::remove);

                                if (!failedPlayers.isEmpty()) {
                                    List<String> info = failedPlayers.stream()
                                            .map(player ->
                                                    getPlayers().get(player).getCache().getName())
                                            .collect(Collectors.toList());

                                    sendBroadcast(MessageBuilder.hostInfo(this));
                                    sendBroadcast(MessageBuilder.chatMessage(
                                            MessageType.SERVER_MESSAGE,
                                            "Failed to connect: " + String.join(", ", info)));
                                }

                                cancelCountdown();
                            }
                        }
                    }, 5, TimeUnit.SECONDS);
        }
    }

    public void onCountdown(short count) {
        synchronized (locker) {
            if (state() == RoomState.COUNT_DOWN) {
                if (count == 0) {
                    startLoading();
                }

                sendBroadcast(MessageBuilder.countDown(count));
            }
        }
    }

    public void cancelCountdown() {
        synchronized (locker) {
            if (countdownTimeoutFuture.isCancellable()) {
                countdownTimeoutFuture.cancel(true);
            }
            setState(RoomState.WAITING);
            sendBroadcast(MessageBuilder.cancelCountDown());
        }
    }

    private void startLoading() {
        synchronized (locker) {
            setState(RoomState.LOADING);
            updateTrainingFactor();

            loadingTimeoutFuture = getPlayers().get(host).getChannel().eventLoop()
                    .schedule(() -> {
                        synchronized (locker) {
                            cancelLoading();
                        }
                    }, 30, TimeUnit.SECONDS);
        }
    }

    private void cancelLoading() {
        synchronized (locker) {
            if (getLoadingTimeoutFuture().isCancellable()) {
                getLoadingTimeoutFuture().cancel(true);
            }
            setState(RoomState.WAITING);
            sendBroadcast(MessageBuilder.cancelLoading());
        }
    }

    void onPlayerJoined(Session session) {
        // Send the room info to the client
        session.send(roomInfoMessage());

        // Send to the client information about players inside the room
        sendRoomPlayersInfo(session);

        timeLastJoin = DateUtils.currentTimeMillis();

        // Notify all the players in the room about the new player
        if (players.size() > 1) {
            notifyAboutNewPlayer(session);
        }
    }

    void onPlayerLeaved(Session session, RoomLeaveReason reason) {
        int playerId = session.getPlayerId();

        // Notify players in room about player leaving
        sendBroadcast(leaveRoomMessage(playerId, reason));

        if (isLoading()) {
            cancelLoading();
        } else if (state() == RoomState.COUNT_DOWN) {
            cancelCountdown();
        }
    }

    void notifyAboutNewPlayer(Session session) {
        try (Connection con = MySqlManager.getConnection()) {
            sendBroadcast(roomPlayerInfoMessage(session, con),
                    s -> s.getPlayerId() != session.getPlayerId());
        } catch (SQLException e) {
            Output.println("Exception when notifying about new room player: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    private void onHostLeaved(int playerId) {
        switch (state()) {
            case PLAYING:
                sendBroadcast(MessageBuilder.hostInfo(this));
                sendBroadcast(MessageBuilder.leaveRoom(playerId, RoomLeaveReason.DISCONNECTED));
                break;
            case LOADING:
                cancelLoading();
                break;
            case RESULT:
                ThreadUtils.sleep(3000);

                setState(RoomState.WAITING);
                sendBroadcast(MessageBuilder.unknown1());
                sendBroadcast(MessageBuilder.unknown2());
                break;
            default:
        }
    }

    private void updateMaster() {
        setMaster((Integer) getPlayers().keySet().toArray()[0]);
    }

    private void updateHost() {
        setHost((Integer) getPlayers().keySet().toArray()[0]);
    }

    public RoomTeam getPlayerTeam(int playerId) {
        if (getRedTeam().contains(playerId)) {
            return RoomTeam.RED;
        } else if (getBlueTeam().contains(playerId)) {
            return RoomTeam.BLUE;
        }

        return null;
    }

    ServerMessage roomPlayerInfoMessage(Session session, Connection... con) {
        return MessageBuilder.roomPlayerInfo(session, this, con);
    }

    ServerMessage leaveRoomMessage(int playerId, RoomLeaveReason reason) {
        return MessageBuilder.leaveRoom(playerId, reason);
    }

    ServerMessage roomMasterMessage(int master) {
        return MessageBuilder.roomMaster(master);
    }

    ServerMessage joinRoomMessage(Room room, int playerId, short result) {
        return MessageBuilder.joinRoom(room, playerId, result);
    }

    ServerMessage roomInfoMessage() {
        return MessageBuilder.roomInfo(this);
    }

    public void sendHostInfo() {
        synchronized (locker) {
            if (state == RoomState.COUNT_DOWN) {
                if (countdownTimeoutFuture.isCancellable()) {
                    countdownTimeoutFuture.cancel(true);
                }
                sendBroadcast(MessageBuilder.hostInfo(this));
            }
        }
    }

    void sendRoomPlayersInfo(Session session) {
        try (Connection con = MySqlManager.getConnection()) {
            getPlayers().values().forEach(s ->
                    session.send(roomPlayerInfoMessage(s, con)));
            session.flush();
        } catch (SQLException e) {
            Output.println("Exception when sending room players info to a player: " +
                    e.getMessage(), Level.DEBUG);
        }
    }

    public void sendBroadcast(ServerMessage msg) {
        try {
            getPlayers().values().forEach(currentSession -> currentSession.sendAndFlush(msg));
        } finally {
            msg.release();
        }
    }

    public void sendBroadcast(ServerMessage msg, Predicate<? super Session> filter) {
        try {
            getPlayers().values().stream().filter(filter).forEach(currentSession ->
                currentSession.sendAndFlush(msg));
        } finally {
            msg.release();
        }
    }

    public void sendTeamBroadcast(ServerMessage msg, RoomTeam team, int broadcaster) {
        try {
            if (team != null) {
                List<Integer> teamPlayers = team == RoomTeam.RED ? getRedTeam() : getBlueTeam();

                teamPlayers.stream()
                        .filter(id -> !PlayerInfo.getIgnoredList(id).containsPlayer(broadcaster))
                        .forEach(playerId -> getPlayers().get(playerId).sendAndFlush(msg));
            }
        } finally {
            msg.release();
        }
    }

    public boolean trainingFactorAllowsRewards() {
        return getTrainingFactor() > 0;
    }

    public boolean isPlayerIn(int playerId) {
        return getPlayers().containsKey(playerId);
    }

    public boolean isObserver(int playerId) {
        return getObservers().contains(playerId);
    }

    public boolean isNotFull() {
        synchronized (locker) {
            return getPlayers().size() < getMaxSize().toInt();
        }
    }

    private boolean isTeamFull(RoomTeam team) {
        switch (team) {
            case RED:
                return getRedTeam().size() >= 5;
            case BLUE:
                return getBlueTeam().size() >= 5;
            default:
                return true;
        }
    }

    public boolean isLevelAllowed(short level) {
        return level >= getMinLevel() && level <= getMaxLevel();
    }

    public boolean isValidMinLevel(byte level) {
        for (Session s : getPlayers().values()) {
            if (PlayerInfo.getLevel(s.getPlayerId()) < level) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidMaxLevel(byte level) {
        for (Session s : getPlayers().values()) {
            if (PlayerInfo.getLevel(s.getPlayerId()) > level) {
                return false;
            }
        }

        return true;
    }

    /** Returns true if there are not enough players to play a real match */
    public boolean isTraining() {
        return getPlayers().values().stream().filter(s -> !observers.contains(s.getPlayerId()))
                .count() < 6 || redTeamSize() != blueTeamSize();
    }

    public boolean isWaiting() {
        return state() == RoomState.WAITING;
    }

    public boolean isLoading() {
        return state() == RoomState.LOADING;
    }

    public boolean isInLobbyScreen() {
        return state() == RoomState.WAITING || state() == RoomState.COUNT_DOWN;
    }

    public void resetTrainingFactor() {
        trainingFactor = -1;
    }

    private void updateTrainingFactor() {
        trainingFactor = isTraining() ? 0 : getTeamSizes();
    }

    /* ---- Accessors ---- */

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RoomMap getMap() {
        return map;
    }

    public void setMap(RoomMap map) {
        this.map = map;
    }

    public RoomBall getBall() {
        return ball;
    }

    public void setBall(RoomBall ball) {
        this.ball = ball;
    }

    public RoomAccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(RoomAccessType accessType) {
        this.accessType = accessType;
    }

    public RoomMode getRoomMode() {
        return roomMode;
    }

    public void setRoomMode(RoomMode roomMode) {
        this.roomMode = roomMode;
    }

    public byte getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(byte minLevel) {
        this.minLevel = minLevel;
    }

    public byte getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(byte maxLevel) {
        this.maxLevel = maxLevel;
    }

    public RoomSize getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(RoomSize maxSize) {
        this.maxSize = maxSize;
    }

    public byte getCurrentSize() {
        return (byte) getPlayers().size();
    }

    public int getHost() {
        return host;
    }

    public void setHost(int host) {
        this.host = host;
    }

    public int getMaster() {
        return master;
    }

    public void setMaster(int master) {
        this.master = master;
        sendBroadcast(roomMasterMessage(master));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Integer, Session> getPlayers() {
        return players;
    }

    public List<Short> getRedTeamPositions() {
        return redTeamPositions;
    }

    public List<Short> getBlueTeamPositions() {
        return blueTeamPositions;
    }

    public List<Integer> getRedTeam() {
        return redTeam;
    }

    public List<Integer> getBlueTeam() {
        return blueTeam;
    }

    public int getTeamSizes() {
        return redTeamSize() + blueTeamSize();
    }

    private int redTeamSize() {
        return (int) getRedTeam().stream().filter(id -> !observers.contains(id)).count();
    }

    private int blueTeamSize() {
        return (int) getBlueTeam().stream().filter(id -> !observers.contains(id)).count();
    }

    public RoomLobby getRoomLobby() {
        return roomLobby;
    }

    public List<Integer> getObservers() {
        return observers;
    }

    void addObserver(int playerId) {
        observers.add(playerId);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getConfirmedPlayers() {
        return confirmedPlayers;
    }

    public RoomState state() {
        return state;
    }

    public void setState(RoomState state) {
        synchronized (locker) {
            this.state = state;
        }
    }

    public int getTrainingFactor() {
        return trainingFactor;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public SwapLocker getSwapLocker() {
        return swapLocker;
    }

    public ScheduledFuture<?> getLoadingTimeoutFuture() {
        return loadingTimeoutFuture;
    }
}
