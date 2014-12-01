package com.neikeq.kicksemu.network.packets.out;

import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.chat.ChatMessageType;
import com.neikeq.kicksemu.game.inventory.Celebration;
import com.neikeq.kicksemu.game.inventory.Item;
import com.neikeq.kicksemu.game.inventory.Skill;
import com.neikeq.kicksemu.game.inventory.Training;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.RoomLeaveReason;
import com.neikeq.kicksemu.game.rooms.RoomTeam;
import com.neikeq.kicksemu.game.servers.ServerInfo;
import com.neikeq.kicksemu.game.sessions.AuthenticationResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserInfo;
import com.neikeq.kicksemu.game.users.UserSettings;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.utils.DateUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

public class MessageBuilder {

    // TODO move all reason/response appends to a single method
    
    public static ServerMessage certifyLogin(UserInfo user, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_LOGIN);

        msg.append(result);
        msg.append((byte)(result == AuthenticationResult.SUCCESS ? 0 : 255));

        if (result == AuthenticationResult.SUCCESS) {
            msg.append(user.getId());

            UserSettings settings = user.getSettings();

            msg.append(settings.getCamera());
            msg.append(settings.getShadows());
            msg.append(settings.getNames());
            msg.append(settings.getVolEffects());
            msg.append(settings.getVolMusic());
            msg.append(settings.getInvites());
            msg.append(settings.getWhispers());
            msg.append(settings.getCountry());

            msg.append(DateUtils.dateToString(user.getLastCharDeletion()), 19);
        } else {
            // Request the client to close the connection
            msg.write(0, (short)-1);
        }

