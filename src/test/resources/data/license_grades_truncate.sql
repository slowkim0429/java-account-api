set FOREIGN_KEY_CHECKS = 0;
truncate table `license_grades`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `license_grades`
    ALTER COLUMN id RESTART WITH 1000000000;
