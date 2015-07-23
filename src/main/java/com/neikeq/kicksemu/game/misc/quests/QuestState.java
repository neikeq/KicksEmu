package com.neikeq.kicksemu.game.misc.quests;

public class QuestState {

    private short currentQuest;
    private short remainMatches;

    public QuestState() {
        this((short) 0, (short) 0);
    }

    public QuestState(short currentQuest, short remainMatches) {
        this.currentQuest = currentQuest;
        this.remainMatches = remainMatches;
    }

    public short getCurrentQuest() {
        return currentQuest;
    }

    public short getRemainMatches() {
        return remainMatches;
    }

    public void decreaseRemainMatches() {
        remainMatches--;
    }

    public void nextQuest() {
        currentQuest++;
        remainMatches = 5;
    }
}
