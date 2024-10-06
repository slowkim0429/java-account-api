set
    FOREIGN_KEY_CHECKS = 0;
truncate table `mobile_force_update_minimum_versions`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `mobile_force_update_minimum_versions`
    ALTER COLUMN id RESTART WITH 1000000000;
