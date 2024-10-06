set FOREIGN_KEY_CHECKS = 0;
truncate table `users`;
truncate table `user_roles`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `users`
    ALTER COLUMN `id` RESTART WITH 1000000000;

INSERT INTO `users`
(`created_at`, `updated_at`,
 `email`, `email_domain`, `service_region_locale_mapping_id`, `service_region_locale_mapping_code`, `service_region_id`,
 `service_region_code`, `service_region_aws_code`,
 `language`, `market_info_receive`, `nickname`, `password`, `profile_image`, `profile_color`, `status`,
 `organization_id`, `organization_status`, `privacy_policy`, `terms_of_service`, `referrer_url`,`created_by`, `updated_by`, `zone_id`)
VALUES ('2021-08-24 01:48:54', '2021-08-24 01:48:54', 'slowkim@gggg.com','gggg.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'admin',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'TEAL', 'JOIN', 1000000001, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-11-16 03:20:24', '2021-11-16 03:20:24', 'organiowner@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organizationowner',
        '$2a$10$sszmquIiDNny7GTIyo50UuLv2KKzlFrzeHpt/qJQZPotaG5pM66de', 'default', 'BLUE', 'JOIN', 1000000000, 'USE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'partnermaster@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partnermaster',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'PURPLE', 'JOIN', 1000000002, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-11-16 03:20:24', '2021-11-16 03:20:24', 'organization1@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organization1',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'VIOLET', 'JOIN', 1000000003, 'USE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-11-16 03:20:24', '2021-11-16 03:20:24', 'organization2@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organization2',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'PURPLE', 'JOIN', 1000000004, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'user1@guest.com','guest.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'user',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'GREEN', 'JOIN', 1000000005, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'partner1@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partner1',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'TEAL', 'JOIN', 1000000006, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'partner2@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partner2',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'BLUE', 'JOIN', 1000000007, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'inviteUser@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'inviteUser',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'VIOLET', 'JOIN', 1000000008, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'organiRegister@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organizationRegister',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'GREEN', 'JOIN', 1000000009, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'salesOrderMaster@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organizationRegister',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'TEAL', 'JOIN', 1000000010, 'USE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'updatePassword@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'organizationRegister',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'PINK', 'JOIN', 1000000011, 'UNUSE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'partnermaster2@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partnermaster2',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'VIOLET', 'JOIN', 1000000012, 'USE', 'ACCEPT', 'ACCEPT', null, 1000000000, 1000000000 ,'ROK'),
       ('2021-12-14 04:38:18', '2021-12-14 04:38:18', 'onpremise@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partnermaster2',
        '$2a$10$iZzPLmpru2zFuymG8GeTheRdgGJySf3HM.ARQFzV/3aB2eTOELLeu', 'default', 'GREEN', 'JOIN', 1000000013, 'USE', 'ACCEPT', 'ACCEPT', 'https://www.naver.com/', 1000000000, 1000000000 ,'ROK'),
       (DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP), 'organiowner-one@virnect.com','virnect.com', 1000000118, 'KR', 1000000000, 'KR', 'ap-northeast-2', 'ko_KR', 'ACCEPT', 'partnermaster2',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'TEAL', 'JOIN', 1000000014, 'USE', 'ACCEPT', 'ACCEPT', 'https://www.naver.com/', 1000000000, 1000000000 ,'ROK'),
       (DATEADD('DAY', -28, CURRENT_TIMESTAMP), DATEADD('DAY', -28, CURRENT_TIMESTAMP), 'organiowner-one@gmail.com','gmail.com', 1000000234, 'US', 1000000001, 'US', 'us-east-1', 'ko_KR', 'REJECT', 'partnermaster2',
        '$2a$10$H1S0dB87CpdHhfMwj7JqFeQOlXwTGeMZrz5.njlXmnQ2ZXVVoWiDG', 'default', 'TEAL', 'JOIN', 1000000015, 'USE', 'ACCEPT', 'ACCEPT', 'https://www.naver.com/', 1000000000, 1000000000 ,'US/Pacific')
;

ALTER TABLE `user_roles`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `user_roles`
(`user_id`, `role`, `status`, `created_at`, `updated_at`, `created_by`, `updated_by`)
VALUES (1000000000, 'ROLE_USER', 'USE', now(), now(), 1000000000, 1000000000),
       (1000000000, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000000, 1000000000),

       (1000000001, 'ROLE_USER', 'USE', now(), now(), 1000000001, 1000000001),
       (1000000001, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000001, 1000000001),

       (1000000002, 'ROLE_USER', 'USE', now(), now(), 1000000001, 1000000001),

       (1000000003, 'ROLE_USER', 'USE', now(), now(), 1000000004, 1000000004),

       (1000000004, 'ROLE_USER', 'USE', now(), now(), 1000000005, 1000000005),

       (1000000005, 'ROLE_USER', 'USE', now(), now(), 1000000009, 1000000009),

       (1000000006, 'ROLE_USER', 'USE', now(), now(), 1000000006, 1000000006),

       (1000000007, 'ROLE_USER', 'USE', now(), now(), 1000000007, 1000000007),

       (1000000008, 'ROLE_USER', 'USE', now(), now(), 1000000007, 1000000007),

       (1000000009, 'ROLE_USER', 'USE', now(), now(), 1000000009, 1000000009),
       (1000000009, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000009, 1000000009),

       (1000000012, 'ROLE_USER', 'USE', now(), now(), 1000000010, 1000000010),
       (1000000012, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000010, 1000000010),

       (1000000013, 'ROLE_USER', 'USE', now(), now(), 1000000010, 1000000010),
       (1000000013, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000010, 1000000010),

       (1000000014, 'ROLE_USER', 'USE', now(), now(), 1000000014, 1000000014),
       (1000000014, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000014, 1000000014),

       (1000000015, 'ROLE_USER', 'USE', now(), now(), 1000000015, 1000000015),

       (1000000016, 'ROLE_USER', 'USE', now(), now(), 1000000016, 1000000016),

       (1000000017, 'ROLE_USER', 'USE', now(), now(), 1000000017, 1000000017),

       (1000000018, 'ROLE_USER', 'USE', now(), now(), 1000000018, 1000000018),
       (1000000018, 'ROLE_ORGANIZATION_OWNER', 'USE', now(), now(), 1000000018, 1000000018);

