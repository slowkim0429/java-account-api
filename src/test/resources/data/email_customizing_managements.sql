set
    FOREIGN_KEY_CHECKS = 0;
truncate table `email_customizing_managements`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `email_customizing_managements`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `email_customizing_managements` (id, email_type, contents_inline_image_url, description, use_status,
                                             created_at, updated_at, created_by, updated_by)
VALUES (1000000000, 'WELCOME', 'https://', null, 'UNUSE', NOW(), NOW(), 1000000000, 1000000000),
       (1000000001, 'WELCOME', 'https://', null, 'USE', NOW(), NOW(), 1000000000, 1000000000)
