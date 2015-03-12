DROP PROCEDURE IF EXISTS update_ranking;

delimiter |
CREATE PROCEDURE update_ranking()
BEGIN
	DECLARE workDone INT;

	DECLARE matches INT;
	DECLARE wins INT;
	DECLARE draws INT;
	DECLARE points INT;
	DECLARE MOM INT;
	DECLARE valid_goals INT;
	DECLARE valid_assists INT;
	DECLARE valid_interception INT;
	DECLARE valid_shooting INT;
	DECLARE valid_stealing INT;
	DECLARE valid_tackling INT;
	DECLARE shooting INT;
	DECLARE stealing INT;
	DECLARE tackling INT;
	DECLARE total_points INT;

	DECLARE month_matches INT;
	DECLARE month_wins INT;
	DECLARE month_draws INT;
	DECLARE month_points INT;
	DECLARE month_MOM INT;
	DECLARE month_valid_goals INT;
	DECLARE month_valid_assists INT;
	DECLARE month_valid_interception INT;
	DECLARE month_valid_shooting INT;
	DECLARE month_valid_stealing INT;
	DECLARE month_valid_tackling INT;
	DECLARE month_shooting INT;
	DECLARE month_stealing INT;
	DECLARE month_tackling INT;
	DECLARE month_total_points INT;

	DECLARE curs_matches CURSOR FOR SELECT id FROM characters ORDER BY history_matches DESC LIMIT 65535;
	DECLARE curs_wins CURSOR FOR SELECT id FROM characters ORDER BY history_wins DESC LIMIT 65535;
	DECLARE curs_draws CURSOR FOR SELECT id FROM characters ORDER BY history_draws DESC LIMIT 65535;
	DECLARE curs_points CURSOR FOR SELECT id FROM characters ORDER BY history_points DESC LIMIT 65535;
	DECLARE curs_mom CURSOR FOR SELECT id FROM characters ORDER BY history_MOM DESC LIMIT 65535;
	DECLARE curs_valid_goals CURSOR FOR SELECT id FROM characters ORDER BY history_valid_goals DESC LIMIT 65535;
	DECLARE curs_valid_assists CURSOR FOR SELECT id FROM characters ORDER BY history_valid_assists DESC LIMIT 65535;
	DECLARE curs_valid_interception CURSOR FOR SELECT id FROM characters ORDER BY history_valid_interception DESC LIMIT 65535;
	DECLARE curs_valid_shooting CURSOR FOR SELECT id FROM characters ORDER BY history_valid_shooting DESC LIMIT 65535;
	DECLARE curs_valid_stealing CURSOR FOR SELECT id FROM characters ORDER BY history_valid_stealing DESC LIMIT 65535;
	DECLARE curs_valid_tackling CURSOR FOR SELECT id FROM characters ORDER BY history_valid_tackling DESC LIMIT 65535;
	DECLARE curs_shooting CURSOR FOR SELECT id FROM characters ORDER BY history_shooting DESC LIMIT 65535;
	DECLARE curs_stealing CURSOR FOR SELECT id FROM characters ORDER BY history_stealing DESC LIMIT 65535;
	DECLARE curs_tackling CURSOR FOR SELECT id FROM characters ORDER BY history_tackling DESC LIMIT 65535;
	DECLARE curs_total_points CURSOR FOR SELECT id FROM characters ORDER BY history_total_points DESC LIMIT 65535;

	DECLARE curs_month_matches CURSOR FOR SELECT id FROM characters ORDER BY history_month_matches DESC LIMIT 65535;
	DECLARE curs_month_wins CURSOR FOR SELECT id FROM characters ORDER BY history_month_wins DESC LIMIT 65535;
	DECLARE curs_month_draws CURSOR FOR SELECT id FROM characters ORDER BY history_month_draws DESC LIMIT 65535;
	DECLARE curs_month_points CURSOR FOR SELECT id FROM characters ORDER BY history_month_points DESC LIMIT 65535;
	DECLARE curs_month_mom CURSOR FOR SELECT id FROM characters ORDER BY history_month_MOM DESC LIMIT 65535;
	DECLARE curs_month_valid_goals CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_goals DESC LIMIT 65535;
	DECLARE curs_month_valid_assists CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_assists DESC LIMIT 65535;
	DECLARE curs_month_valid_interception CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_interception DESC LIMIT 65535;
	DECLARE curs_month_valid_shooting CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_shooting DESC LIMIT 65535;
	DECLARE curs_month_valid_stealing CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_stealing DESC LIMIT 65535;
	DECLARE curs_month_valid_tackling CURSOR FOR SELECT id FROM characters ORDER BY history_month_valid_tackling DESC LIMIT 65535;
	DECLARE curs_month_shooting CURSOR FOR SELECT id FROM characters ORDER BY history_month_shooting DESC LIMIT 65535;
	DECLARE curs_month_stealing CURSOR FOR SELECT id FROM characters ORDER BY history_month_stealing DESC LIMIT 65535;
	DECLARE curs_month_tackling CURSOR FOR SELECT id FROM characters ORDER BY history_month_tackling DESC LIMIT 65535;
	DECLARE curs_month_total_points CURSOR FOR SELECT id FROM characters ORDER BY history_month_total_points DESC LIMIT 65535;

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET workDone = 1;

	OPEN curs_matches;
	OPEN curs_wins;
	OPEN curs_draws;
	OPEN curs_points;
	OPEN curs_mom;
	OPEN curs_valid_goals;
	OPEN curs_valid_assists;
	OPEN curs_valid_interception;
	OPEN curs_valid_shooting;
	OPEN curs_valid_stealing;
	OPEN curs_valid_tackling;
	OPEN curs_shooting;
	OPEN curs_stealing;
	OPEN curs_tackling;
	OPEN curs_total_points;

	OPEN curs_month_matches;
	OPEN curs_month_wins;
	OPEN curs_month_draws;
	OPEN curs_month_points;
	OPEN curs_month_mom;
	OPEN curs_month_valid_goals;
	OPEN curs_month_valid_assists;
	OPEN curs_month_valid_interception;
	OPEN curs_month_valid_shooting;
	OPEN curs_month_valid_stealing;
	OPEN curs_month_valid_tackling;
	OPEN curs_month_shooting;
	OPEN curs_month_stealing;
	OPEN curs_month_tackling;
	OPEN curs_month_total_points;

	-- Clear content of the ranking table before updating it
	DELETE FROM ranking;
	-- Reset AUTO_INCREMENT
	ALTER TABLE ranking AUTO_INCREMENT = 1;

	SET workDone = 0;
	REPEAT
		FETCH curs_matches INTO matches;
		FETCH curs_wins INTO wins;
		FETCH curs_draws INTO draws;
		FETCH curs_points INTO points;
		FETCH curs_mom INTO mom;
		FETCH curs_valid_goals INTO valid_goals;
		FETCH curs_valid_assists INTO valid_assists;
		FETCH curs_valid_interception INTO valid_interception;
		FETCH curs_valid_shooting INTO valid_shooting;
		FETCH curs_valid_stealing INTO valid_stealing;
		FETCH curs_valid_tackling INTO valid_tackling;
		FETCH curs_shooting INTO shooting;
		FETCH curs_stealing INTO stealing;
		FETCH curs_tackling INTO tackling;
		FETCH curs_total_points INTO total_points;

		FETCH curs_month_matches INTO month_matches;
		FETCH curs_month_wins INTO month_wins;
		FETCH curs_month_draws INTO month_draws;
		FETCH curs_month_points INTO month_points;
		FETCH curs_month_mom INTO month_mom;
		FETCH curs_month_valid_goals INTO month_valid_goals;
		FETCH curs_month_valid_assists INTO month_valid_assists;
		FETCH curs_month_valid_interception INTO month_valid_interception;
		FETCH curs_month_valid_shooting INTO month_valid_shooting;
		FETCH curs_month_valid_stealing INTO month_valid_stealing;
		FETCH curs_month_valid_tackling INTO month_valid_tackling;
		FETCH curs_month_shooting INTO month_shooting;
		FETCH curs_month_stealing INTO month_stealing;
		FETCH curs_month_tackling INTO month_tackling;
		FETCH curs_month_total_points INTO month_total_points;

		INSERT INTO ranking (
			matches,wins,draws,points,MOM,valid_goals,valid_assists,valid_interception,
			valid_shooting,valid_stealing,valid_tackling,shooting,stealing,tackling,total_points,
			month_matches,month_wins,month_draws,month_points,month_MOM,month_valid_goals,
			month_valid_assists,month_valid_interception,month_valid_shooting,month_valid_stealing,
			month_valid_tackling,month_shooting,month_stealing,month_tackling,month_total_points
			) VALUES (
			matches,wins,draws,points,MOM,valid_goals,valid_assists,valid_interception,
			valid_shooting,valid_stealing,valid_tackling,shooting,stealing,tackling,total_points,
			month_matches,month_wins,month_draws,month_points,month_MOM,month_valid_goals,
			month_valid_assists,month_valid_interception,month_valid_shooting,month_valid_stealing,
			month_valid_tackling,month_shooting,month_stealing,month_tackling,month_total_points
			);
	UNTIL workDone END REPEAT;

	CLOSE curs_matches;
	CLOSE curs_wins;
	CLOSE curs_draws;
	CLOSE curs_points;
	CLOSE curs_mom;
	CLOSE curs_valid_goals;
	CLOSE curs_valid_assists;
	CLOSE curs_valid_interception;
	CLOSE curs_valid_shooting;
	CLOSE curs_valid_stealing;
	CLOSE curs_valid_tackling;
	CLOSE curs_shooting;
	CLOSE curs_stealing;
	CLOSE curs_tackling;
	CLOSE curs_total_points;

	CLOSE curs_month_matches;
	CLOSE curs_month_wins;
	CLOSE curs_month_draws;
	CLOSE curs_month_points;
	CLOSE curs_month_mom;
	CLOSE curs_month_valid_goals;
	CLOSE curs_month_valid_assists;
	CLOSE curs_month_valid_interception;
	CLOSE curs_month_valid_shooting;
	CLOSE curs_month_valid_stealing;
	CLOSE curs_month_valid_tackling;
	CLOSE curs_month_shooting;
	CLOSE curs_month_stealing;
	CLOSE curs_month_tackling;
	CLOSE curs_month_total_points;
END|

delimiter ;

CALL update_ranking;
DROP PROCEDURE IF EXISTS update_ranking;
