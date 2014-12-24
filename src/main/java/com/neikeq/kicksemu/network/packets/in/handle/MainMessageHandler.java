package com.neikeq.kicksemu.network.packets.in.handle;

import com.neikeq.kicksemu.game.characters.deletion.CharacterRemover;
import com.neikeq.kicksemu.game.characters.TutorialManager;
import com.neikeq.kicksemu.game.characters.creation.CharacterCreator;
import com.neikeq.kicksemu.game.servers.ServerUtils;
import com.neikeq.kicksemu.game.sessions.Authenticator;
import com.neikeq.kicksemu.game.users.UserManager;
import com.neikeq.kicksemu.network.packets.MessageId;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMessageHandler extends MessageHandler {

    private static Map<Integer, MessageEventHandler> events;

    private static List<Integer> certifyEvents;

    public MainMessageHandler() {
        super();

        defineEvents();
        defineCertifyEvents();
    }

    private void defineEvents() {
        events = new HashMap<>();

        // TODO Add 'Character position upgrade' handler
        events.put(MessageId.CERTIFY_LOGIN, Authenticator::certifyLogin);
        events.put(MessageId.INSTANT_LOGIN, Authenticator::instantLogin);
        events.put(MessageId.INSTANT_EXIT, (s, msg) -> UserManager.instantExit(s));
        events.put(MessageId.CERTIFY_EXIT, (s, msg) -> UserManager.certifyExit(s));
        events.put(MessageId.CHARACTER_INFO, (s, msg) -> UserManager.characterInfo(s));
        events.put(MessageId.CREATE_CHARACTER, CharacterCreator::createCharacter);
        events.put(MessageId.CHOICE_CHARACTER, UserManager::choiceCharacter);
        events.put(MessageId.REMOVE_CHARACTER, CharacterRemover::removeCharacter);
        events.put(MessageId.SERVER_LIST, ServerUtils::serverList);
        events.put(MessageId.SERVER_INFO, ServerUtils::serverInfo);
        events.put(MessageId.UPGRADE_CHARACTER, UserManager::upgradeCharacter);
        events.put(MessageId.UPDATE_TUTORIAL, TutorialManager::updateTutorial);
    }

    private void defineCertifyEvents() {
        certifyEvents = new ArrayList<>();

        certifyEvents.add(MessageId.CERTIFY_LOGIN);
        certifyEvents.add(MessageId.INSTANT_LOGIN);
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
