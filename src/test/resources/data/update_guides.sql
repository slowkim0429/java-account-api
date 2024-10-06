set FOREIGN_KEY_CHECKS = 0;
truncate table `update_guides`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `update_guides`
    ALTER COLUMN id RESTART WITH 1000000000;


INSERT INTO `update_guides` (`created_at`, `updated_at`, `created_by`, `updated_by`, `date_by_update`,
                             `description`, `file_type`, `file_url`, `is_exposed`, `name`, `service_type`,
                             `sub_description`, `sub_title`, `title`)
VALUES (now(), now(), 1, 1, now(), 'description', 'VIDEO',
        'https://squars.io', true, 'name1', 'WORKSPACE', 'sub description', 'sub title', 'title'),
       (now(), now(), 1, 1, now(), 'description', 'IMAGE',
        'https://squars.io', true, 'name2', 'WORKSPACE', 'sub description', 'sub title', 'title'),
       (now(), now(), 1, 1, now(), 'description', 'IMAGE',
        'https://squars.io', true, 'name3', 'SQUARS', 'sub description', 'sub title', 'title'),
       (now(), now(), 1, 1, now(), 'description', 'VIDEO',
        'https://squars.io', false, 'name4', 'SQUARS', 'sub description', 'sub title', 'title');

