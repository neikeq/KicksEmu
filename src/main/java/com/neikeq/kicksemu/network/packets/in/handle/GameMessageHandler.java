package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.StatusMessage;
import com.neikeq.kicksemu.game.chat.ChatManager;
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
        events.put(MessageId.GAME_EXIT, (s, msg) -> CharacterManager.gameExit(s));
        events.put(MessageId.UDP_CONFIRM, Authenticator::udpConfirm);
        events.put(MessageId.PLAYER_INFO, CharacterManager::playerInfo);
        events.put(MessageId.LOBBY_LIST, LobbyManager::lobbyList);
        events.put(MessageId.ROOM_LIST, RoomManager::roomList);
        events.put(MessageId.NEXT_TIP, ServerUtils::nextTip);
        events.put(MessageId.CHAT_MESSAGE, ChatManager::chatMessage);
        events.put(MessageId.STATUS_MESSAGE, StatusMessage::statusMessage);
        events.put(MessageId.CREATE_ROOM, RoomManager::createRoom);
        events.put(MessageId.JOIN_ROOM, RoomManager::joinRoom);
        events.put(MessageId.LEAVE_ROOM, RoomManager::leaveRoom);
        events.put(MessageId.ROOM_MAP, RoomManager::roomMap);
        events.put(MessageId.ROOM_BALL, RoomManager::roomBall);
        events.put(MessageId.ROOM_SETTINGS, RoomManager::roomSettings);
        events.put(MessageId.SWAP_TEAM, RoomManager::swapTeam);
        events.put(MessageId.KICK_PLAYER, RoomManager::kickPlayer);
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
                    return true;
                }
            }
        }

        return false;
    }
}
