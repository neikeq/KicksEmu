package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.config.Constants;
import com.neikeq.kicksemu.game.characters.types.PlayerHistory;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.QuestState;
import com.neikeq.kicksemu.game.chat.MessageType;
import com.neikeq.kicksemu.game.clubs.ClubInfo;
import com.neikeq.kicksemu.game.clubs.MemberInfo;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.match.MatchResult;
import com.neikeq.kicksemu.game.rooms.match.PlayerResult;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.sessions.AuthResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.game.users.UserSettings;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.storage.MySqlManager;
import com.neikeq.kicksemu.storage.SqlUtils;
import com.neikeq.kicksemu.utils.DateUtils;
import com.neikeq.kicksemu.utils.GameEvents;
import com.neikeq.kicksemu.utils.Strings;

import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MessageBuilder {
    
    public static ServerMessage certifyLogin(int sessionId, int userId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_LOGIN);

        MessageUtils.appendResult(result, msg);

        if (result == AuthResult.SUCCESS) {
            msg.append(sessionId);

            UserSettings settings = UserInfo.getSettings(userId);

            msg.append(settings.getCamera());
            msg.append(settings.getShadows());
            msg.append(settings.getNames());
            msg.append(settings.getVolEffects());
            msg.append(settings.getVolMusic());
            msg.append(settings.getInvites());
            msg.append(settings.getWhispers());
            msg.append(settings.getCountry());

            msg.append(DateUtils.dateToString(UserInfo.getLastCharDeletion(userId)), 19);
        } else {
            // Request the client to close the connection
            msg.write(0, (short)-1);
        }

        return msg;
    }

    public static ServerMessage instantLogin(int sessionId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_LOGIN);

        MessageUtils.appendResult(result, msg);

        msg.append(sessionId);

        return msg;
    }

    public static ServerMessage instantExit() {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        MessageUtils.appendResult((byte)0, msg);

        msg.appendZeros(2);

        // Request the client to close the connection
        msg.write(0, (short) -1);

        return msg;
    }

    public static ServerMessage certifyExit() {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_EXIT);

        MessageUtils.appendResult((byte) 0, msg);

        // Request the client to close the connection
        msg.write(0, (short) -1);

        return msg;
    }

    public static ServerMessage characterInfo(int playerId, int ownerId,
                                              short slot, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.CHARACTER_INFO);

        boolean blocked = PlayerInfo.isBlocked(playerId, con);

        MessageUtils.appendResult((byte) (blocked ? 254 : 0), msg);

        if (!blocked) {
            msg.append(ownerId);
            msg.append(playerId);
            msg.append(slot);
            msg.append(PlayerInfo.getName(playerId, con), 15);

            MessageUtils.appendQuestInfo(playerId, msg, con);
            MessageUtils.appendTutorialInfo(playerId, msg, con);

            msg.appendZeros(3);

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.appendZeros(2);
            msg.append(PlayerInfo.getAnimation(playerId, con));
            msg.append(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.append(PlayerInfo.getPosition(playerId, con));
            msg.appendZeros(6);

            MessageUtils.appendStats(playerId, msg, con);

            msg.appendZeros(4);

            MessageUtils.appendItemsInUse(playerId, msg, con);
        }

        return msg;
    }

    public static ServerMessage createCharacter(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage choiceCharacter(int characterId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CHOICE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(characterId);
        }

        return msg;
    }

    public static ServerMessage removeCharacter(int characterId, String date, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.REMOVE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        msg.append(characterId);
        msg.append(date, 20);

        return msg;
    }

    public static ServerMessage serverList(List<Short> servers, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_LIST);

        MessageUtils.appendResult(result, msg);

        msg.append((short) servers.size());

        for (short serverId : servers) {
            final String query = "SELECT name, online, max_users, connected_users, " +
                    "address, port FROM servers WHERE id = ? LIMIT 1";

            try (Connection con = MySqlManager.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setInt(1, serverId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.append(serverId);
                        msg.append(rs.getString("name"), 30);
                        msg.append(rs.getBoolean("online"));
                        msg.append(rs.getShort("max_users"));
                        msg.append(rs.getShort("connected_users"));
                        msg.append(rs.getString("address"), 16);
                        msg.append(rs.getShort("port"));
                        msg.appendZeros(20);
                    }
                }
            } catch (SQLException ignored) {}
        }

        return msg;
    }

    public static ServerMessage serverInfo(short serverId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_INFO);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            final String query = "SELECT type, min_level, max_level, address, port " +
                    "FROM servers WHERE id = ? LIMIT 1";

            try (Connection con = MySqlManager.getConnection();
                 PreparedStatement stmt = con.prepareStatement(query)) {
                stmt.setInt(1, serverId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        msg.append(serverId);
                        msg.append(ServerType.valueOf(rs.getString("type")).toShort());
                        msg.append(rs.getShort("min_level"));
                        msg.append(rs.getShort("max_level"));
                        msg.append(rs.getString("address"), 16);
                        msg.append(rs.getShort("port"));
                    }
                }
            } catch (SQLException ignored) {}
        }

        return msg;
    }

    public static ServerMessage upgradeCharacter(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPGRADE_CHARACTER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage updateTutorial(byte dribbling, byte passing, byte shooting,
                                               byte defense, int reward, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_TUTORIAL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(dribbling);
            msg.append(passing);
            msg.append(shooting);
            msg.append(defense);
            msg.append(reward);
        }

        return msg;
    }

    public static ServerMessage gameLogin(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.GAME_LOGIN);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage gameExit(InetSocketAddress clientIp, int characterId) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        MessageUtils.appendResult((byte)0, msg);

        msg.append(characterId);
        msg.append(clientIp.getAddress().getHostAddress(), 16);
        msg.appendZeros(2);

        // Request the client to close the connection
        msg.write(0, (short) -1);

        return msg;
    }

    public static ServerMessage udpConfirm(boolean result) {
        ServerMessage msg = new ServerMessage(MessageId.UDP_CONFIRM);

        MessageUtils.appendResult((byte) (result ? 0 : 253), msg);

        return msg;
    }

    public static ServerMessage playerInfo(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_INFO);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            int clubId = MemberInfo.getClubId(playerId, con);

            msg.append(playerId);
            msg.appendZeros(54);
            msg.append(PlayerInfo.getName(playerId, con), 15);
            msg.append(ClubInfo.getName(clubId), 15);
            msg.append(PlayerInfo.getStatusMessage(playerId, con), 35);

            MessageUtils.appendQuestInfo(playerId, msg, con);
            MessageUtils.appendTutorialInfo(playerId, msg, con);

            msg.appendZeros(24);

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.appendZeros(2);
            msg.append(PlayerInfo.getAnimation(playerId, con));
            msg.append(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.append(PlayerInfo.getPosition(playerId, con));
            msg.appendZeros(6);

            // Stats
            MessageUtils.appendStats(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);

            // History
            MessageUtils.appendHistory(playerId, msg, con);
            MessageUtils.appendHistoryMonth(playerId, msg, con);

            // Ranking
            MessageUtils.appendRanking(playerId, msg, con);
            MessageUtils.appendRankingLastMonth(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            MessageUtils.appendClubUniform(clubId, msg, con);
        }

        return msg;
    }

    public static ServerMessage itemList(Map<Integer, Item> items, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ITEM_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append((short)items.size());

            for (Item item : items.values()) {
                MessageUtils.appendInventoryItem(item, msg);
            }
        }

        return msg;
    }

    public static ServerMessage trainingList(Map<Integer, Training> trainings, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.TRAINING_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append((short)trainings.size());

            for (Training training : trainings.values()) {
                MessageUtils.appendInventoryTraining(training, msg);
            }
        }

        return msg;
    }

    public static ServerMessage skillList(Map<Integer, Skill> skills, byte slots, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SKILL_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(slots);
            msg.append((short)skills.size());

            for (Skill skill : skills.values()) {
                MessageUtils.appendInventorySkill(skill, msg);
            }
        }

        return msg;
    }

    public static ServerMessage celebrationList(Map<Integer, Celebration> celebs, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CELEBRATION_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append((short)celebs.size());

            for (Celebration celebration : celebs.values()) {
                MessageUtils.appendInventoryCelebration(celebration, msg);
            }
        }

        return msg;
    }

    public static ServerMessage friendList(List<Integer> friends, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.FRIENDS_LIST);

        MessageUtils.appendResult((byte)0, msg);

        msg.append(page);

        for (int friendId : friends) {
            msg.append(friendId);
            msg.append(PlayerInfo.getName(friendId), 15);
            msg.append(PlayerInfo.getLevel(friendId));
            msg.append((byte)PlayerInfo.getPosition(friendId));

            byte status;
            short server = 0;
            short location = 0;
            int userId = PlayerInfo.getOwner(friendId);

            if (!ServerManager.isPlayerConnected(friendId)) {
                server = UserInfo.getServer(userId);

                status = (byte)(server > 0 && UserInfo.getOnline(userId) == friendId ? 1 : 0);
            } else {
                status = 2;
            }

            switch (status) {
                case 1:
                    location = server;
                    break;
                case 2:
                    location = (short)ServerManager.getSessionById(friendId).getRoomId();
                    break;
                case 3:
                    location = (short)ServerManager.getSessionById(friendId).getRoomId();
                    break;
                default:
            }

            msg.append(status);
            msg.append(location);
        }

        return msg;
    }

    public static ServerMessage friendRequest(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.FRIEND_REQUEST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(playerId);
            msg.append(PlayerInfo.getName(playerId), 15);
        }

        return msg;
    }

    public static ServerMessage friendResponse(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.FRIEND_RESPONSE);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage deleteFriend(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DELETE_FRIEND);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage clubInfo(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        int clubId = MemberInfo.getClubId(playerId);

        msg.append(ClubInfo.getName(clubId), 15);
        msg.append(ClubInfo.getMembersCount(clubId));
        msg.append(ClubInfo.getMembersLimit(clubId));

        int manager = ClubInfo.getManager(clubId);
        msg.append(PlayerInfo.getName(manager), 15);

        List<Integer> captains = ClubInfo.getCaptains(clubId);

        for (int i = 0; i < 2; i++) {
            if (i >= captains.size()) {
                msg.appendZeros(15);
            } else {
                msg.append(PlayerInfo.getName(captains.get(i)), 15);
            }
        }

        msg.append(clubId > 0 ? ClubInfo.getClubPoints(clubId) : 0);

        return msg;
    }

    public static ServerMessage clubMembers(int playerId, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.CLUB_MEMBERS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(page);

        int clubId = MemberInfo.getClubId(playerId);

        List<Integer> membersForPage = ClubInfo.getMembers(clubId, page * 10);

        membersForPage.forEach(memberId -> {
            msg.append(memberId);
            msg.append(PlayerInfo.getName(memberId), 15);
            msg.append(PlayerInfo.getLevel(memberId));
            msg.append((byte) PlayerInfo.getPosition(memberId));

            byte status;
            short server = 0;
            short location = 0;
            int userId = PlayerInfo.getOwner(memberId);

            if (!ServerManager.isPlayerConnected(memberId)) {
                server = UserInfo.getServer(userId);

                status = (byte) (server > 0 && UserInfo.getOnline(userId) == memberId ? 1 : 0);
            } else {
                status = 2;
            }

            switch (status) {
                case 1:
                    location = server;
                    break;
                case 2:
                    location = (short) ServerManager.getSessionById(memberId).getRoomId();
                    break;
                case 3:
                    location = (short) ServerManager.getSessionById(memberId).getRoomId();
                    break;
                default:
            }

            msg.append(status);
            msg.append(location);
        });

        return msg;
    }

    public static ServerMessage ignoredList(List<Integer> ignoredPlayers, byte page) {
        ServerMessage msg = new ServerMessage(MessageId.IGNORED_LIST);

        MessageUtils.appendResult((byte)0, msg);

        msg.append(page);

        for (int playerId : ignoredPlayers) {
            msg.append(playerId);
            msg.append(PlayerInfo.getName(playerId), 15);
            msg.append((byte)PlayerInfo.getPosition(playerId));
        }

        return msg;
    }

    public static ServerMessage blockPlayer(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.BLOCK_PLAYER);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(playerId);
            msg.append(PlayerInfo.getName(playerId), 15);
        }

        return msg;
    }

    public static ServerMessage unblockPlayer(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UNBLOCK_PLAYER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage statusMessage(String statusMessage, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.STATUS_MESSAGE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(statusMessage, statusMessage.length());
        }

        return msg;
    }

    public static ServerMessage roomList(Map<Integer, Room> rooms, short page, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(page);

            for (Room room : rooms.values()) {
                // Visibility. Hide if player is moderator.
                msg.append((byte)room.getType().toInt());
                msg.append(room.isPlaying());
                msg.append((short)room.getId());
                msg.append(room.getName(), 46);
                msg.append(room.getMinLevel());
                msg.append(room.getMaxLevel());
                msg.append((byte)room.getMaxSize().toInt());
                msg.append(room.getCurrentSize());

                // Red team positions

                int i = 0;

                for (short position : room.getRedTeamPositions()) {
                    msg.append((byte)position);
                    i++;
                }

                // Fill the remain spaces if red team is not full
                msg.appendZeros(5 - i);

                // Blue team positions

                i = 0;

                for (short position : room.getBlueTeamPositions()) {
                    msg.append((byte)position);
                    i++;
                }

                // Fill the remain spaces if blue team is not full
                msg.appendZeros(5 - i);
            }
        }

        return msg;
    }

    public static ServerMessage createRoom(short roomId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(roomId);
        }

        return msg;
    }

    public static ServerMessage joinRoom(Room room, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.JOIN_ROOM);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.append((short)room.getId());

            RoomTeam team = room.getPlayerTeam(playerId);
            short teamIndex = team != null ? (short)team.toInt() : -1;
            msg.append(teamIndex);
            msg.append(teamIndex);
        }

        return msg;
    }

    public static ServerMessage quickJoinRoom(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.QUICK_JOIN_ROOM);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage nextTip(String tip, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.NEXT_TIP);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(GameEvents.isGoldenTime());
            msg.append(GameEvents.isClubTime());

            msg.append(tip, tip.length() > 120 ? 120 : tip.length());
        }

        return msg;
    }

    public static ServerMessage roomInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        if (room != null) {
            msg.append((byte)room.getType().toInt());
            msg.append((short)room.getId());
            msg.append(room.getName(), 45);
            msg.append(room.getPassword(), 5);
            msg.append(room.getMaster());
            msg.append((byte)room.getRoomMode().toInt());
            msg.append(room.getMinLevel());
            msg.append(room.getMaxLevel());
            msg.append((short)room.getMap().toInt());
            msg.append((short)room.getBall().toInt());
            msg.append((byte)room.getMaxSize().toInt());
        }

        return msg;
    }

    public static ServerMessage roomPlayerInfo(Session session, Room room, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_PLAYER_INFO);

        if (session != null) {
            int playerId = session.getPlayerId();
            int ownerId = PlayerInfo.getOwner(playerId, con);
            int clubId = MemberInfo.getClubId(playerId, con);

            msg.append(true, 2);
            msg.append(playerId);
            msg.append(PlayerInfo.getName(playerId, con), 15);
            msg.append(ClubInfo.getName(clubId, con), 15);
            msg.append((short)(room.getRedTeam().contains(playerId) ? 0 : 1));
            msg.append(room.isObserver(playerId));
            msg.append(false, 2);
            msg.append(UserInfo.getSettings(ownerId).getCountry());
            msg.append(session.getPing() < 100, 2);
            msg.append(session.getRemoteAddress().getAddress().getHostAddress(), 16);
            msg.append((short) session.getUdpPort());

            MessageUtils.appendCharacterInfo(playerId, msg, con);

            msg.appendZeros(2);
            msg.append(PlayerInfo.getAnimation(playerId, con));
            msg.append(PlayerInfo.getFace(playerId, con));

            MessageUtils.appendDefaultClothes(playerId, msg, con);

            msg.append(PlayerInfo.getPosition(playerId, con));
            msg.appendZeros(1);
            msg.append(clubId > 0); // is club member
            msg.append((byte) MemberInfo.getRole(playerId, con).toInt());
            msg.appendZeros(3);
            msg.append((byte) 0);
            msg.append((byte) 0);
            msg.append((byte) 0);

            // Stats
            MessageUtils.appendStats(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);

            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            MessageUtils.appendClubUniform(clubId, msg, con);
            MessageUtils.appendInventorySkillsInUse(playerId, msg, con);
            MessageUtils.appendInventoryCelebrationsInUse(playerId, msg, con);
        }

        return msg;
    }

    public static ServerMessage leaveRoom(int playerId, RoomLeaveReason reason) {
        return leaveRoom(playerId, reason.toShort());
    }

    public static ServerMessage leaveRoom(int playerId, short reason) {
        ServerMessage msg = new ServerMessage(MessageId.LEAVE_ROOM);

        msg.append(reason);
        msg.append(playerId);

        return msg;
    }

    public static ServerMessage roomMaster(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MASTER);

        msg.appendZeros(2);
        msg.append(playerId);

        return msg;
    }

    public static ServerMessage swapTeam(int playerId, RoomTeam newTeam) {
        ServerMessage msg = new ServerMessage(MessageId.SWAP_TEAM);

        MessageUtils.appendResult((byte)0, msg);

        msg.append(playerId);
        msg.appendZeros(2);
        msg.append((short) newTeam.toInt());

        return msg;
    }

    public static ServerMessage roomMap(short map) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MAP);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(map);

        return msg;
    }

    public static ServerMessage roomBall(short ball) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_BALL);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(ball);

        return msg;
    }

    public static ServerMessage roomSettings(Room room, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_SETTINGS);

        MessageUtils.appendResult(result, msg);

        if (room != null) {
            msg.append((short)room.getType().toInt());
            msg.append(room.getName(), 45);
            msg.append(room.getPassword(), 4);
            msg.appendZeros(1);
            msg.append((byte)room.getRoomMode().toInt());
            msg.append(room.getMinLevel());
            msg.append(room.getMaxLevel());
            msg.append((byte)room.getMaxSize().toInt());
        }

        return msg;
    }

    public static ServerMessage kickPlayer(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.KICK_PLAYER);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage lobbyList(Integer[] players, byte page,
                                          byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.LOBBY_LIST);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(page);

            try {
                Connection connection = con.length > 0 ? con[0] : MySqlManager.getConnection();

                String array = Strings.repeatAndSplit("?", ", ", players.length);

                final String query = "SELECT name, level, position, status_message FROM characters" +
                        " WHERE id IN(" + array + ") ORDER BY FIELD(id, " + array + ")";

                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    SqlUtils.repeatSetInt(stmt, players);
                    SqlUtils.repeatSetInt(stmt, players.length + 1, players);

                    try (ResultSet rs = stmt.executeQuery()) {
                        for (int i = 0; rs.next(); i++) {
                            msg.append(true);
                            msg.append(players[i]);
                            msg.append(rs.getString("name"), 15);
                            msg.append(rs.getShort("level"));
                            msg.append((byte) rs.getShort("position"));
                            msg.append(rs.getString("status_message"), 35);
                        }
                    }
                } finally {
                    if (con.length <= 0) {
                        connection.close();
                    }
                }
            } catch (SQLException ignored) {
                System.out.println(ignored.getMessage());
            }
        }

        return msg;
    }

    public static ServerMessage invitePlayer(byte result, Room room, String name) {
        ServerMessage msg = new ServerMessage(MessageId.INVITE_PLAYER);

        MessageUtils.appendResult(result, msg);

        if (result == 0 && room != null) {
            msg.append(name, 15);
            msg.append((short) room.getId());
            msg.append(room.getPassword(), 5);
        }

        return msg;
    }

    public static ServerMessage chatMessage(MessageType messageType, String message) {
        return chatMessage(0, "", messageType, message);
    }

    public static ServerMessage chatMessage(int playerId, String name,
                                            MessageType messageType, String message) {
        ServerMessage msg = new ServerMessage(MessageId.CHAT_MESSAGE);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(playerId);
        msg.append(name, 15);
        msg.append((byte)messageType.toInt());
        msg.append(message, message.length());

        return msg;
    }

    public static ServerMessage setObserver(int playerId, boolean observer) {
        ServerMessage msg = new ServerMessage(MessageId.SET_OBSERVER);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(playerId);
        msg.append(observer, 2);

        return msg;
    }

    public static ServerMessage startCountDown(byte type) {
        ServerMessage msg = new ServerMessage(MessageId.START_COUNT_DOWN);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(type);

        return msg;
    }

    public static ServerMessage hostInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.HOST_INFO);

        MessageUtils.appendResult((byte) 0, msg);

        Session session = ServerManager.getSessionById(room.getHost());

        msg.append(room.getHost());
        msg.append(session.getRemoteAddress().getAddress().getHostAddress(), 16);
        msg.append((short) session.getUdpPort());
        msg.append(room.isTraining());
        msg.appendZeros(4);

        byte hostIndex = (byte)(room.getPlayerTeam(room.getHost()) == RoomTeam.RED ?
                room.getRedTeam() : room.getBlueTeam()).indexOf(room.getHost());

        msg.append(room.getHost());
        msg.append(hostIndex);

        room.getPlayers().keySet().stream()
                .filter(playerId -> playerId != room.getHost())
                .forEach(playerId -> {
                    byte playerIndex = (byte) (room.getPlayerTeam(playerId) == RoomTeam.RED ?
                            room.getRedTeam() : room.getBlueTeam()).indexOf(playerId);

                    msg.append(playerId);
                    msg.append(playerIndex);
                });

        return msg;
    }

    public static ServerMessage countDown(short count) {
        ServerMessage msg = new ServerMessage(MessageId.COUNT_DOWN);

        msg.append(count);

        return msg;
    }

    public static ServerMessage cancelCountDown() {
        ServerMessage msg = new ServerMessage(MessageId.CANCEL_COUNT_DOWN);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage matchLoading(int playerId, int roomId, short status) {
        ServerMessage msg = new ServerMessage(MessageId.MATCH_LOADING);

        msg.append(playerId);
        msg.append((short)roomId);
        msg.append(status);

        return msg;
    }

    public static ServerMessage playerReady(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_READY);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage cancelLoading() {
        ServerMessage msg = new ServerMessage(MessageId.CANCEL_LOADING);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage startMatch(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.START_MATCH);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage matchResult(MatchResult result, PlayerResult playerResult,
                                            Room room, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.MATCH_RESULT);

        MessageUtils.appendResult((byte) 0, msg);

        if (result != null && playerResult != null && room != null) {
            msg.append(result.getMom());

            result.getRedTeam().appendResult(msg);
            result.getBlueTeam().appendResult(msg);

            result.getPlayers().stream().forEach(pr -> pr.appendResult(msg));
            msg.appendZeros(40 * (10 - result.getPlayers().size()));

            msg.append(result.getCountdown());
            msg.append(result.isGoldenTime());
            msg.append(result.isExperience());
            msg.append(result.isExperience()); // Point

            msg.append(playerResult.getExperience());
            msg.append(playerResult.getPoints());

            MessageUtils.appendMatchHistory(playerResult, room, result, msg, con);
        }

        return msg;
    }

    public static ServerMessage unknown1() {
        ServerMessage msg = new ServerMessage(MessageId.UNKNOWN1);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage unknown2() {
        ServerMessage msg = new ServerMessage(MessageId.UNKNOWN2);

        MessageUtils.appendResult((byte) 0, msg);

        return msg;
    }

    public static ServerMessage updateRoomPlayer(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_ROOM_PLAYER);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(playerId);
        msg.append(true);

        MessageUtils.appendCharacterInfo(playerId, msg, con);
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage playerBonusStats(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_BONUS_STATS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(playerId);

        MessageUtils.appendStatsBonus(playerId, msg, con);

        return msg;
    }

    public static ServerMessage purchaseItem(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            msg.append(PlayerInfo.getSkillSlots(playerId, con));
        }

        return msg;
    }

    public static ServerMessage resellItem(int playerId, int inventoryId, int refund,
                                           byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.RESELL_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsBonus(playerId, msg, con);
            MessageUtils.appendInventoryItemsInUse(playerId, msg, con);
            msg.append(PlayerInfo.getSkillSlots(playerId, con));
            msg.appendZeros(3);
            msg.append(refund);
            msg.append(inventoryId);
        }

        return msg;
    }

    public static ServerMessage activateItem(int inventoryId, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            MessageUtils.appendInventoryItemsInUse(playerId, msg);
            msg.append(inventoryId);
        }

        return msg;
    }

    public static ServerMessage deactivateItem(int inventoryId, int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            MessageUtils.appendInventoryItemsInUse(playerId, msg);
            msg.append(inventoryId);
        }

        return msg;
    }

    public static ServerMessage mergeItem(int playerId, int inventoryId,
                                          short usages, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.MERGE_ITEM);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendStatsBonus(playerId, msg);
            msg.append(inventoryId);
            msg.append(usages);
        }

        return msg;
    }

    public static ServerMessage purchaseLearn(int playerId, Training learn,
                                              byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_LEARN);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendStatsTraining(playerId, msg, con);
            MessageUtils.appendInventoryTraining(learn, msg);
        }

        return msg;
    }

    public static ServerMessage purchaseSkill(int playerId, Skill skill,
                                              byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendInventorySkill(skill, msg);
        }

        return msg;
    }

    public static ServerMessage activateSkill(int inventoryId, byte index, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(inventoryId);
            msg.append(index);
        }

        return msg;
    }

    public static ServerMessage deactivateSkill(int skillId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_SKILL);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(skillId);
        }

        return msg;
    }

    public static ServerMessage purchaseCele(int playerId, Celebration cele,
                                             byte result, Connection con) {
        ServerMessage msg = new ServerMessage(MessageId.PURCHASE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            MessageUtils.appendCharacterInfo(playerId, msg, con);
            MessageUtils.appendInventoryCelebration(cele, msg);
        }

        return msg;
    }

    public static ServerMessage activateCele(int inventoryId, byte index, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ACTIVATE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(inventoryId);
            msg.append(index);
        }

        return msg;
    }

    public static ServerMessage deactivateCele(int inventoryId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.DEACTIVATE_CELE);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(inventoryId);
        }

        return msg;
    }

    public static ServerMessage tcpPing() {
        return new ServerMessage(MessageId.TCP_PING);
    }

    public static ServerMessage updateSettings(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_SETTINGS);

        MessageUtils.appendResult(result, msg);

        return msg;
    }

    public static ServerMessage playerDetails(int playerId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_DETAILS);

        MessageUtils.appendResult(result, msg);

        if (result == 0) {
            msg.append(playerId);
            msg.append(PlayerInfo.getName(playerId), 15);
            msg.append(PlayerInfo.getPosition(playerId));
            msg.append(PlayerInfo.getLevel(playerId));
            msg.append(ClubInfo.getName(MemberInfo.getClubId(playerId)), 15);

            PlayerHistory history = PlayerInfo.getHistory(playerId);
            PlayerHistory monthHistory = PlayerInfo.getMonthHistory(playerId);

            int historyLoses = history.getMatches() - (history.getWins() + history.getDraws());
            short historyMonthLoses = (short) (monthHistory.getMatches() -
                    (monthHistory.getWins() + monthHistory.getDraws()));

            // Matches history
            msg.append(history.getWins());
            msg.append(history.getDraws());
            msg.append(historyLoses);
            msg.append((short) monthHistory.getWins());
            msg.append((short) monthHistory.getDraws());
            msg.append(historyMonthLoses);

            // TODO Fix History and Last Month History writing to PlayerDetails message

            // History
            msg.append((short) history.getMatches());
            msg.append((short) history.getWins());
            msg.append((short) history.getDraws());
            msg.append((short) history.getMom());
            msg.append((short) history.getValidGoals());
            msg.append((short) history.getValidAssists());
            msg.append((short) history.getValidInterception());
            msg.append((short) history.getValidShooting());
            msg.append((short) history.getValidStealing());
            msg.append((short) history.getValidTackling());
            msg.appendZeros(2);
            msg.append((short) history.getShooting());
            msg.append((short) history.getStealing());
            msg.append((short) history.getTackling());
            msg.appendZeros(2);
            msg.append((short) history.getTotalPoints());

            // History Last Month
            msg.append((short) monthHistory.getMatches());
            msg.append((short) monthHistory.getWins());
            msg.append((short) monthHistory.getDraws());
            msg.append((short) monthHistory.getMom());
            msg.append((short) monthHistory.getValidGoals());
            msg.append((short) monthHistory.getValidAssists());
            msg.append((short) monthHistory.getValidInterception());
            msg.append((short) monthHistory.getValidShooting());
            msg.append((short) monthHistory.getValidStealing());
            msg.append((short) monthHistory.getValidTackling());
            msg.appendZeros(2);
            msg.append((short) monthHistory.getShooting());
            msg.append((short) monthHistory.getStealing());
            msg.append((short) monthHistory.getTackling());
            msg.appendZeros(2);
            msg.append((short) monthHistory.getTotalPoints());

            // TODO Add club info
        }

        return msg;
    }

    public static ServerMessage addStatsPoints(int playerId, byte result, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.ADD_STATS_POINTS);

        MessageUtils.appendResult(result, msg);

        msg.append(playerId);

        msg.append(PlayerInfo.getStatsPoints(playerId));
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage playerProgress(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_PROGRESS);

        MessageUtils.appendResult((byte)0, msg);

        QuestState questState = PlayerInfo.getQuestState(playerId, con);

        msg.append(questState.getCurrentQuest());
        msg.append(PlayerInfo.getLevel(playerId, con));
        msg.append(questState.getRemainMatches());
        msg.append(PlayerInfo.getPoints(playerId, con));

        return msg;
    }

    public static ServerMessage playerStats(int playerId, Connection ... con) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_STATS);

        MessageUtils.appendResult((byte) 0, msg);

        msg.append(PlayerInfo.getStatsPoints(playerId, con));
        MessageUtils.appendStats(playerId, msg, con);

        return msg;
    }

    public static ServerMessage udpPing(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.UDP_PING);

        msg.write(Constants.TARGET_ID_INDEX, playerId);

        return msg;
    }
}
