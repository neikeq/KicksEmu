package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.types.Soda;
import com.neikeq.kicksemu.game.misc.quests.QuestManager;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.Connection;

class PlayerRewards {

    private static final int VOTE_POINTS_LIMIT = 100;

    private final MatchResultHandler resultHandler;
    private final PlayerResult playerResult;
    private final RoomTeam playerTeam;
    private final Session session;
    private final int playerId;
    private final int currentExperience;
    private int baseReward = 0;
    private int rewardWithBonus = 0;
    private short levelsEarned = 0;
    private short lastQuest = -1;

    private final MutableInteger experience = new MutableInteger();
    private final MutableInteger points = new MutableInteger();

    public void applyMatchRewards() {
        calculateBaseReward();

        if (baseReward > 0) {
            calculateBonuses();
            applyRewardRates();

            limitMaximumExperience();
            updateResultRewards();
            giveReward();

            checkExperienceAndTryLevelUp();
            checkQuestProgress();
        }

        sendRequiredMessages();
    }

    private void calculateBaseReward() {
        short votePoints = playerResult.getVotePoints() > VOTE_POINTS_LIMIT ?
                VOTE_POINTS_LIMIT : playerResult.getVotePoints();

        switch (room().getTrainingFactor()) {
            case -1:
                if (Configuration.getBoolean("game.rewards.practice") &&
                        matchResult().getCountdown() <= 0 && playerResult.getGoals() >= 3) {
                    baseReward = (int)(12 * (float)(votePoints / 10));
                } else {
                    baseReward = 0;
                }
            case 6:
                baseReward = (int)(12 * (float)(votePoints / 10));
            case 8:
                baseReward = (int)(18 * (float)(votePoints / 10));
            case 10:
                baseReward = (int)(24 * (float)(votePoints / 10));
            default:
                baseReward = 0;
        }

        rewardWithBonus = baseReward;
    }

    private void calculateBonuses() {
        calculateMatchBonuses();
        calculateItemBonuses();
    }

    private void calculateMatchBonuses() {
        rewardWithBonus = baseReward;

        // If player's base position is DF
        short playerPosition = PlayerInfo.getPosition(playerId, connection());
        if (Position.basePosition(playerPosition) == Position.DF) {
            short scoredGoals = resultHandler.getPlayerTeamResult(playerId).getGoals();
            short concededGoals = resultHandler.getRivalTeamResult(playerId).getGoals();

            // If player's team did not lose and conceded 1 or less goals
            applyBonusPercentageIf(concededGoals <= 1 && scoredGoals >= concededGoals, 30);
        }

        if (resultHandler.isLowersBonusEnabled()) {
            applyLowersBonus();
        }

        applyBonusPercentageIf(isLevelGapGreaterThanLimit(), 10);
        applyBonusPercentageIf(resultHandler.isGoldenTime(), 50);
        applyBonusPercentageIf(isPlayerMvp(), 25);
    }

    private void calculateItemBonuses() {
        points.set(rewardWithBonus);
        experience.set(rewardWithBonus);

        PlayerInfo.getInventoryItems(playerId, connection()).values().stream()
                .filter(item -> item.getExpiration().isUsage() && item.isSelected())
                .forEach(item -> {
                    Soda bonusOne = Soda.fromId(item.getBonusOne());
                    if (bonusOne != null) {
                        bonusOne.applyBonus(baseReward, experience, points);
                    }

                    Soda bonusTwo = Soda.fromId(item.getBonusTwo());
                    if (bonusTwo != null) {
                        bonusTwo.applyBonus(baseReward, experience, points);
                    }
                });
    }

    private void applyRewardRates() {
        experience.multiply(Configuration.getInt("game.rewards.exp"));
        points.multiply(Configuration.getInt("game.rewards.point"));
    }

    private void limitMaximumExperience() {
        if (currentExperience + experience.get() > TableManager.EXPERIENCE_LIMIT) {
            experience.set(TableManager.EXPERIENCE_LIMIT - currentExperience);
        }
    }

    private void updateResultRewards() {
        playerResult.setExperience(experience.get());
        playerResult.setPoints(points.get());
    }

    private void giveReward() {
        PlayerInfo.sumRewards(playerResult.getExperience(), playerResult.getPoints(),
                playerId, connection());
    }

    private void checkExperienceAndTryLevelUp() {
        levelsEarned = CharacterManager.checkExperience(playerId, getPlayerLevel(),
                currentExperience + experience.get(), connection());
    }

    private void checkQuestProgress() {
        lastQuest = QuestManager.checkQuests(playerId, matchResult(), playerTeam, connection());
    }

    private void sendRequiredMessages() {
        if (baseReward > 0) {
            room().sendBroadcast(MessageBuilder.updateRoomPlayer(playerId, connection()));
            room().sendBroadcast(MessageBuilder.playerBonusStats(playerId, connection()));

            if (levelsEarned > 0) {
                session.send(MessageBuilder.playerStats(playerId, connection()));
            }
        }

        session.send(MessageBuilder.playerProgress(playerId, lastQuest, connection()));
        session.flush();
    }

    private void applyLowersBonus() {
        int bonusPercentage = 0;
        int levelsDifference = resultHandler.getRoomAverageLevel().get() - getPlayerLevel();

        boolean levelIsLowerThanRoomAverage = levelsDifference > 0;

        if (levelIsLowerThanRoomAverage) {
            bonusPercentage = levelsDifference * 2;
            if (bonusPercentage > 75) bonusPercentage = 75;
        }

        applyBonusPercentageIf(levelIsLowerThanRoomAverage, bonusPercentage);
    }

    private void applyBonusPercentageIf(boolean condition, int percentage) {
        if (condition) {
            rewardWithBonus += (baseReward * percentage) / 100;
        }
    }

    private boolean isLevelGapGreaterThanLimit() {
        return resultHandler.getRoomLevelGap() > MatchResultHandler.LEVEL_GAP_LIMIT;
    }

    private boolean isPlayerMvp() {
        return matchResult().getMom() == playerId;
    }

    private short getPlayerLevel() {
        return resultHandler.getLevelCache().getPlayerLevel(playerId, connection());
    }

    private Room room() {
        return resultHandler.getRoom();
    }

    private Connection connection() {
        return resultHandler.getConnection();
    }

    private MatchResult matchResult() {
        return resultHandler.getResult();
    }

    public PlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        this.resultHandler = resultHandler;
        this.playerResult = playerResult;
        this.playerId = playerResult.getPlayerId();
        this.session = room().getPlayers().get(playerId);
        this.playerTeam = room().getPlayerTeam(playerId);
        this.currentExperience = PlayerInfo.getExperience(playerId, connection());
    }
}
