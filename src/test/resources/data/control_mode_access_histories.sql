set
    FOREIGN_KEY_CHECKS = 0;
truncate table `control_mode_access_histories`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `control_mode_access_histories`
    ALTER COLUMN id RESTART WITH 1000000000;

insert into `control_mode_access_histories`
(access_result_type, created_at, updated_at, created_by, updated_by)
values ('FAILED', now(), now(), 1000000000, 1000000000),
       ('SUCCEEDED', now(), now(), 0, 0),
       ('FAILED', now(), now(), 0, 0);
