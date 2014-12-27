-- --------------------------------------------------------------------
-- Patch to update experience required for each level in 'levels' table
-- --------------------------------------------------------------------

-- Delete the table contents
DELETE FROM levels;

-- Reset index
ALTER TABLE levels AUTO_INCREMENT = 1;

-- Insert new contents
INSERT INTO levels (experience) VALUES (0);
INSERT INTO levels (experience) VALUES (300);
INSERT INTO levels (experience) VALUES (700);
INSERT INTO levels (experience) VALUES (1300);
INSERT INTO levels (experience) VALUES (2200);
INSERT INTO levels (experience) VALUES (3500);
INSERT INTO levels (experience) VALUES (5300);
INSERT INTO levels (experience) VALUES (7800);
INSERT INTO levels (experience) VALUES (10800);
INSERT INTO levels (experience) VALUES (12000);
INSERT INTO levels (experience) VALUES (15100);
INSERT INTO levels (experience) VALUES (19000);
INSERT INTO levels (experience) VALUES (23800);
INSERT INTO levels (experience) VALUES (29600);
INSERT INTO levels (experience) VALUES (36500);
INSERT INTO levels (experience) VALUES (44500);
INSERT INTO levels (experience) VALUES (47000);
INSERT INTO levels (experience) VALUES (56000);
INSERT INTO levels (experience) VALUES (66000);
INSERT INTO levels (experience) VALUES (71000);
INSERT INTO levels (experience) VALUES (81000);
INSERT INTO levels (experience) VALUES (93000);
INSERT INTO levels (experience) VALUES (107000);
INSERT INTO levels (experience) VALUES (123000);
INSERT INTO levels (experience) VALUES (141000);
INSERT INTO levels (experience) VALUES (150000);
INSERT INTO levels (experience) VALUES (170000);
INSERT INTO levels (experience) VALUES (192000);
INSERT INTO levels (experience) VALUES (216000);
INSERT INTO levels (experience) VALUES (228000);
INSERT INTO levels (experience) VALUES (252000);
INSERT INTO levels (experience) VALUES (279000);
INSERT INTO levels (experience) VALUES (309000);
INSERT INTO levels (experience) VALUES (342000);
INSERT INTO levels (experience) VALUES (358000);
INSERT INTO levels (experience) VALUES (394000);
INSERT INTO levels (experience) VALUES (433000);
INSERT INTO levels (experience) VALUES (475000);
INSERT INTO levels (experience) VALUES (520000);
INSERT INTO levels (experience) VALUES (542000);
INSERT INTO levels (experience) VALUES (592000);
INSERT INTO levels (experience) VALUES (652000);
INSERT INTO levels (experience) VALUES (722000);
INSERT INTO levels (experience) VALUES (732000);
INSERT INTO levels (experience) VALUES (802000);
INSERT INTO levels (experience) VALUES (872000);
INSERT INTO levels (experience) VALUES (942000);
INSERT INTO levels (experience) VALUES (1022000);
INSERT INTO levels (experience) VALUES (1122000);
INSERT INTO levels (experience) VALUES (1272000);
INSERT INTO levels (experience) VALUES (1432000);
INSERT INTO levels (experience) VALUES (1602000);
INSERT INTO levels (experience) VALUES (1782000);
INSERT INTO levels (experience) VALUES (1972000);
INSERT INTO levels (experience) VALUES (2172000);