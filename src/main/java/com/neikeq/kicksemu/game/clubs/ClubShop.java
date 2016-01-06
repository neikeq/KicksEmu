package com.neikeq.kicksemu.game.clubs;

import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.ItemInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.io.Output;
import com.neikeq.kicksemu.network.packets.in.ClientMessage;
import com.neikeq.kicksemu.network.packets.in.MessageException;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;

import java.sql.SQLException;
import java.util.Optional;

public class ClubShop {

    public static void purchaseClubItem(Session session, ClientMessage msg) {
        try (ConnectionRef con = ConnectionRef.ref()) {
            int playerId = session.getPlayerId();
            int clubId = MemberInfo.getClubId(playerId, con);

            if (clubId <= 0) {
                throw new MessageException("Player is not a club member.", -2);
            }

            if (MemberInfo.getRole(playerId, con) != MemberRole.MANAGER) {
                throw new MessageException("Player is not the club manager.", -6);
            }

            ClubItemRequest request = new ClubItemRequest(msg);

            Optional<ItemInfo> maybeItemInfo = TableManager.getItemInfo(c ->
                    c.getId() == request.getProductId());

            Optional<MessageException> exception = maybeItemInfo.map(itemInfo -> {
                MessageException ex = null;
                try {
                    if (request.getPrice() != itemInfo.getPrice().getClubItemPrice()) {
                        throw new MessageException("Invalid price.", -4);
                    }

                    if (request.getPrice() > ClubInfo.getClubPoints(clubId, con)) {
                        throw new MessageException("Not enough club points.", -5);
                    }

                    ClubItemManager.getInstance().applyEffect(itemInfo, request, clubId);
                    chargeClub(clubId, request);

                    session.send(MessageBuilder.purchaseClubItem(session, (short) 0));
                } catch (MessageException e) {
                    ex = e;
                }
                return Optional.ofNullable(ex);
            }).orElseThrow(() -> new MessageException("Item does not exist.", -1));

            if (exception.isPresent()) {
                throw exception.get();
            }
        } catch (MessageException e) {
            session.send(MessageBuilder.purchaseClubItem(session, (short) e.getErrorCode()));
        } catch (SQLException e) {
            Output.println(e.getMessage());
        }
    }

    private static void chargeClub(int clubId, ClubItemRequest request) {
        ClubInfo.sumClubPoints(-request.getPrice(), clubId);
    }
}
