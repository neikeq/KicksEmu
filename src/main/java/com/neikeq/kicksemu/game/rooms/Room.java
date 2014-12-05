package com.neikeq.kicksemu.game.rooms;

import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.lobby.RoomLobby;
import com.neikeq.kicksemu.game.rooms.enums.*;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Room {

    private int id;

    private byte minLevel;
    private byte maxLevel;

    private int host;
    private int master;

    private boolean playing;

    private String name;
    private String password;

    private RoomMap map;
    private RoomBall ball;
    private RoomType type;
    private RoomSize maxSize;
    private GoalkeeperMode goalkeeperMode;

    private Map<Integer, Session> players;

    private List<Integer> redTeam;
    private List<Integer> blueTeam;

    private List<Integer> observers;

    private List<Short> positions;

    private RoomLobby roomLobby;

    private final Object locker = new Object();

    public void addPlayer(Session session) {
        synchronized (locker) {
            int playerId = session.getPlayerInfo().getId();

            // Remove player from the main lobby
            LobbyManager.removePlayer(playerId);

            // If this is the first player in the room, make it room master and host
            if (getPlayers().size() < 1) {
                master = playerId;
                host = playerId;
            }

            // Add player to players list and room lobby
            getPlayers().put(playerId, session);
            getRoomLobby().addPlayer(playerId);

            // Add player to the correct team
            addPlayerToTeam(playerId);

            updatePositions();
        }

        session.setRoomId(id);

        onPlayerJoined(session);
    }
    
    public void removePlayer(Session session, RoomLeaveReason reason) {
        int playerId = session.getPlayerInfo().getId();

        synchronized (locker) {
            // Remove player from players list and room lobby
            getPlayers().remove(playerId);
            getRoomLobby().removePlayer(playerId);

            // Remove player from his team list
            if (removePlayerFromTeam(playerId, getRedTeam()) < 0) {
                removePlayerFromTeam(playerId, getBlueTeam());
            }

            // If room is empty, remove it
            if (getPlayers().size() < 1) {
                RoomManager.removeRoom(id);
            } else {
                // If the leaver was room master, set a new one
                if (playerId == master) {
                    updateMaster();
                }

                // If the leaver was room host, set a new one
                if (playerId == host) {
                    updateHost();
                }
            }

            updatePositions();
        }

        // Notify the session
        session.onLeavedRoom();

        onPlayerLeaved(playerId, reason);
    }

    private void addPlayerToTeam(int playerId) {
        if (getRedTeam().size() > getBlueTeam().size()) {
            if (!getBlueTeam().contains(playerId)) {
                getBlueTeam().add(playerId);
            }
        } else {
            if (!getRedTeam().contains(playerId)) {
                getRedTeam().add(playerId);
            }
        }
    }

    private int removePlayerFromTeam(int playerId, List<Integer> team) {
        int index = team.indexOf(playerId);

        if (index >= 0) {
            team.remove(index);
        }

        return index;
    }

    private void onPlayerJoined(Session session) {
        int playerId = session.getPlayerInfo().getId();

        // Notify players in room about the new player
        getPlayers().values().stream()
                .filter(s -> s.getPlayerInfo().getId() != playerId)
                .forEach(s -> {
                    ServerMessage msgNewPlayer = MessageBuilder.roomPlayerInfo(session, this);
                    s.sendAndFlush(msgNewPlayer);
                });
    }

    private void onPlayerLeaved(int playerId, RoomLeaveReason reason) {
        // Notify players in room about the new player
        ServerMessage msgPlayerLeaved = MessageBuilder.leaveRoom(playerId, reason);
        sendBroadcast(msgPlayerLeaved);
    }

    private void updateMaster() {
        master = (Integer)getPlayers().keySet().toArray()[0];

        ServerMessage msgRoomMaster = MessageBuilder.roomMaster(master);
        sendBroadcast(msgRoomMaster);
    }

    private void updateHost() {
        host = (Integer)getPlayers().keySet().toArray()[0];
    }

    /** Update the list players positions in the room.
     * This is required for sending in room list message
     */
    private void updatePositions() {
        getPositions().clear();
        getPositions().addAll(getPlayers().values().stream().map(
                session -> session.getPlayerInfo().getPosition()
        ).collect(Collectors.toList()));
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
        for (Session s : getPlayers().values()) {
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
            byteBuf.writeBytes(msg.getByteBuf());

            s.getChannel().writeAndFlush(byteBuf);
        }

        msg.getByteBuf().release();
    }

    public boolean isPlayerIn(int playerId) {
        return getPlayers().containsKey(playerId);
    }

    public boolean isFull() {
        synchronized (locker) {
            return players.size() >= maxSize.toInt();
        }
    }

    public boolean isObserver(int playerId) {
        return getObservers().contains(playerId);
    }

    public boolean isValidMinLevel(byte level) {
        for (Session s : getPlayers().values()) {
            if (s.getPlayerInfo().getLevel()  < level) {
                return false;
            }
        }

        return true;
    }

    public boolean isValidMaxLevel(byte level) {
        for (Session s : getPlayers().values()) {
            if (s.getPlayerInfo().getLevel()  > level) {
                return false;
            }
        }

        return true;
    }

    public Room() {
        players = new HashMap<>();

        redTeam = new ArrayList<>();
        blueTeam = new ArrayList<>();
        observers = new ArrayList<>();
        positions = new ArrayList<>();

        roomLobby = new RoomLobby();
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
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
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

    public List<Short> getPositions() {
        return positions;
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
}
