-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Server version:               5.6.20 - Source distribution
-- Server OS:                    Linux
-- HeidiSQL Version:             8.3.0.4694
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

-- Dumping database structure for kicksdb
CREATE DATABASE IF NOT EXISTS `kicksdb` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `kicksdb`;


-- Dumping structure for table kicksdb.bans
CREATE TABLE IF NOT EXISTS `bans` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `reason` varchar(140) DEFAULT 'not specified',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.blacklist
CREATE TABLE IF NOT EXISTS `blacklist` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `remote_address` varchar(64) NOT NULL,
  `created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `reason` varchar(140) NOT NULL DEFAULT 'not specified',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.ceres
CREATE TABLE IF NOT EXISTS `ceres` (
  `player_id` int(11) unsigned NOT NULL,
  `inventory_id` int(11) unsigned NOT NULL,
  `cere_id` int(11) unsigned NOT NULL,
  `expiration` int(11) unsigned NOT NULL,
  `selection_index` tinyint(4) unsigned NOT NULL,
  `timestamp_expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `visible` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.characters
CREATE TABLE IF NOT EXISTS `characters` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `owner` int(11) unsigned NOT NULL,
  `name` varchar(14) NOT NULL,
  `position` smallint(5) unsigned NOT NULL,
  `level` smallint(6) unsigned NOT NULL DEFAULT '1',
  `blocked` bit(1) NOT NULL DEFAULT b'0',
  `moderator` bit(1) NOT NULL DEFAULT b'0',
  `visible` bit(1) NOT NULL DEFAULT b'0',
  `club_id` int(10) unsigned NOT NULL DEFAULT '0',
  `quest_current` smallint(6) unsigned NOT NULL DEFAULT '1',
  `quest_matches_left` smallint(6) unsigned NOT NULL DEFAULT '5',
  `tutorial_dribbling` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `tutorial_passing` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `tutorial_shooting` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `tutorial_defense` tinyint(4) unsigned NOT NULL DEFAULT '0',
  `received_reward` bit(1) NOT NULL DEFAULT b'0',
  `experience` int(11) unsigned NOT NULL DEFAULT '0',
  `points` int(11) unsigned NOT NULL DEFAULT '4000',
  `animation` smallint(6) unsigned NOT NULL,
  `face` smallint(6) unsigned NOT NULL,
  `default_head` int(10) unsigned NOT NULL,
  `default_shirts` int(10) unsigned NOT NULL,
  `default_pants` int(10) unsigned NOT NULL,
  `default_shoes` int(10) unsigned NOT NULL,
  `stats_points` smallint(6) unsigned NOT NULL,
  `stats_running` smallint(6) unsigned NOT NULL,
  `stats_endurance` smallint(6) unsigned NOT NULL,
  `stats_agility` smallint(6) unsigned NOT NULL,
  `stats_ball_control` smallint(6) unsigned NOT NULL,
  `stats_dribbling` smallint(6) unsigned NOT NULL,
  `stats_stealing` smallint(6) unsigned NOT NULL,
  `stats_tackling` smallint(6) unsigned NOT NULL,
  `stats_heading` smallint(6) unsigned NOT NULL,
  `stats_short_shots` smallint(6) unsigned NOT NULL,
  `stats_long_shots` smallint(6) unsigned NOT NULL,
  `stats_crossing` smallint(6) unsigned NOT NULL,
  `stats_short_passes` smallint(6) unsigned NOT NULL,
  `stats_long_passes` smallint(6) unsigned NOT NULL,
  `stats_marking` smallint(6) unsigned NOT NULL,
  `stats_goalkeeping` smallint(6) unsigned NOT NULL,
  `stats_punching` smallint(6) unsigned NOT NULL,
  `stats_defense` smallint(6) unsigned NOT NULL,
  `status_message` varchar(34) NOT NULL DEFAULT '0',
  `friends_list` text NOT NULL,
  `ignored_list` text NOT NULL,
  `history_matches` int(11) NOT NULL DEFAULT '0',
  `history_wins` int(11) NOT NULL DEFAULT '0',
  `history_draws` int(11) NOT NULL DEFAULT '0',
  `history_points` int(11) NOT NULL DEFAULT '0',
  `history_MOM` int(11) NOT NULL DEFAULT '0',
  `history_valid_goals` int(11) NOT NULL DEFAULT '0',
  `history_valid_assists` int(11) NOT NULL DEFAULT '0',
  `history_valid_interception` int(11) NOT NULL DEFAULT '0',
  `history_valid_shooting` int(11) NOT NULL DEFAULT '0',
  `history_valid_stealing` int(11) NOT NULL DEFAULT '0',
  `history_valid_tackling` int(11) NOT NULL DEFAULT '0',
  `history_shooting` int(11) NOT NULL DEFAULT '0',
  `history_stealing` int(11) NOT NULL DEFAULT '0',
  `history_tackling` int(11) NOT NULL DEFAULT '0',
  `history_total_points` int(11) NOT NULL DEFAULT '0',
  `history_month_matches` int(11) NOT NULL DEFAULT '0',
  `history_month_wins` int(11) NOT NULL DEFAULT '0',
  `history_month_draws` int(11) NOT NULL DEFAULT '0',
  `history_month_points` int(11) NOT NULL DEFAULT '0',
  `history_month_MOM` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_goals` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_assists` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_interception` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_shooting` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_stealing` int(11) NOT NULL DEFAULT '0',
  `history_month_valid_tackling` int(11) NOT NULL DEFAULT '0',
  `history_month_shooting` int(11) NOT NULL DEFAULT '0',
  `history_month_stealing` int(11) NOT NULL DEFAULT '0',
  `history_month_tackling` int(11) NOT NULL DEFAULT '0',
  `history_month_total_points` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.clubs
CREATE TABLE IF NOT EXISTS `clubs` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `uniform_home_shirts` int(11) unsigned DEFAULT NULL,
  `uniform_home_pants` int(11) unsigned DEFAULT NULL,
  `uniform_home_socks` int(11) unsigned DEFAULT NULL,
  `uniform_home_wrist` int(11) unsigned DEFAULT NULL,
  `uniform_away_shirts` int(11) unsigned DEFAULT NULL,
  `uniform_away_pants` int(11) unsigned DEFAULT NULL,
  `uniform_away_socks` int(11) unsigned DEFAULT NULL,
  `uniform_away_wrist` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.items
CREATE TABLE IF NOT EXISTS `items` (
  `player_id` int(11) unsigned NOT NULL,
  `inventory_id` int(11) unsigned NOT NULL,
  `item_id` int(11) unsigned NOT NULL,
  `expiration` int(11) unsigned NOT NULL,
  `bonus_one` int(11) unsigned NOT NULL,
  `bonus_two` int(11) unsigned NOT NULL,
  `usages` smallint(6) unsigned NOT NULL,
  `timestamp_expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `selected` bit(1) NOT NULL,
  `visible` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.learns
