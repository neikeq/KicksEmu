package com.neikeq.kicksemu.game.rooms.match;

import com.neikeq.kicksemu.config.Configuration;
import com.neikeq.kicksemu.game.characters.CharacterManager;
import com.neikeq.kicksemu.game.characters.PlayerInfo;
import com.neikeq.kicksemu.game.characters.types.Position;
import com.neikeq.kicksemu.game.inventory.products.Item;
import com.neikeq.kicksemu.game.inventory.types.Soda;
import com.neikeq.kicksemu.game.misc.quests.MissionTarget;
import com.neikeq.kicksemu.game.misc.quests.QuestManager;
import com.neikeq.kicksemu.game.rooms.Room;
import com.neikeq.kicksemu.game.rooms.enums.RoomTeam;
import com.neikeq.kicksemu.game.rooms.enums.VictoryResult;
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.MissionInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.utils.mutable.MutableInteger;

import java.sql.Connection;

class PlayerRewards {

    private static final int VOTE_POINTS_LIMIT = 100;

    private final MatchResultHandler resultHandler;
    final PlayerResult playerResult;
    final RoomTeam playerTeam;

    private final Session session;
    private final int playerId;
    private final int currentExperience;
    private int baseReward = 0;
    private int rewardWithBonus = 0;
    private short levelsEarned = 0;
    private short lastQuest = -1;

    protected final MutableInteger experience = new MutableInteger();
    protected final MutableInteger points = new MutableInteger();
    protected int onePercentOfBaseReward = 0;

    public void applyMatchRewards() {
        calculateBaseReward();

        if (baseReward > 0) {
            applyRewardExtras();
            limitMaximumExperience();
            updateResultRewards();
            giveReward();

            checkExperienceLevel();
            checkQuestProgress();
        }

        sendRequiredMessages();
    }

    private void calculateBaseReward() {
        int rewardFactor = 0;
        short votePoints = playerResult.getVotePoints() > VOTE_POINTS_LIMIT ?
                VOTE_POINTS_LIMIT : playerResult.getVotePoints();

        switch (room().getTrainingFactor()) {
            case -1:
                if (Configuration.getBoolean("game.rewards.practice") &&
                        matchResult().getCountdown() <= 0 && playerResult.getGoals() >= 3) {
                    rewardFactor = 12;
                }
                break;
            case 6:
                rewardFactor = 12;
                break;
            case 8:
                rewardFactor = 18;
                break;
            case 10:
                rewardFactor = 24;
                break;
            default:
        }

        setBaseReward((int) (rewardFactor * (float) (votePoints / 10)));
    }

    protected void applyRewardExtras() {
        calculateBonuses();
        applyMissionReward();
        applyRewardRates();
    }

    private void calculateBonuses() {
        calculateMatchBonuses();
        calculateItemBonuses();
    }

    private void calculateMatchBonuses() {
        // If player's base position is DF
        short playerPosition = session.getCache().getPosition(connection());
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

        session.getCache().getItems(connection()).values().stream()
                .filter(Item::isSelectedUsageItem).forEach(item -> {
            Soda.applyBonus(Soda.fromId(item.getBonusOne()), baseReward, experience, points);
            Soda.applyBonus(Soda.fromId(item.getBonusTwo()), baseReward, experience, points);
        });
    }

    private void applyRewardRates() {
        experience.multiply(Configuration.getInt("game.rewards.exp"));
        points.multiply(Configuration.getInt("game.rewards.point"));
    }

    private void applyMissionReward() {
        MissionInfo mission = room().getMatchMissionInfo();

        if (mission != null && isMissionCompleted(mission)) {
            experience.sum(mission.getReward());
            points.sum(mission.getReward());
        }
    }

    private boolean isMissionCompleted(MissionInfo mission) {
        StatisticsCarrier targetResult = getMissionTarget(mission);
        int missionValue = mission.getValue();

        if (targetResult == null) return true;
        if (mission.getType() == null) return true;

        switch (mission.getType()) {
            case GOALS:
                return targetResult.getGoals() >= missionValue;
            case ASSISTS:
                return targetResult.getAssists() >= missionValue;
            case STEALS:
                return targetResult.getSteals() >= missionValue;
            case TACKLES:
                return targetResult.getTackles() >= missionValue;
            case INTERCEPTIONS:
                return targetResult.getBlocks() >= missionValue;
            case GOALS_LIMIT:
                return targetResult.getGoals() <= missionValue;
            case ASSISTS_LIMIT:
                return targetResult.getAssists() <= missionValue;
            case STEALS_LIMIT:
                return targetResult.getSteals() <= missionValue;
            case TACKLES_LIMIT:
                return targetResult.getTackles() <= missionValue;
            case INTERCEPTIONS_LIMIT:
                return targetResult.getBlocks() <= missionValue;
            case WIN: {
                boolean win = getMissionTargetTeam(mission).getResult() == VictoryResult.WIN;
                return missionValue == 1 ? win : !win;
            }
            case DRAW: {
                boolean draw = getMissionTargetTeam(mission).getResult() == VictoryResult.DRAW;
                return missionValue == 1 ? draw : !draw;
            }
            case LOSE: {
                boolean lose = getMissionTargetTeam(mission).getResult() == VictoryResult.LOSE;
                return missionValue == 1 ? lose : !lose;
            }
            default:
                return true;
        }
    }

    private StatisticsCarrier getMissionTarget(MissionInfo mission) {
        if (mission.getTarget() == null) return null;

        switch (mission.getTarget()) {
            case PLAYER:
                return playerResult;
            case TEAM:
                return resultHandler.getPlayerTeamResult(playerId);
            case RIVAL_TEAM:
                resultHandler.getRivalTeamResult(playerId);
            default:
                return null;
        }
    }

    private TeamResult getMissionTargetTeam(MissionInfo mission) {
        return mission.getTarget() == MissionTarget.TEAM ?
                resultHandler.getPlayerTeamResult(playerId) :
                resultHandler.getRivalTeamResult(playerId);
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

    private void checkExperienceLevel() {
        levelsEarned = CharacterManager.checkIfLevelUp(session, getPlayerLevel(),
                currentExperience + experience.get(), connection());
    }

    private void checkQuestProgress() {
        lastQuest = QuestManager.checkQuests(playerId, matchResult(), playerTeam, connection());
    }

    private void sendRequiredMessages() {
        if (baseReward > 0) {
            room().broadcast(MessageBuilder.updateRoomPlayer(playerId, connection()));
            room().broadcast(MessageBuilder.playerBonusStats(session, connection()));

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
            rewardWithBonus += onePercentOfBaseReward * percentage;
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

    protected Room room() {
        return resultHandler.getRoom();
    }

    private Connection connection() {
        return resultHandler.getConnection();
    }

    protected MatchResult matchResult() {
        return resultHandler.getResult();
    }

    private void setBaseReward(int baseReward) {
        this.baseReward = baseReward;
        this.rewardWithBonus = baseReward;
        this.onePercentOfBaseReward = baseReward / 100;
    }

    public PlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        this.resultHandler = resultHandler;
        this.playerResult = playerResult;
        this.playerId = playerResult.getPlayerId();
        this.session = room().getPlayer(playerId);
        this.playerTeam = room().getPlayerTeam(playerId);
        this.currentExperience = PlayerInfo.getExperience(playerId, connection());
    }
}
