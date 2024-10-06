set FOREIGN_KEY_CHECKS = 0;
truncate table `authority_groups`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `authority_groups`
    ALTER COLUMN `id` RESTART WITH 10000000000;

INSERT INTO `authority_groups`
(id, created_at, updated_at,  created_by, updated_by, name, description, status)
VALUES (10000000000, now(), now(), 1000000000, 1000000000, 'Marketer Admin Level 1', null , 'UNUSE'),
       (10000000001, now(), now(), 1000000000, 1000000000, 'Marketer Admin Level 2', null , 'UNUSE'),
       (10000000002, now(), now(), 1000000000, 1000000000, 'Marketer Admin Master', null , 'USE');
