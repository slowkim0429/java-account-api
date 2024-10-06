set
    FOREIGN_KEY_CHECKS = 0;
truncate table `items`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `items`
    ALTER COLUMN id RESTART WITH 1000000000;
