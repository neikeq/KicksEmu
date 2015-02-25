-- ------------------------------------------
-- Creates a monthly ranking reset event
-- The event runs the first day of each month
-- ------------------------------------------

delimiter |

CREATE EVENT monthly_ranking_reset
    ON SCHEDULE
        EVERY 1 MONTH
        STARTS DATE_SUB(LAST_DAY(DATE_ADD(NOW(), INTERVAL 1 MONTH)),
        INTERVAL DAY(LAST_DAY(DATE_ADD(NOW(), INTERVAL 1 MONTH))) - 1 DAY)
    COMMENT 'Garbage collection'
    DO
        BEGIN
            UPDATE characters SET
            history_month_matches = 0,
            history_month_wins = 0,
            history_month_draws = 0,
            history_month_points = 0,
            history_month_MOM = 0,
            history_month_valid_goals = 0,
            history_month_valid_assists = 0,
            history_month_valid_interception = 0,
            history_month_valid_shooting = 0,
            history_month_valid_stealing = 0,
            history_month_valid_tackling = 0,
            history_month_shooting = 0,
            history_month_stealing = 0,
            history_month_tackling = 0,
            history_month_total_points = 0;
        END |

delimiter ;
