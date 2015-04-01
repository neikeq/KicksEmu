-- ------------------------------------
-- Creates a garbage collection event
-- The event runs every day at 00:00:00
-- ------------------------------------

delimiter |

CREATE EVENT daily_gc
    ON SCHEDULE
        EVERY 1 DAY
        STARTS CURDATE() + INTERVAL 1 DAY
        ON COMPLETION PRESERVE
    COMMENT 'Garbage collection'
    DO
        BEGIN
            -- Remove expired items
            DELETE FROM items WHERE
             ((expiration = 9201007 OR expiration = 9201030) AND timestamp_expire <= CURRENT_TIMESTAMP)
             OR ((expiration = 9101010 OR expiration = 9101050 OR expiration = 9101100) AND usages <= 0);
            -- Remove expired skills
            DELETE FROM skills WHERE expiration <> 9201999 AND timestamp_expire <= CURRENT_TIMESTAMP;
            -- Remove expired ceremonies
            DELETE FROM ceres WHERE expiration <> 9201999 AND timestamp_expire <= CURRENT_TIMESTAMP;
            -- Remove expired bans
            DELETE FROM bans WHERE expire <= CURRENT_TIMESTAMP;
            -- Remove expired entries in blacklists
            DELETE FROM blacklist WHERE expire <= CURRENT_TIMESTAMP;
        END |

delimiter ;
