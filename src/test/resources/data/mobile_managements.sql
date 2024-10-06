set
    FOREIGN_KEY_CHECKS = 0;
truncate table `mobile_managements`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `mobile_managements`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `mobile_managements` (control_mode_password, notice_message, notice_is_exposed, created_at, updated_at, created_by, updated_by)
VALUES('$2b$12$4EuZAKWFnq1CaPzEgPhvg.AwGePtmGKuX8a9TF9uq0/NiKDoS2RF6', 'isNotExposed', false, now(), now(), 1000000000, 1000000000)
