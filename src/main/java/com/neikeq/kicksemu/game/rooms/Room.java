package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.lobby.RoomLobby;
import com.neikeq.kicksemu.game.rooms.enums.*;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {

    private int id;

    private byte minLevel;
    private byte maxLevel;

    private int host;
    private int master;

    private String name;
    private String password;

    private RoomState state;
    private RoomMap map;
    private RoomBall ball;
    private RoomType type;
    private RoomSize maxSize;
    private GoalkeeperMode goalkeeperMode;

    private final RoomLobby roomLobby;

    private final Map<Integer, Session> players;

    private final List<Integer> confirmedPlayers;
    private final List<Integer> redTeam;
    private final List<Integer> blueTeam;
    private final List<Integer> observers;

    private final List<Short> redTeamPositions;
    private final List<Short> blueTeamPositions;

    private final Object locker = new Object();

    public byte tryJoinRoom(Session session, String password) {
        byte result = 0;

        synchronized (locker) {
            if (!isFull()) {
                // If room does not have a password, or typed password matches room's password
                if (getType() != RoomType.PASSWORD || password.equals(getPassword())) {
                    if (!isPlaying()) {
                        short level = PlayerInfo.getLevel(session.getPlayerId());

                        // If player level is not allowed in room settings
                        if (playerHasInvalidLevel(level)) {
                            result = (byte) 248; // Invalid level
                        } else {
                            // Join the room
                            addPlayer(session);
                        }
                    } else {
                        result = (byte) 250; // Match already started
                    }
                } else {
                    result = (byte) 251; // Wrong password
                }
            } else {
                result = (byte) 252; // Room is full
            }
        }

        return result;
    }

    public void addPlayer(Session session) {
        synchronized (locker) {
            int playerId = session.getPlayerId();

            // Remove player from the main lobby
            LobbyManager.removePlayer(playerId);

            // If this is the first player in the room, make it room master and host
            if (getPlayers().size() < 1) {
                setMaster(playerId);
                setHost(playerId);
            }

            // Add player to players list and room lobby
            getPlayers().put(playerId, session);
            getRoomLobby().addPlayer(playerId);

            // Add player to the correct team
            addPlayerToTeam(playerId);
        }

        session.setRoomId(getId());

        onPlayerJoined(session);
    }
    
    public void removePlayer(Session session, RoomLeaveReason reason) {
        int playerId = session.getPlayerId();

        synchronized (locker) {
            // Remove player from players list and room lobby
            getPlayers().remove(playerId);
            getRoomLobby().removePlayer(playerId);

            // Remove player from his team list
            removePlayerFromTeam(playerId);

            // If room is empty, remove it
            if (getPlayers().size() < 1) {
                RoomManager.removeRoom(getId());
            } else {
                // If the leaver was room master, set a new one
                if (playerId == getMaster()) {
                    updateMaster();
                }

                // If the leaver was room host, set a new one
                if (playerId == getHost()) {
                    updateHost();
                }
            }
        }

        // Notify the session
        session.onLeavedRoom();

        onPlayerLeaved(playerId, reason);
    }

    private void addPlayerToTeam(int playerId) {
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

    private void addPlayerToTeam(int playerId, RoomTeam team) {
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

    private void addPlayerToRedTeam(int playerId) {
        getRedTeam().add(playerId);

        short position = PlayerInfo.getPosition(playerId);
        getRedTeamPositions().add(position);
    }

    private void addPlayerToBlueTeam(int playerId) {
        getBlueTeam().add(playerId);

        short position = PlayerInfo.getPosition(playerId);
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

    public RoomTeam swapPlayerTeam(int playerId, RoomTeam currentTeam) {
        RoomTeam targetTeam = currentTeam == RoomTeam.RED ? RoomTeam.BLUE :  RoomTeam.RED;

        if (!isTeamFull(targetTeam)) {
            removePlayerFromTeam(playerId, currentTeam);
            addPlayerToTeam(playerId, targetTeam);

            return targetTeam;
        } else {
            return currentTeam;
        }
    }

    public void startCountDown() {
        setState(RoomState.COUNT_DOWN);
        getConfirmedPlayers().clear();

        ServerMessage msgStartCountDown = MessageBuilder.startCountDown((byte) -1);
        sendBroadcast(msgStartCountDown);
    }

    public void cancelCountDown() {
        setState(RoomState.WAITING);

        ServerMessage msgStopCountDown = MessageBuilder.cancelCountDown();
        sendBroadcast(msgStopCountDown);
    }

    private void onPlayerJoined(Session session) {
        int playerId = session.getPlayerId();

        // Send the room info to the client
        ServerMessage msgRoomInfo = MessageBuilder.roomInfo(this);
        session.send(msgRoomInfo);

        // Send to the client information about players inside the room
        sendRoomPlayersInfo(session);

        // Notify players in room about the new player
        getPlayers().values().stream()
                .filter(s -> s.getPlayerId() != playerId)
                .forEach(s -> {
                    ServerMessage msgNewPlayer = MessageBuilder.roomPlayerInfo(session, this);
                    s.sendAndFlush(msgNewPlayer);
                });
    }

    private void onPlayerLeaved(int playerId, RoomLeaveReason reason) {
        // Notify players in room about player leaving
        ServerMessage msgPlayerLeaved = MessageBuilder.leaveRoom(playerId, reason);
        sendBroadcast(msgPlayerLeaved);

        // If the countdown started, cancel it
        if (getState() == RoomState.COUNT_DOWN) {
            cancelCountDown();
        }
    }

    private void sendRoomPlayersInfo(Session session) {
        getPlayers().values().stream().forEach(s -> {
            ServerMessage roomPlayerInfo = MessageBuilder.roomPlayerInfo(s, this);
            session.send(roomPlayerInfo);
        });
    }

    private void updateMaster() {
        setMaster((Integer)getPlayers().keySet().toArray()[0]);

        ServerMessage msgRoomMaster = MessageBuilder.roomMaster(getMaster());
        sendBroadcast(msgRoomMaster);
    }

    private void updateHost() {
        setHost((Integer)getPlayers().keySet().toArray()[0]);
    }

    public RoomTeam getPlayerTeam(int playerId) {
        if (getRedTeam().contains(playerId)) {
            return RoomTeam.RED;
        } else if (getBlueTeam().contains(playerId)) {
            return RoomTeam.BLUE;
        }

        return null;
    }

    public void sendBroadcast(ServerMessage msg) {
        try {
            getPlayers().values().stream().forEach(s -> {
                msg.getByteBuf().retain();
                s.sendAndFlush(msg);
            });
        } finally {
            msg.getByteBuf().release();
        }
    }

    public void sendTeamBroadcast(ServerMessage msg, RoomTeam team, int broadcaster) {
        try {
            if (team != null) {
                List<Integer> teamPlayers = team == RoomTeam.RED ? getRedTeam() : getBlueTeam();

                teamPlayers.stream()
                        .filter(id -> !PlayerInfo.getIgnoredList(id).containsPlayer(broadcaster))
                        .forEach(playerId -> {
                            Session s = getPlayers().get(playerId);

                            msg.getByteBuf().retain();
                            s.sendAndFlush(msg);
                        });
            }
        } finally {
            msg.getByteBuf().release();
        }
    }

    public boolean isPlayerIn(int playerId) {
        return getPlayers().containsKey(playerId);
    }

    public boolean isFull() {
        synchronized (locker) {
            return getPlayers().size() >= getMaxSize().toInt();
        }
    }

    public boolean playerHasInvalidLevel(short level) {
        return level < getMinLevel() || level > getMaxLevel();
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

    public boolean isObserver(int playerId) {
        return getObservers().contains(playerId);
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
        return getPlayers().size() < 6 || getBlueTeam().size() != getRedTeam().size();
    }

    public Room() {
        players = new HashMap<>();

        confirmedPlayers = new ArrayList<>();

        redTeam = new ArrayList<>();
        blueTeam = new ArrayList<>();

        redTeamPositions = new ArrayList<>();
        blueTeamPositions = new ArrayList<>();

        observers = new ArrayList<>();

        roomLobby = new RoomLobby();

        state = RoomState.WAITING;
    }

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

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public GoalkeeperMode getGoalkeeperMode() {
        return goalkeeperMode;
    }

    public void setGoalkeeperMode(GoalkeeperMode goalkeeperMode) {
        this.goalkeeperMode = goalkeeperMode;
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
        return (byte)getPlayers().size();
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
    }

    public boolean isPlaying() {
        return getState() != RoomState.WAITING;
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

    public RoomLobby getRoomLobby() {
        return roomLobby;
    }

    public List<Integer> getObservers() {
        return observers;
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

    public RoomState getState() {
        return state;
    }

    public void setState(RoomState state) {
        this.state = state;
    }
}
