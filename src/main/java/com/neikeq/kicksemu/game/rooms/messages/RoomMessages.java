package com.neikeq.kicksemu.game.rooms.messages;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.PlayerLevelCache;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.types.Soda;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.misc.quests.QuestManager;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.rooms.enums.RoomBall;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomMap;
import com.neikeq.kicksemu.game.rooms.enums.RoomMode;
import com.neikeq.kicksemu.game.rooms.enums.RoomSize;
import com.neikeq.kicksemu.game.rooms.enums.RoomState;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.enums.RoomAccessType;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.MatchRewards;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.rooms.match.TeamResult;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.network.packets.out.ServerMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.GameEvents;
import com.neikeq.kicksemu.utils.ThreadUtils;
import com.neikeq.kicksemu.utils.mutable.MutableBoolean;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.Connection;
import java.sql.SQLException;

public class RoomMessages {

    private static final int MAX_ROOM_NAME_LENGTH = 30;
    static final byte MAX_ROOM_LEVEL = 60;
    static final byte MIN_ROOM_LEVEL = 1;

    public static void createRoom(Session session, ClientMessage msg) {
        // If player is not already in a room
        if (session.getRoomId() <= 0) {
            RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
            String name = msg.readString(45);
            String password = msg.readString(4);
            msg.ignoreBytes(1);

            RoomMode roomMode = RoomMode.fromInt(msg.readByte());

            byte minLevel = msg.readByte();
            byte maxLevel = msg.readByte();

            RoomMap map = RoomMap.fromInt(msg.readShort());
            RoomBall ball = RoomBall.fromInt(msg.readShort());
            RoomSize maxSize = RoomSize.fromInt(msg.readByte());

            // Check that everything is correct
            byte result = 0;

            ServerType serverType = ServerManager.getServerType();

            if (minLevel < MIN_ROOM_LEVEL || maxLevel > MAX_ROOM_LEVEL) {
                result = (byte) -3; // Wrong level settings
            } else if (maxSize == null || type == null || map == null || ball == null ||
                    roomMode == null || roomMode.notValidForServer(serverType)) {
                result = (byte) -1; // System problem
            } else {
                short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

                if (playerLevel < minLevel || playerLevel > maxLevel) {
                    result = (byte) -4; // Invalid level
                }
            }

            // Send the result to the client
            session.send(MessageBuilder.createRoom((short) 0, result));

            // If everything is correct, create the room
            if (result == 0) {
                Room room = new Room();

                // Limit the length of the name
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                // If password is blank, disable password usage
                if (type == RoomAccessType.PASSWORD && password.isEmpty()) {
                    type = RoomAccessType.FREE;
                }

                // Set room information from received data
                room.setName(name);
                room.setPassword(password);
                room.setAccessType(type);
                room.setRoomMode(roomMode);
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMap(map);
                room.setBall(ball);
                room.setMaxSize(maxSize);

                synchronized (RoomManager.ROOMS_LOCKER) {
                    // Get the room id
                    room.setId(RoomManager.getSmallestMissingIndex());
                    // Add it to the rooms list
                    RoomManager.addRoom(room);
                    // Add the player to the room
                    room.addPlayer(session);
                }

                // Notify the client to join the room
                session.send(MessageBuilder.joinRoom(room, session.getPlayerId(), result));
            }
        }
    }

    public static void joinRoom(Session session, ClientMessage msg) {
        if (session.getRoomId() > 0) return;

        int roomId = msg.readShort();
        String password = msg.readString(4);

        Room room = RoomManager.getRoomById(roomId);

        // Try to join the room.
        if (room != null) {
            room.tryJoinRoom(session, password);
        } else {
            // Result -3 means that the room does not exists.
            session.send(MessageBuilder.joinRoom(null, session.getPlayerId(), (byte) -3));
        }
    }

    public static void quickJoinRoom(Session session) {
        // Ignore the message if the player is already in a room
        if (session.getRoomId() > 0) return;

        short level = PlayerInfo.getLevel(session.getPlayerId());

        Room room = RoomManager.getQuickRoom(level);

        // If a valid room was found
        if (room != null) {
            room.tryJoinRoom(session, "");
        } else {
            // Notify the player that no rooms were found
            session.send(MessageBuilder.quickJoinRoom((byte) -2));
        }
    }

