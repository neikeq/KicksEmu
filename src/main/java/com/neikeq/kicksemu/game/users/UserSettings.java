package com.neikeq.kicksemu.game.users;

import com.neikeq.kicksemu.network.packets.in.ClientMessage;

public class UserSettings {

    private byte camera = CameraValues.front2;
    private boolean shadows;
    private byte names = 2;
    private byte volEffects = 7;
    private byte volMusic = 7;
    private boolean invites = true;
    private boolean whispers = true;
    private int country;

    // TODO List<Integer> validCountries;

    private static class CameraValues {
        private static final byte side1 = 0;
        private static final byte front1 = 9;
        private static final byte front2 = 5;
        private static final byte front3 = 7;
    }

    public static UserSettings fromMessage(ClientMessage msg) {
        UserSettings settings = new UserSettings();

        settings.setCamera(msg.readByte());
        settings.setShadows(msg.readBoolean());
        settings.setNames(msg.readByte());
        settings.setVolEffects(msg.readByte());
        settings.setVolMusic(msg.readByte());
        settings.setInvites(msg.readBoolean());
        settings.setWhispers(msg.readBoolean());
        settings.setCountry(msg.readInt());

        return settings;
    }

    public boolean isValid() {
        return isValidCamera() && isValidNameDisplay() && isValidSound() && isValidCountry();
    }

    private boolean isValidCamera() {
        return (camera == CameraValues.side1) || (camera == CameraValues.front1) ||
                (camera == CameraValues.front2) || (camera == CameraValues.front3);
    }

    private boolean isValidNameDisplay() {
        // Hide: 0, PositionOnly: 1, Display: 2
        return (names >= 0) && (names <= 2);
    }

    private boolean isValidSound() {
        return (volEffects >= 0) && (volEffects <= 10) && (volMusic >= 0) && (volMusic <= 10);
    }

    private boolean isValidCountry() {
        // TODO check is validCountries contains country
        return country >= 0;
    }

    public byte getCamera() {
        return camera;
    }

    public void setCamera(byte camera) {
        this.camera = camera;
    }

    public boolean getShadows() {
        return shadows;
    }

    public void setShadows(boolean shadows) {
        this.shadows = shadows;
    }

    public byte getNames() {
        return names;
    }

    public void setNames(byte names) {
        this.names = names;
    }

    public byte getVolEffects() {
        return volEffects;
    }

    public void setVolEffects(byte volEffects) {
        this.volEffects = volEffects;
    }

    public byte getVolMusic() {
        return volMusic;
    }

    public void setVolMusic(byte volMusic) {
        this.volMusic = volMusic;
    }

    public boolean getInvites() {
        return invites;
    }

    public void setInvites(boolean invites) {
        this.invites = invites;
    }

    public boolean getWhispers() {
        return whispers;
    }

    public void setWhispers(boolean whispers) {
        this.whispers = whispers;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }
}
