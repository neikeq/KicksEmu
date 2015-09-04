package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.CharacterRemover;
import com.neikeq.kicksemu.game.characters.StatusMessage;
import com.neikeq.kicksemu.game.misc.tutorial.TutorialManager;
import com.neikeq.kicksemu.game.characters.creation.CharacterCreator;
import com.neikeq.kicksemu.game.chat.ChatManager;
import com.neikeq.kicksemu.game.clubs.ClubManager;
import com.neikeq.kicksemu.game.inventory.InventoryManager;
import com.neikeq.kicksemu.game.inventory.Shop;
import com.neikeq.kicksemu.game.lobby.LobbyManager;
import com.neikeq.kicksemu.game.misc.MatchBroadcaster;
import com.neikeq.kicksemu.game.misc.friendship.FriendsManager;
import com.neikeq.kicksemu.game.misc.ignored.IgnoredManager;
import com.neikeq.kicksemu.game.rooms.messages.ClubRoomMessages;
import com.neikeq.kicksemu.game.rooms.messages.RoomMessages;
import com.neikeq.kicksemu.game.servers.ServerType;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Authenticator;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.users.UserManager;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.server.ServerManager;
import com.neikeq.kicksemu.network.server.udp.UdpPing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    private static final List<Integer> certifyEvents = new ArrayList<>();
    private static final Map<Integer, MessageEventHandler> events = new HashMap<>();

    public void defineEvents() {
        // Define global events
        events.put(MessageId.TCP_PING, UserManager::tcpPing);
        events.put(MessageId.UPDATE_SETTINGS, UserManager::updateSettings);

        if (ServerManager.getServerType() == ServerType.MAIN) {
            // Define main events
            events.put(MessageId.CERTIFY_LOGIN, Authenticator::certifyLogin);
            events.put(MessageId.INSTANT_LOGIN, Authenticator::instantLogin);
            events.put(MessageId.INSTANT_EXIT, (s, m) -> UserManager.instantExit(s));
            events.put(MessageId.CERTIFY_EXIT, (s, m) -> UserManager.certifyExit(s));
            events.put(MessageId.CHARACTER_INFO, (s, m) -> UserManager.characterInfo(s));
            events.put(MessageId.CREATE_CHARACTER, CharacterCreator::createCharacter);
            events.put(MessageId.CHOICE_CHARACTER, UserManager::choiceCharacter);
            events.put(MessageId.REMOVE_CHARACTER, CharacterRemover::removeCharacter);
            events.put(MessageId.SERVER_LIST, ServerUtils::serverList);
            events.put(MessageId.SERVER_INFO, ServerUtils::serverInfo);
            events.put(MessageId.UPGRADE_CHARACTER, UserManager::upgradeCharacter);
            events.put(MessageId.UPDATE_TUTORIAL, TutorialManager::updateTutorial);
        } else {
            // Define game events
            events.put(MessageId.GAME_LOGIN, Authenticator::gameLogin);
            events.put(MessageId.GAME_EXIT, (s, m) -> UserManager.gameExit(s));
            events.put(MessageId.UDP_CONFIRM, (s, m) -> Authenticator.udpConfirm(s));
            events.put(MessageId.PLAYER_INFO, (s, m) -> CharacterManager.playerInfo(s));
            events.put(MessageId.FRIENDS_LIST, FriendsManager::friendsList);
            events.put(MessageId.FRIEND_REQUEST, FriendsManager::friendRequest);
            events.put(MessageId.FRIEND_RESPONSE, FriendsManager::friendResponse);
            events.put(MessageId.DELETE_FRIEND, FriendsManager::deleteFriend);
            events.put(MessageId.CLUB_INFO, (s, m) -> ClubManager.clubInfo(s));
            events.put(MessageId.CLUB_MEMBERS, ClubManager::clubMembers);
            events.put(MessageId.IGNORED_LIST, IgnoredManager::ignoreList);
            events.put(MessageId.BLOCK_PLAYER, IgnoredManager::blockPlayer);
            events.put(MessageId.UNBLOCK_PLAYER, IgnoredManager::unblockPlayer);
            events.put(MessageId.STATUS_MESSAGE, StatusMessage::statusMessage);
            events.put(MessageId.ROOM_LIST, RoomMessages::roomList);
            events.put(MessageId.CREATE_ROOM, RoomMessages::createRoom);
            events.put(MessageId.JOIN_ROOM, RoomMessages::joinRoom);
            events.put(MessageId.QUICK_JOIN_ROOM, (s, m) -> RoomMessages.quickJoinRoom(s));
            events.put(MessageId.NEXT_TIP, (s, m) -> ServerUtils.nextTip(s));
            events.put(MessageId.LEAVE_ROOM, RoomMessages::leaveRoom);
            events.put(MessageId.SWAP_TEAM, RoomMessages::swapTeam);
            events.put(MessageId.ROOM_MAP, RoomMessages::roomMap);
            events.put(MessageId.ROOM_BALL, RoomMessages::roomBall);
            events.put(MessageId.ROOM_SETTINGS, RoomMessages::roomSettings);
            events.put(MessageId.KICK_PLAYER, RoomMessages::kickPlayer);
            events.put(MessageId.LOBBY_LIST, LobbyManager::lobbyList);
            events.put(MessageId.INVITE_PLAYER, RoomMessages::invitePlayer);
            events.put(MessageId.CHAT_MESSAGE, ChatManager::chatMessage);
            events.put(MessageId.START_COUNT_DOWN, RoomMessages::startCountDown);
            events.put(MessageId.HOST_INFO, RoomMessages::hostInfo);
            events.put(MessageId.COUNT_DOWN, RoomMessages::countDown);
            events.put(MessageId.CANCEL_COUNT_DOWN, RoomMessages::cancelCountDown);
            events.put(MessageId.MATCH_LOADING, RoomMessages::matchLoading);
            events.put(MessageId.PLAYER_READY, RoomMessages::playerReady);
            events.put(MessageId.CANCEL_LOADING, RoomMessages::cancelLoading);
            events.put(MessageId.START_MATCH, RoomMessages::startMatch);
            events.put(MessageId.MATCH_RESULT, RoomMessages::matchResult);

            if (Configuration.getBoolean("game.match.result.force")) {
                events.put(MessageId.MATCH_FORCED_RESULT, RoomMessages::matchResult);
            }

            events.put(MessageId.UNKNOWN1, RoomMessages::unknown1);
            events.put(MessageId.UNKNOWN2, RoomMessages::unknown2);
            events.put(MessageId.CLUB_ROOM_LIST, ClubRoomMessages::roomList);
            events.put(MessageId.CLUB_CREATE_ROOM, ClubRoomMessages::createRoom);
            events.put(MessageId.CLUB_JOIN_ROOM, ClubRoomMessages::joinRoom);
            events.put(MessageId.CLUB_LEAVE_ROOM, ClubRoomMessages::leaveRoom);
            events.put(MessageId.CLUB_QUICK_JOIN, (s, m) -> ClubRoomMessages.quickJoinRoom(s));
            events.put(MessageId.CLUB_KICK_PLAYER, ClubRoomMessages::kickPlayer);
            events.put(MessageId.CLUB_ROOM_SETTINGS, ClubRoomMessages::roomSettings);
            events.put(MessageId.CLUB_REGISTER_TEAM, ClubRoomMessages::registerTeam);
            events.put(MessageId.CLUB_UNREGISTER_TEAM, ClubRoomMessages::unregisterTeam);
            events.put(MessageId.CLUB_CHALLENGE_TEAM, ClubRoomMessages::challengeTeam);
            events.put(MessageId.CLUB_CHALLENGE_RESPONSE, ClubRoomMessages::challengeResponse);
            events.put(MessageId.CLUB_TEAMS_LIST, ClubRoomMessages::teamList);
            events.put(MessageId.CLUB_INVITE_PLAYER, ClubRoomMessages::invitePlayer);
            events.put(MessageId.PURCHASE_ITEM, Shop::purchaseItem);
            events.put(MessageId.RESELL_ITEM, InventoryManager::resellItem);
            events.put(MessageId.ACTIVATE_ITEM, InventoryManager::activateItem);
            events.put(MessageId.DEACTIVATE_ITEM, InventoryManager::deactivateItem);
            events.put(MessageId.MERGE_ITEM, InventoryManager::mergeItem);
            events.put(MessageId.PURCHASE_LEARN, Shop::purchaseLearn);
            events.put(MessageId.PURCHASE_SKILL, Shop::purchaseSkill);
            events.put(MessageId.ACTIVATE_SKILL, InventoryManager::activateSkill);
            events.put(MessageId.DEACTIVATE_SKILL, InventoryManager::deactivateSkill);
            events.put(MessageId.PURCHASE_CELE, Shop::purchaseCele);
            events.put(MessageId.ACTIVATE_CELE, InventoryManager::activateCele);
            events.put(MessageId.DEACTIVATE_CELE, InventoryManager::deactivateCele);
            events.put(MessageId.PLAYER_DETAILS, CharacterManager::playerDetails);
            events.put(MessageId.ADD_STATS_POINTS, CharacterManager::addStatsPoints);
            events.put(MessageId.UDP_PING, (s, m) -> UdpPing.udpPing(s));
            events.put(MessageId.UDP_AUTHENTICATE, (s, m) -> Authenticator.udpAuthentication(s));

            if (MatchBroadcaster.isBroadcastEnabled()) {
                events.put(MessageId.UDP_GAME_1, MatchBroadcaster::udpGame);
                events.put(MessageId.UDP_GAME_2, MatchBroadcaster::udpGame);
                events.put(MessageId.UDP_GAME_3, MatchBroadcaster::udpGame);
                events.put(MessageId.UDP_GAME_4, MatchBroadcaster::udpGame);
                events.put(MessageId.UDP_GAME_5, MatchBroadcaster::udpGame);
            }

            if (Configuration.getBoolean("game.proxy.enabled")) {
                events.put(MessageId.PROXY_UPDATE_PORT, (s, m) -> s.setUdpPort(m.readShort()));
            }
        }
    }

    public void defineCertifyEvents() {
        if (ServerManager.getServerType() == ServerType.MAIN) {
            certifyEvents.add(MessageId.CERTIFY_LOGIN);
            certifyEvents.add(MessageId.INSTANT_LOGIN);
        } else {
            certifyEvents.add(MessageId.GAME_LOGIN);
        }
    }

    public void handle(Session session, ClientMessage msg)
            throws UndefinedMessageException {
        int messageId = msg.getMessageId();

        if (session.isAuthenticated() || certifyEvents.contains(messageId)) {
            MessageEventHandler event = events.get(msg.getMessageId());

            if (event != null) {
                event.handle(session, msg);
            } else {
                throw new UndefinedMessageException("Received unknown message (" +
                        messageId + ")");
            }
        }
    }
}