    public static void leaveRoom(Session session, ClientMessage msg) {
        short roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(playerId) &&
                (room.state() == RoomState.WAITING || room.state() == RoomState.COUNT_DOWN)) {
            session.leaveRoom(RoomLeaveReason.LEAVED);
        }
    }

    public static void roomList(Session session, ClientMessage msg) {
        short page = msg.readShort();
        session.send(MessageBuilder.roomList(RoomManager.getRoomsFromPage(page),
                page, (byte) 0));
    }

    public static void roomMap(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short mapId = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomMap map = RoomMap.fromInt(mapId);

            if (map != null) {
                room.setMap(map);

                // Notify players in room that map changed
                room.sendBroadcast(MessageBuilder.roomMap(mapId));
            }
        }
    }

    public static void roomBall(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short ballId = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.isPlayerIn(session.getPlayerId())) {
            RoomBall ball = RoomBall.fromInt(ballId);

            if (ball != null) {
                room.setBall(ball);

                // Notify players in room that ball changed
                room.sendBroadcast(MessageBuilder.roomBall(ballId));
            }
        }
    }

    public static void roomSettings(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        RoomAccessType type = RoomAccessType.fromShort(msg.readShort());
        String name = msg.readString(45);
        String password = msg.readString(4);
        msg.ignoreBytes(1);
        RoomMode roomMode = RoomMode.fromInt(msg.readByte());
        byte minLevel = msg.readByte();
        byte maxLevel = msg.readByte();
        RoomSize maxSize = RoomSize.fromInt(msg.readByte());

        if (minLevel < MIN_ROOM_LEVEL) {
            minLevel = MIN_ROOM_LEVEL;
        }

        if (maxLevel > MAX_ROOM_LEVEL) {
            maxLevel = MAX_ROOM_LEVEL;
        }

        // Check that settings are valid

        byte result = 0;

        Room room = RoomManager.getRoomById(roomId);

        ServerType serverType = ServerManager.getServerType();

        if (maxSize == null || type == null || roomMode == null ||
                roomMode.notValidForServer(serverType)) {
            result = (byte) -1; // System problem
        } else if (room == null) {
            result = (byte) -2; // Room does not exist
        } else if (room.getMaster() != session.getPlayerId()) {
            result = (byte) -3; // Player is not room's master
        } else if (maxSize.toInt() < room.getPlayers().size()) {
            result = (byte) -4; // Size is lower than players in room
        } else if (minLevel > maxLevel) {
            result = (byte) -5; // Wrong level settings
        } else if (!room.isValidMaxLevel(maxLevel)) {
            result = (byte) -7; // Invalid maximum level
        } else if (!room.isValidMinLevel(minLevel)) {
            result = (byte) -8; // Invalid minimum level
        } else {
            short playerLevel = PlayerInfo.getLevel(session.getPlayerId());

            if (playerLevel < minLevel || playerLevel > maxLevel) {
                result = (byte) -6; // Invalid level
            } else {
                // Limit the length of the name
                if (name.length() > MAX_ROOM_NAME_LENGTH) {
                    name = name.substring(0, MAX_ROOM_NAME_LENGTH);
                }

                // Update room settings
                room.setAccessType(type);
                room.setName(name);
                room.setPassword(password);
                room.setRoomMode(roomMode);
                room.setMinLevel(minLevel);
                room.setMaxLevel(maxLevel);
                room.setMaxSize(maxSize);

                room.sendBroadcast(MessageBuilder.roomSettings(room, result));
            }
        }

        if (result != 0) {
            session.send(MessageBuilder.roomSettings(room, result));
        }
    }

    public static void swapTeam(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        Room room = RoomManager.getRoomById(roomId);

        // If the room is valid, the player is inside it and it is in waiting state
        if (room != null && room.isPlayerIn(playerId) && room.state() == RoomState.WAITING) {
            if (!room.getSwapLocker().isPlayerLocked(playerId)) {
                RoomTeam currentTeam = room.getPlayerTeam(playerId);
                RoomTeam newTeam = room.swapPlayerTeam(playerId, currentTeam);

                if (newTeam != currentTeam) {
                    room.getSwapLocker().lockPlayer(playerId);
                    room.sendBroadcast(MessageBuilder.swapTeam(playerId, newTeam));
                }
            }
        }
    }

    public static void kickPlayer(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerToKick = msg.readInt();

        byte result = 0;

        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the room exist and the player is inside it
        if (room != null && room.getId() == roomId) {
            // If the player is the room master
            if (room.getMaster() == session.getPlayerId() && room.isInLobbyScreen()) {
                // If the player is in the room
                if (room.isPlayerIn(playerToKick)) {
                    room.getPlayers().get(playerToKick).leaveRoom(RoomLeaveReason.KICKED);
                } else {
                    result = (byte) -4; // Player not found
                }
            } else {
                result = (byte) -3; // Not the room master
            }
        } else {
            result = (byte)- 2; // Invalid room
        }

        // If there is something wrong, notify the client
        if (result != 0) {
            session.send(MessageBuilder.kickPlayer(result));
        }
    }

    public static void invitePlayer(Session session, ClientMessage msg) {
        Room room = RoomManager.getRoomById(session.getRoomId());

        // If the player is in a room
        if (room != null) {
            int playerToInvite = msg.readInt();

            byte result = 0;

            if (room.isNotFull()) {
                // If the player to invite is in the main lobby
                if (LobbyManager.getMainLobby().getPlayers().contains(playerToInvite)) {
                    Session sessionToInvite = ServerManager.getPlayers().get(playerToInvite);

                    if (UserInfo.getSettings(sessionToInvite.getUserId()).getInvites()) {
                        byte level = (byte) PlayerInfo.getLevel(sessionToInvite.getPlayerId());

                        // If player level meets the level requirement of the room
                        if (room.getMinLevel() <= level && room.getMaxLevel() >= level) {
                            ServerMessage invitation = MessageBuilder.invitePlayer(result,
                                    room, PlayerInfo.getName(session.getPlayerId()));
                            sessionToInvite.sendAndFlush(invitation);
                        } else {
                            result = (byte) -5; // Player does not meet the level requirements
                        }
                    } else {
                        result = (byte) -3; // Player does not accept invitations
                    }
                } else {
                    result = (byte) -2; // Player not found
                }
            }

            // If there is something wrong, notify the client
            if (result != 0) {
                session.send(MessageBuilder.invitePlayer(result, null, ""));
            }
        }
    }

    public static void startCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room != null) {
                int playerId = session.getPlayerId();
                byte type = msg.readByte();

                switch (type) {
                    case 1:
                        if (!room.getConfirmedPlayers().contains(playerId)) {
                            room.getConfirmedPlayers().add(playerId);
                        }

                        if (room.getConfirmedPlayers().size() >= room.getPlayers().size()) {
                            room.getConfirmedPlayers().clear();
                            room.sendBroadcast(MessageBuilder.startCountDown((byte) 1));
                        }
                        break;
                    case -1:
                        if (!room.isPlaying() && room.getMaster() == playerId) {
                            room.startCountdown();
                        }
                        break;
                    default:
                }
            }
        }
    }

    public static void hostInfo(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.getHost() == session.getPlayerId()) {
                room.sendHostInfo();
            }
        }
    }

    public static void countDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        short count = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.getMaster() == session.getPlayerId()) {
            room.onCountdown(count);
        }
    }

    public static void cancelCountDown(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.state() == RoomState.COUNT_DOWN) {
                room.cancelCountdown();
            }
        }
    }

    public static void matchLoading(Session session, ClientMessage msg) {
        msg.readInt();
        int playerId = session.getPlayerId();
        int roomId = msg.readShort();
        short status = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.state() == RoomState.LOADING) {
                room.sendBroadcast(MessageBuilder.matchLoading(playerId, roomId, status));
            }
        }
    }

    public static void playerReady(Session session, ClientMessage msg) {
        int roomId = msg.readShort();
        int playerId = session.getPlayerId();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            if (room.state() == RoomState.LOADING) {
                if (!room.getConfirmedPlayers().contains(playerId)) {
                    room.getConfirmedPlayers().add(playerId);

                    // Instead of waiting 5 seconds (or not), we send an udp ping immediately to
                    // the client so we can update his udp port (if changed) before match starts
                    UdpPing.sendUdpPing(session);
                }

                if (room.getConfirmedPlayers().size() >= room.getPlayers().size()) {
                    room.setState(RoomState.PLAYING);
                    room.setTimeStart(DateUtils.currentTimeMillis());
                    room.sendBroadcast(MessageBuilder.playerReady((byte) 0));
                }
            } else {
                session.send(MessageBuilder.playerReady((byte) 0));
            }
        }
    }

    public static void startMatch(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            byte result = 0;

            if (room.state() == RoomState.LOADING &&
                    room.getConfirmedPlayers().size() < room.getPlayers().size()) {
                result = -1;
            }

            session.send(MessageBuilder.startMatch(result));
        }
    }

    public static void matchResult(Session session, ClientMessage msg) {
        final int roomId = msg.readShort();
        msg.ignoreBytes(4);

        if (session.getRoomId() != roomId) return;

        Room room = RoomManager.getRoomById(roomId);

        // If match was not playing
        if (room.state() != RoomState.PLAYING) return;

        MatchResult result = MatchResult.fromMessage(msg,
                room.redTeamSize() + room.blueTeamSize());

        // Apply level gap bonus if the levels difference in room settings is less than 10
        final boolean levelGapReward = room.getMaxLevel() - room.getMinLevel() < 10;
        final boolean goldenTime = GameEvents.isGoldenTime();
        final boolean levelExpWeightingFlag = Configuration.getBoolean("game.match.bonus.lowers");

        // Check the match countdown
        final long startTime = DateUtils.currentTimeMillis();
        final long countdown = 300 - ((startTime - room.getTimeStart()) / 1000);
        final short gameCountdown = result.getCountdown();

        // If countdown sent by client is not valid (more than 20 secs less than server's),
        // disable rewards and player's history updating
        if (gameCountdown < countdown && gameCountdown - countdown > 20) {
            room.resetTrainingFactor();
        }

        room.setState(RoomState.RESULT);

        try (Connection con = MySqlManager.getConnection()) {
            // Calculate average level
            PlayerLevelCache levelCache = new PlayerLevelCache();
            MutableInteger roomAvgLevel = new MutableInteger(0);

            if (result.getPlayers().size() > 0 && levelExpWeightingFlag) {
                MutableInteger avgLevel = new MutableInteger(0);

                result.getPlayers().stream().forEach(pr -> {
                    int playerId = pr.getPlayerId();
                    avgLevel.add(levelCache.getPlayerLevel(playerId, con));
                });

                roomAvgLevel.add(avgLevel.get() / result.getPlayers().size());
            }

            // Reward players
            result.getPlayers().stream().forEach(pr -> {
                int playerId = pr.getPlayerId();
                short finishedQuest = -1;
                Session playerSession = room.getPlayers().get(playerId);

                int reward = MatchRewards.calculateReward(pr, room, gameCountdown);

                if (reward > 0) {
                    int appliedReward = reward;

                    RoomTeam playerTeam = room.getPlayerTeam(playerId);

                    // If player's position is a DF branch
                    if (Position.trunk(PlayerInfo.getPosition(playerId, con)) == Position.DF) {
                        short scoredGoals = playerTeam == RoomTeam.RED ?
                                result.getRedTeam().getGoals() : result.getBlueTeam().getGoals();
                        short concededGoals = playerTeam == RoomTeam.RED ?
                                result.getBlueTeam().getGoals() : result.getRedTeam().getGoals();

                        // If player's team did not lose and conceded 1 or less goals,
                        // increase his rewards by 30%
                        appliedReward += concededGoals <= 1 && scoredGoals >= concededGoals ?
                                (reward * 30) / 100 : 0;
                    }

                    if (levelExpWeightingFlag) {
                        int lvl = levelCache.getPlayerLevel(playerId, con);
                        int diff = roomAvgLevel.get() - lvl;
                        boolean levelExpWeighting = (diff > 0);

                        if (levelExpWeighting) {
                            diff = diff * 2;
                            if (diff > 75) diff = 75;
                        }

                        appliedReward += levelExpWeighting ? (reward * (diff)) / 100 : 0;
                    }

                    appliedReward += levelGapReward ? (reward * 10) / 100 : 0;
                    appliedReward += goldenTime ? (reward * 50) / 100 : 0;
                    // If player is mvp increase his rewards by 25%
                    appliedReward += result.getMom() == playerId ? (reward * 25) / 100 : 0;

                    final MutableInteger points = new MutableInteger(appliedReward);
                    final MutableInteger experience = new MutableInteger(appliedReward);

                    if (room.getTrainingFactor() > 0) {
                        // Apply item reward bonuses
                        PlayerInfo.getInventoryItems(playerId, con).values().stream()
                                .filter(i -> i.getExpiration().isUsage() && i.isSelected())
                                .forEach(i -> {
                                    Soda bonusOne = Soda.fromId(i.getBonusOne());

                                    if (bonusOne != null) {
                                        bonusOne.applyBonus(reward, experience, points);
                                    }

                                    Soda bonusTwo = Soda.fromId(i.getBonusTwo());

                                    if (bonusTwo != null) {
                                        bonusTwo.applyBonus(reward, experience, points);
                                    }
                                });
                    }

                    // Avoid the player to earn more experience than the limit
                    experience.mult(Configuration.getInt("game.rewards.exp"));

                    int currentExp = PlayerInfo.getExperience(pr.getPlayerId(), con);

                    if (currentExp + experience.get() > TableManager.EXPERIENCE_LIMIT) {
                        experience.set(TableManager.EXPERIENCE_LIMIT - currentExp);
                    }

                    // Update the definitive experience and points rewards for this player
                    pr.setExperience(experience.get());
                    pr.setPoints(points.get() * Configuration.getInt("game.rewards.point"));

                    // Add the experience and points earned to the player
                    PlayerInfo.sumRewards(pr.getExperience(), pr.getPoints(), playerId, con);

                    // Check if player did level up and apply level up operations if needed
                    short levels = CharacterManager.checkExperience(playerId,
                            levelCache.getPlayerLevel(playerId, con),
                            currentExp + experience.get(), con);

                    finishedQuest = QuestManager.checkQuests(playerId, result, playerTeam, con);

                    room.sendBroadcast(MessageBuilder.updateRoomPlayer(playerId, con));
                    room.sendBroadcast(MessageBuilder.playerBonusStats(playerId, con));

                    if (levels > 0) {
                        playerSession.sendAndFlush(MessageBuilder.playerStats(playerId, con));
                    }
                }

                playerSession.sendAndFlush(MessageBuilder.playerProgress(playerId,
                        finishedQuest, con));
            });

            // If the match was not finished manually, or was finished during golden goal
            if (gameCountdown <= 0) {
                final long delay = DateUtils.currentTimeMillis() - startTime;
                final int minDelay = 1000;

                // This fixed delay is necessary to avoid golden goal bug
                if (delay < minDelay) {
                    ThreadUtils.sleep(minDelay - delay);
                }
            }

            // Broadcast match result message after calculating rewards
            result.getPlayers().stream().forEach(pr ->
                            room.getPlayers().get(pr.getPlayerId()).sendAndFlush(
                                    MessageBuilder.matchResult(result, pr, room, con))
            );

            // Send match result message to observers players in the room
            if (room.getObservers().size() > 0) {
                // Because observer players does not count in stats,
                // we pass an empty PlayerResult instance
                ServerMessage observerMsg = MessageBuilder.matchResult(result,
                        new PlayerResult(), room, con);

                room.getObservers().stream().forEach(o ->
                        room.getPlayers().get(o).sendAndFlush(observerMsg));
            }

            result.getPlayers().stream().forEach(pr -> {
                int playerId = pr.getPlayerId();

                // If match was not in training mode, update player's history
                if (room.getTrainingFactor() > 0) {
                    TeamResult teamResult = room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            result.getRedTeam() : result.getBlueTeam();

                    MatchRewards.updatePlayerHistory(pr, teamResult,
                            result.getMom(), con);

                    if (pr.getExperience() > 0 || pr.getPoints() > 0) {
                        MutableBoolean expired = new MutableBoolean(false);

                        // Decrease by 1 the remain usage of usage items
                        PlayerInfo.getInventoryItems(playerId, con).values().stream()
                                .filter(i -> i.getExpiration().isUsage() && i.isSelected())
                                .forEach(item -> {
                                    item.sumUsages((short) -1);
                                    PlayerInfo.setInventoryItem(item, playerId, con);

                                    if (item.getUsages() <= 0) {
                                        expired.set(true);
                                    }
                                });

                        if (expired.get()) {
                            CharacterManager.sendItemList(room.getPlayers().get(playerId));
                        }
                    }
                }
            });

            room.getConfirmedPlayers().clear();
        } catch (SQLException ignored) {}
    }

    public static void unknown1(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            room.setState(RoomState.WAITING);
            room.sendBroadcast(MessageBuilder.unknown1());
        }
    }

    public static void unknown2(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        if (session.getRoomId() == roomId) {
            Room room = RoomManager.getRoomById(roomId);

            room.sendBroadcast(MessageBuilder.unknown2());

            if (GameEvents.isGoldenTime() || GameEvents.isClubTime()) {
                room.sendBroadcast(MessageBuilder.nextTip("", (byte) 0));
            }
        }
    }

    public static void cancelLoading(Session session, ClientMessage msg) {
        int roomId = msg.readShort();

        Room room = RoomManager.getRoomById(roomId);

        if (room != null && room.state() == RoomState.LOADING &&
                room.getHost() == session.getPlayerId()) {
            room.setState(RoomState.WAITING);
            room.sendBroadcast(MessageBuilder.cancelLoading());
        }
    }
}
