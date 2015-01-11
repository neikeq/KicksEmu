package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.StatusMessage;
import com.neikeq.kicksemu.game.chat.ChatManager;
import com.neikeq.kicksemu.game.datagram.MatchBroadcaster;
import com.neikeq.kicksemu.game.inventory.InventoryManager;
import com.neikeq.kicksemu.game.inventory.shop.Shop;
import com.neikeq.kicksemu.game.misc.friendship.FriendsManager;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredManager;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.rooms.RoomManager;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Authenticator;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameMessageHandler extends MessageHandler {
    private static Map<Integer, MessageEventHandler> events;

    private static List<Integer> certifyEvents;

    public GameMessageHandler() {
        super();

        defineEvents();
        defineCertifyEvents();
    }

    private void defineEvents() {
        events = new HashMap<>();

        events.put(MessageId.GAME_LOGIN, Authenticator::gameLogin);
        events.put(MessageId.UDP_CHECK, (s, msg) -> Authenticator.udpAuthentication(s));
        events.put(MessageId.GAME_EXIT, (s, msg) -> CharacterManager.gameExit(s));
        events.put(MessageId.UDP_CONFIRM, (s, msg) -> Authenticator.udpConfirm(s));
        events.put(MessageId.PLAYER_INFO, (s, msg) -> CharacterManager.playerInfo(s));
        events.put(MessageId.LOBBY_LIST, LobbyManager::lobbyList);
        events.put(MessageId.ROOM_LIST, RoomManager::roomList);
        events.put(MessageId.NEXT_TIP, (s, msg) -> ServerUtils.nextTip(s));
        events.put(MessageId.CHAT_MESSAGE, ChatManager::chatMessage);
        events.put(MessageId.STATUS_MESSAGE, StatusMessage::statusMessage);
        events.put(MessageId.CREATE_ROOM, RoomManager::createRoom);
        events.put(MessageId.JOIN_ROOM, RoomManager::joinRoom);
        events.put(MessageId.QUICK_JOIN_ROOM, (s, msg) -> RoomManager.quickJoinRoom(s));
        events.put(MessageId.LEAVE_ROOM, RoomManager::leaveRoom);
        events.put(MessageId.ROOM_MAP, RoomManager::roomMap);
        events.put(MessageId.ROOM_BALL, RoomManager::roomBall);
        events.put(MessageId.ROOM_SETTINGS, RoomManager::roomSettings);
        events.put(MessageId.SWAP_TEAM, RoomManager::swapTeam);
        events.put(MessageId.KICK_PLAYER, RoomManager::kickPlayer);
        events.put(MessageId.INVITE_PLAYER, RoomManager::invitePlayer);
        events.put(MessageId.FRIENDS_LIST, FriendsManager::friendsList);
        events.put(MessageId.FRIEND_REQUEST, FriendsManager::friendRequest);
        events.put(MessageId.FRIEND_RESPONSE, FriendsManager::friendResponse);
        events.put(MessageId.DELETE_FRIEND, FriendsManager::deleteFriend);
        events.put(MessageId.IGNORED_LIST, IgnoredManager::ignoreList);
        events.put(MessageId.BLOCK_PLAYER, IgnoredManager::blockPlayer);
        events.put(MessageId.UNBLOCK_PLAYER, IgnoredManager::unblockPlayer);
        events.put(MessageId.START_COUNT_DOWN, RoomManager::startCountDown);
        events.put(MessageId.HOST_INFO, RoomManager::hostInfo);
        events.put(MessageId.COUNT_DOWN, RoomManager::countDown);
        events.put(MessageId.CANCEL_COUNT_DOWN, RoomManager::cancelCountDown);
        events.put(MessageId.MATCH_LOADING, RoomManager::matchLoading);
        events.put(MessageId.PLAYER_READY, RoomManager::playerReady);
        events.put(MessageId.CANCEL_LOADING, RoomManager::cancelLoading);
        events.put(MessageId.START_MATCH, RoomManager::startMatch);
        events.put(MessageId.MATCH_RESULT, RoomManager::matchResult);
        events.put(MessageId.MATCH_FORCED_RESULT, RoomManager::matchResult);
        events.put(MessageId.UNKNOWN1, RoomManager::unknown1);
        events.put(MessageId.UNKNOWN2, RoomManager::unknown2);
        events.put(MessageId.ADD_STATS_POINTS, CharacterManager::addStatsPoints);
        events.put(MessageId.PURCHASE_SKILL, Shop::purchaseSkill);
        events.put(MessageId.ACTIVATE_SKILL, InventoryManager::activateSkill);
        events.put(MessageId.DEACTIVATE_SKILL, InventoryManager::deactivateSkill);
        events.put(MessageId.UDP_GAME_1, MatchBroadcaster::udpGame);
        events.put(MessageId.UDP_GAME_2, MatchBroadcaster::udpGame);
        events.put(MessageId.UDP_GAME_3, MatchBroadcaster::udpGame);
        events.put(MessageId.UDP_GAME_4, MatchBroadcaster::udpGame);
        events.put(MessageId.UDP_GAME_5, MatchBroadcaster::udpGame);
    }

    private void defineCertifyEvents() {
        certifyEvents = new ArrayList<>();

        certifyEvents.add(MessageId.GAME_LOGIN);
    }

    public boolean handle(Session session, ClientMessage msg) {
        int messageId = msg.getMessageId();

        if (session.isAuthenticated() || certifyEvents.contains(messageId)) {

            if (!super.handle(session, msg)) {
                MessageEventHandler event = events.get(messageId);

                if (event != null) {
                    event.handle(session, msg);
                } else {
                    return false;
                }
            }
        }

        return true;
    }
}
