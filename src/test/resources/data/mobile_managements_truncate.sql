set
    FOREIGN_KEY_CHECKS = 0;
truncate table `mobile_managements`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `mobile_managements`
    ALTER COLUMN id RESTART WITH 1000000000;
