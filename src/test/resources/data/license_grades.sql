set FOREIGN_KEY_CHECKS = 0;
truncate table `license_grades`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `license_grades`
    ALTER COLUMN id RESTART WITH 1000000000;


INSERT INTO license_grades (id, created_at, updated_at, created_by, updated_by, description, name, grade_type, status)
VALUES (1000000000, '2022-07-26 13:43:38', '2022-07-26 13:43:39', 1000000000, 1000000000, 'Free Plus License Grade',
        'Free+', 'FREE_PLUS', 'APPROVED'),
       (1000000001, '2022-07-26 13:45:01', '2022-07-26 13:44:34', 1000000000, 1000000000, 'Enterprise License Grade',
        'Enterprise', 'ENTERPRISE', 'APPROVED'),
       (1000000002, '2022-07-26 13:44:34', '2022-07-26 13:44:34', 1000000000, 1000000000, 'Professional License Grade',
        'Professional', 'PROFESSIONAL', 'APPROVED'),
       (1000000003, '2022-07-26 13:44:34', '2022-07-26 13:44:33', 1000000000, 1000000000, 'Standard License Grade',
        'Standard', 'STANDARD', 'APPROVED'),
       (1000000004, '2022-07-26 13:45:01', '2022-07-26 13:44:34', 1000000000, 1000000000,
        'Enterprise License Grade Version 2', 'Enterprise', 'ENTERPRISE', 'REGISTER'),
       (1000000005, '2022-07-26 13:45:01', '2022-07-26 13:44:34', 1000000000, 1000000000,
        'Enterprise License Grade Version 3', 'Enterprise 3', 'ENTERPRISE', 'REGISTER'),
       (1000000006, '2022-07-26 13:45:01', '2022-07-26 13:44:34', 1000000000, 1000000000,
        'This is the Standard License Grade For 2023 Users', 'Standard License Grade For 2023', 'STANDARD', 'REJECT');
