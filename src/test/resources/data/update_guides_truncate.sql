set FOREIGN_KEY_CHECKS = 0;
truncate table `update_guides`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `update_guides`
    ALTER COLUMN id RESTART WITH 1000000000;