CREATE TABLE IF NOT EXISTS `learns` (
  `player_id` int(11) DEFAULT NULL,
  `inventory_id` int(11) DEFAULT NULL,
  `learn_id` int(11) DEFAULT NULL,
  `visible` bit(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.levels
CREATE TABLE IF NOT EXISTS `levels` (
  `level` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `experience` int(11) unsigned NOT NULL,
  PRIMARY KEY (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.ranking
CREATE TABLE IF NOT EXISTS `ranking` (
  `index` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `matches` int(10) unsigned NOT NULL,
  `wins` int(10) unsigned NOT NULL,
  `draws` int(10) unsigned NOT NULL,
  `points` int(10) unsigned NOT NULL,
  `mom` int(10) unsigned NOT NULL,
  `valid_goals` int(10) unsigned NOT NULL,
  `valid_assists` int(10) unsigned NOT NULL,
  `valid_interception` int(10) unsigned NOT NULL,
  `valid_shooting` int(10) unsigned NOT NULL,
  `valid_stealing` int(10) unsigned NOT NULL,
  `valid_tackling` int(10) unsigned NOT NULL,
  `shooting` int(10) unsigned NOT NULL,
  `stealing` int(10) unsigned NOT NULL,
  `tackling` int(10) unsigned NOT NULL,
  `total_points` int(10) unsigned NOT NULL,
  `month_matches` int(10) unsigned NOT NULL,
  `month_wins` int(10) unsigned NOT NULL,
  `month_draws` int(10) unsigned NOT NULL,
  `month_points` int(10) unsigned NOT NULL,
  `month_mom` int(10) unsigned NOT NULL,
  `month_valid_goals` int(10) unsigned NOT NULL,
  `month_valid_assists` int(10) unsigned NOT NULL,
  `month_valid_interception` int(10) unsigned NOT NULL,
  `month_valid_shooting` int(10) unsigned NOT NULL,
  `month_valid_stealing` int(10) unsigned NOT NULL,
  `month_valid_tackling` int(10) unsigned NOT NULL,
  `month_shooting` int(10) unsigned NOT NULL,
  `month_stealing` int(10) unsigned NOT NULL,
  `month_tackling` int(10) unsigned NOT NULL,
  `month_total_points` int(10) unsigned NOT NULL,
  PRIMARY KEY (`index`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.servers
CREATE TABLE IF NOT EXISTS `servers` (
  `id` smallint(6) unsigned NOT NULL,
  `filter` smallint(6) NOT NULL,
  `port` smallint(6) NOT NULL,
  `address` varchar(64) NOT NULL,
  `name` varchar(30) NOT NULL,
  `min_level` tinyint(4) unsigned NOT NULL,
  `max_level` tinyint(4) unsigned NOT NULL,
  `max_users` smallint(6) unsigned NOT NULL,
  `connected_users` smallint(6) unsigned NOT NULL DEFAULT '0',
  `type` enum('NORMAL','PRACTICE','CLUB','TOURNAMENT','PRIVATE') NOT NULL,
  `exp_factor` int(11) NOT NULL,
  `point_factor` int(11) NOT NULL,
  `kash_factor` int(11) NOT NULL,
  `practice_rewards` bit(1) NOT NULL,
  `online` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.sessions
CREATE TABLE IF NOT EXISTS `sessions` (
  `id` int(10) NOT NULL,
  `user_id` int(10) NOT NULL,
  `player_id` int(10) NOT NULL,
  `expiration` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `hash` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.skills
CREATE TABLE IF NOT EXISTS `skills` (
  `player_id` int(11) unsigned NOT NULL,
  `inventory_id` int(11) unsigned NOT NULL,
  `skill_id` int(11) unsigned NOT NULL,
  `expiration` int(11) unsigned NOT NULL,
  `selection_index` tinyint(4) unsigned NOT NULL,
  `timestamp_expire` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `visible` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.status
CREATE TABLE IF NOT EXISTS `status` (
  `online_users` int(11) unsigned NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.


-- Dumping structure for table kicksdb.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(15) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `last_ip` varchar(64) NOT NULL DEFAULT '',
  `creation` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_connection` timestamp NULL DEFAULT NULL,
  `online` int(11) NOT NULL DEFAULT '-1',
  `server` smallint(6) NOT NULL DEFAULT '-1',
  `kash` int(11) unsigned NOT NULL DEFAULT '0',
  `settings_camera` tinyint(4) NOT NULL DEFAULT '5',
  `settings_shadows` tinyint(4) NOT NULL DEFAULT '0',
  `settings_names` tinyint(4) NOT NULL DEFAULT '2',
  `vol_effects` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `vol_music` tinyint(3) unsigned NOT NULL DEFAULT '10',
  `settings_invites` tinyint(4) NOT NULL DEFAULT '1',
  `settings_whispers` tinyint(4) NOT NULL DEFAULT '1',
  `settings_country` int(11) unsigned NOT NULL DEFAULT '0',
  `last_char_deletion` timestamp NULL DEFAULT NULL,
  `slot_one` int(11) unsigned DEFAULT NULL,
  `slot_two` int(11) unsigned DEFAULT NULL,
  `slot_three` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- Data exporting was unselected.
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
