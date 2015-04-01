-- ------------------------------------------
-- Creates a session garbage collection event
-- The event runs once every hour
-- ------------------------------------------

delimiter |

CREATE EVENT session_gc
    ON SCHEDULE
        EVERY 1 HOUR
        STARTS CURRENT_TIMESTAMP + INTERVAL 1 HOUR
        ON COMPLETION PRESERVE
    COMMENT 'Sessions garbage collection'
    DO
        BEGIN
            -- Remove expired sessions
            DELETE FROM sessions WHERE expiration <= CURRENT_TIMESTAMP;
        END |

delimiter ;
