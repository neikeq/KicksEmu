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
import com.neikeq.kicksemu.game.sessions.Session;
import com.neikeq.kicksemu.game.table.MissionInfo;
import com.neikeq.kicksemu.game.table.TableManager;
import com.neikeq.kicksemu.network.packets.out.MessageBuilder;
import com.neikeq.kicksemu.storage.ConnectionRef;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;

class PlayerRewards {

    private static final int VOTE_POINTS_LIMIT = 100;

    private final MatchResultHandler resultHandler;
    private final PlayerResult playerResult;
    final RoomTeam playerTeam;

    private final Session session;
    private final int playerId;
    private final int currentExperience;
    private int baseReward;
    private int rewardWithBonus;
    private short levelsEarned;
    private short lastQuest = -1;

    final MutableInt experience = new MutableInt();
    final MutableInt points = new MutableInt();
    int onePercentOfBaseReward;

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
        short votePoints = (playerResult.getVotePoints() > VOTE_POINTS_LIMIT) ?
                VOTE_POINTS_LIMIT : playerResult.getVotePoints();

        switch (room().getTrainingFactor()) {
            case -1:
                if (Configuration.getBoolean("game.rewards.practice") &&
                        (matchResult().getCountdown() <= 0) && (playerResult.getGoals() >= 3)) {
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

    void applyRewardExtras() {
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
            short scoredGoals = resultHandler.getPlayerTeamResult(playerId)
                    .map(TeamResult::getGoals).orElse((short) 0);
            short concededGoals = resultHandler.getRivalTeamResult(playerId)
                    .map(TeamResult::getGoals).orElse((short) 0);

            // If player's team did not lose and conceded 1 or less goals
            applyBonusPercentageIf((concededGoals <= 1) && (scoredGoals >= concededGoals), 30);
        }

        if (resultHandler.isLowersBonusEnabled()) {
            applyLowersBonus();
        }

        applyBonusPercentageIf(isLevelGapGreaterThanLimit(), 10);
        applyBonusPercentageIf(resultHandler.isGoldenTime(), 50);
        applyBonusPercentageIf(isPlayerMvp(), 25);
    }

    private void calculateItemBonuses() {
        points.setValue(rewardWithBonus);
        experience.setValue(rewardWithBonus);

        session.getCache().getItems(connection()).values().stream()
                .filter(Item::isSelectedUsageItem).forEach(item -> {
            Soda.applyBonus(Soda.fromId(item.getBonusOne()), baseReward, experience, points);
            Soda.applyBonus(Soda.fromId(item.getBonusTwo()), baseReward, experience, points);
        });
    }

    private void applyRewardRates() {
        int multiplier = Configuration.getInt("game.rewards.exp");

        if (Configuration.getBoolean("game.match.bonus.general")) {
            if (getPlayerLevel() < 11) {
                multiplier = Configuration.getInt("game.rewards.noob");
            } else if (getPlayerLevel() < 26) {
                multiplier = Configuration.getInt("game.rewards.rookie");
            }
        }

        experience.setValue(experience.getValue() * multiplier);
        points.setValue(points.getValue() * Configuration.getInt("game.rewards.point"));
    }

    private void applyMissionReward() {
        room().getMatchMissionInfo().ifPresent(mission -> {
            if (isMissionCompleted(mission)) {
                experience.add(mission.getReward());
                points.add(mission.getReward());
            }
        });
    }

    private boolean isMissionCompleted(MissionInfo mission) {
        return getMissionTarget(mission).map(target -> mission.getType().map(type ->
                type.resultAchievesMission(target, getMissionTargetTeam(mission), mission)
        ).orElse(true)).orElse(true);
    }

    private Optional<StatisticsCarrier> getMissionTarget(MissionInfo mission) {
        switch (mission.getTarget()) {
            case PLAYER:
                return Optional.of(playerResult);
            case TEAM:
                return resultHandler.getPlayerTeamResult(playerId)
                        .map(teamResult -> (StatisticsCarrier) teamResult);
            case RIVAL_TEAM:
                return resultHandler.getRivalTeamResult(playerId)
                        .map(rivalTeamResult -> (StatisticsCarrier) rivalTeamResult);
            case NOBODY:
            default:
                return Optional.empty();
        }
    }

    private Optional<TeamResult> getMissionTargetTeam(MissionInfo mission) {
        if (mission.getTarget() == MissionTarget.TEAM) {
            return resultHandler.getPlayerTeamResult(playerId);
        } else if (mission.getTarget() == MissionTarget.RIVAL_TEAM) {
            return resultHandler.getRivalTeamResult(playerId);
        }

        return Optional.empty();
    }

    private void limitMaximumExperience() {
        if ((currentExperience + experience.getValue()) > TableManager.EXPERIENCE_LIMIT) {
            experience.setValue(TableManager.EXPERIENCE_LIMIT - currentExperience);
        }
    }

    private void updateResultRewards() {
        playerResult.setExperience(experience.getValue());
        playerResult.setPoints(points.getValue());
    }

    private void giveReward() {
        PlayerInfo.sumRewards(playerResult.getExperience(), playerResult.getPoints(),
                playerId, connection());
    }

    private void checkExperienceLevel() {
        levelsEarned = CharacterManager.checkIfLevelUp(session, getPlayerLevel(),
                currentExperience + experience.getValue(), connection());
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
        int levelsDifference = resultHandler.getRoomAverageLevel().getValue() - getPlayerLevel();

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

    Room room() {
        return resultHandler.getRoom();
    }

    private ConnectionRef connection() {
        return resultHandler.getConnection();
    }

    MatchResult matchResult() {
        return resultHandler.getResult();
    }

    private void setBaseReward(int baseReward) {
        this.baseReward = baseReward;
        rewardWithBonus = baseReward;
        onePercentOfBaseReward = baseReward / 100;
    }

    public PlayerRewards(MatchResultHandler resultHandler, PlayerResult playerResult) {
        this.resultHandler = resultHandler;
        this.playerResult = playerResult;
        playerId = playerResult.getPlayerId();
        session = room().getPlayer(playerId);
        playerTeam = room().getPlayerTeam(playerId).orElseThrow(IllegalStateException::new);
        currentExperience = PlayerInfo.getExperience(playerId, connection());
    }
}
