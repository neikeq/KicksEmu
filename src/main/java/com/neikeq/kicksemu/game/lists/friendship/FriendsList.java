package com.neikeq.kicksemu.game.lists.friendship;

import java.util.ArrayList;
import java.util.List;

public class FriendsList {

    private List<Integer> friends;

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        int index = friends.indexOf(friendId);

        if (index != -1) {
            friends.remove(index);
        }
    }

    public boolean containsFriend(int friendId) {
        return friends.contains(friendId);
    }

    public int size() {
        return friends.size();
    }

    public List<Integer> fromPage(byte page) {
        List<Integer> players = new ArrayList<>();

        int index = page * 10;

        for (int i = index; i < index + 10 && i < friends.size(); i++) {
            players.add(friends.get(i));
        }

        return players;
    }

    public static FriendsList fromString(String strFriends) {
        FriendsList friendsList = new FriendsList();

        for (String friendId : strFriends.split(",")) {
            if (friendId.isEmpty()) {
                break;
            }

            friendsList.addFriend(Integer.valueOf(friendId));
        }

        return friendsList;
    }

    @Override
    public String toString() {
        String strFriends = "";

        for (int i = 0; i < friends.size(); i++) {
            strFriends += (i > 0 ? "," :  "");
            strFriends += String.valueOf(friends.get(i));
        }

        return strFriends;
    }

    public FriendsList() {
        friends = new ArrayList<>();
    }
}
