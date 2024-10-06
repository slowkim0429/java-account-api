set FOREIGN_KEY_CHECKS = 0;
truncate table `event_popups`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `event_popups`
    ALTER COLUMN id RESTART WITH 1000000000;
