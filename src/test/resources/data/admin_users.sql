set FOREIGN_KEY_CHECKS = 0;
truncate table `admin_users`;
truncate table `admin_user_roles`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `admin_users`
    ALTER COLUMN `id` RESTART WITH 1000000000;
ALTER TABLE `admin_user_roles`
    ALTER COLUMN `id` RESTART WITH 1000000000;


INSERT INTO `admin_users`
(id, created_at, updated_at, approval_status, email, language,service_region_locale_mapping_code, service_region_locale_mapping_id, nickname, password, profile_color,
profile_image, service_region_aws_code, service_region_code, service_region_id, status, created_by, updated_by, authority_group_id)
VALUES (1000000000, now(), now(), 'REGISTER', 'admin-user1@virnect.com', 'ko_KR', 'KR', 1000000118, 'admin-user1', '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'GREEN', null, 'ap-northeast-2', 'KR', 1000000000, 'JOIN', 0, 0, null),
       (1000000001, now(), now(), 'APPROVED', 'admin-user2@virnect.com', 'ko_KR', 'KR', 1000000118, 'admin-user2', '$2a$10$sszmquIiDNny7GTIyo50UuLv2KKzlFrzeHpt/qJQZPotaG5pM66de', 'GREEN', null, 'ap-northeast-2', 'KR', 1000000000, 'JOIN', 0, 0, 10000000002),
       (1000000002, now(), now(), 'REGISTER', '', '', '', 1000000118, '', '', 'GREEN', null, '', '', 1000000000, 'RESIGN', 0, 0, null ),
       (1000000003, now(), now(), 'APPROVED', 'admin-master@virnect.com', 'ko_KR', 'KR', 1000000118, 'admin-master', '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'GREEN', null, 'ap-northeast-2', 'KR', 1000000000, 'JOIN', 0, 0, 10000000002);



INSERT INTO `admin_user_roles`
(`admin_user_id`, `role`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`)
VALUES (1000000000, 'ROLE_ADMIN_USER', 'USE', now(), now(), 1000000000, 1000000000),
       (1000000001, 'ROLE_ADMIN_USER', 'USE', now(), now(), 1000000001, 1000000001),
       (1000000002, 'ROLE_ADMIN_USER', 'USE', now(), now(), 1000000002, 1000000002),
       (1000000003, 'ROLE_ADMIN_MASTER', 'USE', now(), now(), 1000000003, 1000000003);