        return msg;
    }

    public static ServerMessage instantLogin(int accountId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_LOGIN);

        msg.append(result);
        msg.append((byte)(result == AuthenticationResult.SUCCESS ? 0 : 255));
        msg.append(accountId);

        return msg;
    }

    public static ServerMessage certifyExit(boolean result) {
        ServerMessage msg = new ServerMessage(MessageId.CERTIFY_EXIT);

        msg.append((byte) 0);
        msg.append((byte) (result ? 0 : 255));

        if (result) {
            // Request the client to close the connection
            msg.write(0, (short)-1);
        }

        return msg;
    }

    public static ServerMessage instantExit() {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        msg.appendZeros(4);

        // Request the client to close the connection
        msg.write(0, (short)-1);

        return msg;
    }

    public static ServerMessage characterInfo(PlayerInfo player, UserInfo owner, short slot) {
        ServerMessage msg = new ServerMessage(MessageId.CHARACTER_INFO);

        boolean blocked = player.isBlocked();

        msg.append((byte) (blocked ? 254 : 0));
        msg.append((byte) (blocked ? 255 : 0));

        if (!blocked) {
            msg.append(owner.getId());
            msg.append(player.getId());

            msg.append(slot);

            msg.append(player.getName(), 15);

            MessageUtils.appendQuestInfo(player, msg);
            MessageUtils.appendTutorialInfo(player, msg);

            msg.appendZeros(3);

            MessageUtils.appendCharacterInfo(player, owner, msg);

            msg.appendZeros(2);

            msg.append(player.getAnimation());
            msg.append(player.getFace());

            MessageUtils.appendDefaultClothes(player, msg);

            msg.append(player.getPosition());

            msg.appendZeros(6);

            MessageUtils.appendStats(player, msg);

            msg.appendZeros(4);

            MessageUtils.appendItemsInUse(player, msg);
        }

        return msg;
    }

    public static ServerMessage createCharacter(byte response) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_CHARACTER);

        msg.append(response);
        msg.append((byte)(response == 0 ? response : 255));

        return msg;
    }

    public static ServerMessage updateSettings(byte response) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_SETTINGS);

        msg.append(response);
        msg.append((byte)(response == 0 ? response : 255));

        return msg;
    }

    public static ServerMessage removeCharacter(int characterId, String date, byte response) {
        ServerMessage msg = new ServerMessage(MessageId.REMOVE_CHARACTER);

        msg.append(response);
        msg.append((byte)(response == 0 ? response : 255));
        msg.append(characterId);
        msg.append(date, 20);

        return msg;
    }

    public static ServerMessage choiceCharacter(int characterId, byte response) {
        ServerMessage msg = new ServerMessage(MessageId.CHOICE_CHARACTER);

        msg.append(response);
        msg.append((byte)(response == 0 ? 0 : 255));

        if (response == 0) {
            msg.append(characterId);
        }

        return msg;
    }

    public static ServerMessage serverList(List<ServerInfo> servers, byte response) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_LIST);

        msg.append(response);
        msg.append((byte)(response == 0 ? 0 : 255));

        msg.append((short)servers.size());

        for (ServerInfo server : servers) {
            msg.append(server.getId());
            msg.append(server.getName(), 30);
            msg.append(server.isOnline());
            msg.append(server.getMaxUsers());
            msg.append(server.getConnectedUsers());
            msg.append(server.getAddress(), 16);
            msg.append(server.getPort());
            msg.appendZeros(20);
        }

        return msg;
    }

    public static ServerMessage updateTutorial(byte dribbling, byte passing, byte shooting,
                                               byte defense, int reward, byte response) {
        ServerMessage msg = new ServerMessage(MessageId.UPDATE_TUTORIAL);

        msg.append(response);
        msg.append((byte)(response == 0 ? 0 : 255));

        if (response == 0) {
            msg.append(dribbling);
            msg.append(passing);
            msg.append(shooting);
            msg.append(defense);
            msg.append(reward);
        }

        return msg;
    }

    public static ServerMessage serverInfo(ServerInfo server, byte response) {
        ServerMessage msg = new ServerMessage(MessageId.SERVER_INFO);

        msg.append(response);
        msg.append((byte)(response == 0 ? 0 : 255));

        if (response == 0) {
            msg.append(server.getId());
            msg.appendZeros(2);
            msg.append(server.getMinLevel());
            msg.append(server.getMaxLevel());
            msg.append(server.getAddress(), 16);
            msg.append(server.getPort());
        }

        return msg;
    }

    public static ServerMessage gameLogin(byte result) {
        ServerMessage msg = new ServerMessage(MessageId.GAME_LOGIN);

        msg.append(result);
        msg.append((byte)(result == AuthenticationResult.SUCCESS ? 0 : 255));

        return msg;
    }

    public static ServerMessage gameExit(InetSocketAddress clientIp, int characterId) {
        ServerMessage msg = new ServerMessage(MessageId.INSTANT_EXIT);

        msg.appendZeros(2);
        msg.append(characterId);
        msg.append(clientIp.getAddress().getHostAddress(), 16);
        msg.appendZeros(2);

        // Request the client to close the connection
        msg.write(0, (short)-1);

        return msg;
    }

    public static ServerMessage udpConfirm(boolean result) {
        ServerMessage msg = new ServerMessage(MessageId.UDP_CONFIRM);

        msg.append((byte)(result ? 0 : 253));
        msg.append((byte)(result ? 0 : 255));

        return msg;
    }

    public static ServerMessage itemList(Map<Integer, Item> items, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ITEM_LIST);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

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

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append((short)trainings.size());

            for (Training training : trainings.values()) {
                MessageUtils.appendInventoryTraining(training, msg);
            }
        }

        return msg;
    }

    public static ServerMessage skillList(Map<Integer, Skill> skills, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.SKILL_LIST);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append((short)skills.size());

            for (Skill skill : skills.values()) {
                MessageUtils.appendInventorySkill(skill, msg);
            }
        }

        return msg;
    }

    public static ServerMessage celebrationList(Map<Integer, Celebration> celebs, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CELEBRATION_LIST);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append((short)celebs.size());

            for (Celebration celebration : celebs.values()) {
                MessageUtils.appendInventoryCelebration(celebration, msg);
            }
        }

        return msg;
    }

    public static ServerMessage playerInfo(PlayerInfo player, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.PLAYER_INFO);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append(player.getId());

            msg.appendZeros(54);

            msg.append(player.getName(), 15);

            // TODO Send club name instead of id string
            msg.append(String.valueOf(player.getClubId()), 15);

            msg.append(player.getStatusMessage(), 35);

            MessageUtils.appendQuestInfo(player, msg);
            MessageUtils.appendTutorialInfo(player, msg);

            msg.appendZeros(24);

            UserInfo owner = new UserInfo(player.getOwner());
            MessageUtils.appendCharacterInfo(player, owner, msg);

            msg.appendZeros(2);

            msg.append(player.getAnimation());
            msg.append(player.getFace());

            MessageUtils.appendDefaultClothes(player, msg);

            msg.append(player.getPosition());
            msg.appendZeros(6);

            // Stats
            MessageUtils.appendStats(player, msg);
            MessageUtils.appendStatsTraining(player, msg);
            MessageUtils.appendStatsBonus(player, msg);

            // History
            MessageUtils.appendHistory(player, msg);
            MessageUtils.appendHistoryLastMonth(player, msg);

            // Ranking
            MessageUtils.appendRanking(player, msg);
            MessageUtils.appendRankingLastMonth(player, msg);

            MessageUtils.appendInventoryItemsInUse(player, msg);

            MessageUtils.appendClubUniform(player.getClubId(), msg);
        }

        return msg;
    }

    public static ServerMessage lobbyList(List<Integer> players, byte page, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.LOBBY_LIST);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append(page);

            for (Integer id : players) {
                PlayerInfo player = new PlayerInfo(id);

                // Visibility. Hide if player is moderator.
                msg.append(!player.isModerator());
                msg.append(id);
                msg.append(player.getName(), 15);
                msg.append(player.getLevel());
                msg.append((byte)player.getPosition());
                msg.append(player.getStatusMessage(), 35);
            }
        }

        return msg;
    }

    public static ServerMessage roomList(Map<Integer, Room> rooms, short page, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_LIST);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

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

                int i = 0;

                for (short position : room.getPositions()) {
                    msg.append((byte)position);
                    i++;
                }

                // Fill the remain spaces if room is not full
                // 10 is the number of bytes needed to be filled
                msg.appendZeros(10 - i);
            }
        }

        return msg;
    }

    public static ServerMessage nextTip(String tip, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.NEXT_TIP);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.appendZeros(2);
            msg.append(tip, 120);
        }

        return msg;
    }

    public static ServerMessage chatMessage(int playerId, String name,
                                            ChatMessageType messageType, String message) {
        ServerMessage msg = new ServerMessage(MessageId.CHAT_MESSAGE);

        msg.appendZeros(2);

        msg.append(playerId);
        msg.append(name, 15);
        msg.append((byte)messageType.toInt());
        msg.append(message, message.length());

        return msg;
    }

    public static ServerMessage changeStatusMessage(String statusMessage, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CHANGE_STATUS_MESSAGE);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append(statusMessage, statusMessage.length());
        }

        return msg;
    }

    public static ServerMessage createRoom(short roomId, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.CREATE_ROOM);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append(roomId);
        }

        return msg;
    }

    public static ServerMessage joinRoom(int roomId, RoomTeam team, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.JOIN_ROOM);

        msg.append(result);
        msg.append((byte)(result == 0 ? 0 : 255));

        if (result == 0) {
            msg.append((short)roomId);
            msg.append((short)(team != null ? team.toInt() : -1));
        }

        return msg;
    }

    public static ServerMessage roomInfo(Room room) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_INFO);

        msg.appendZeros(2);

        if (room != null) {
            msg.append((byte)room.getType().toInt());
            msg.append((short)room.getId());
            msg.append(room.getName(), 45);
            msg.append(room.getPassword(), 4);
            msg.appendZeros(1);
            msg.append(room.getMaster());
            msg.append((byte)room.getGoalkeeperMode().toInt());
            msg.append(room.getMinLevel());
            msg.append(room.getMaxLevel());
            msg.append((short)room.getMap().toInt());
            msg.append((short)room.getBall().toInt());
            msg.append((byte)room.getMaxSize().toInt());
        }

        return msg;
    }

    public static ServerMessage roomPlayerInfo(Session session, Room room) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_PLAYER_INFO);

        if (session != null) {
            PlayerInfo player = session.getPlayerInfo();
            UserInfo owner = new UserInfo(player.getOwner());

            msg.append(true, 2);
            msg.append(player.getId());
            msg.append(player.getName(), 15);
            // TODO Send club name instead of id string
            msg.append(String.valueOf(player.getClubId()), 15);
            msg.append((short)(room.getRedTeam().contains(player.getId()) ? 0 : 1));
            msg.append(room.isObserver(player.getId()));
            msg.appendZeros(2);
            msg.append(owner.getSettings().getCountry());
            msg.appendZeros(2);
            msg.append(session.getRemoteAddress().getAddress().getHostAddress(), 16);
            msg.appendZeros(2);
            MessageUtils.appendCharacterInfo(player, owner, msg);
            msg.appendZeros(2);

            msg.append(player.getAnimation());
            msg.append(player.getFace());

            MessageUtils.appendDefaultClothes(player, msg);

            msg.append(player.getPosition());
            msg.appendZeros(9);

            // Stats
            MessageUtils.appendStats(player, msg);
            MessageUtils.appendStatsTraining(player, msg);
            MessageUtils.appendStatsBonus(player, msg);

            MessageUtils.appendInventoryItemsInUse(player, msg);
            MessageUtils.appendClubUniform(player.getClubId(), msg);
            MessageUtils.appendInventorySkillsInUse(player, msg);
            MessageUtils.appendInventoryCelebrationsInUse(player, msg);
        }

        return msg;
    }

    public static ServerMessage leaveRoom(int playerId, RoomLeaveReason reason) {
        ServerMessage msg = new ServerMessage(MessageId.LEAVE_ROOM);

        msg.append(reason.toShort());
        msg.append(playerId);

        return msg;
    }

    public static ServerMessage roomMaster(int playerId) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MASTER);

        msg.appendZeros(2);
        msg.append(playerId);

        return msg;
    }

    public static ServerMessage roomMap(short map) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_MAP);

        msg.appendZeros(2);

        msg.append(map);

        return msg;
    }

    public static ServerMessage roomBall(short ball) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_BALL);

        msg.appendZeros(2);

        msg.append(ball);

        return msg;
    }

    public static ServerMessage roomSettings(Room room, byte result) {
        ServerMessage msg = new ServerMessage(MessageId.ROOM_SETTINGS);

        msg.append(result);
        msg.append((byte) (result == 0 ? 0 : 255));

        if (room != null) {
            msg.append((short)room.getType().toInt());
            msg.append(room.getName(), 45);
            msg.append(room.getPassword(), 4);
            msg.appendZeros(1);
            msg.append((byte)room.getGoalkeeperMode().toInt());
            msg.append(room.getMinLevel());
            msg.append(room.getMaxLevel());
            msg.append((byte)room.getMaxSize().toInt());
        }

        return msg;
    }
}
