set FOREIGN_KEY_CHECKS = 0;
truncate table `event_popups`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `event_popups`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `event_popups` (`event_type`, `service_type`, `image_url`, `image_link_url`, `content_description`,
                            `exposure_option_type`, `exposure_option_data_type`, `exposure_option_value`, `email_title`,
                            `email_content_inline_image_url`, `button_label`, `button_url`, `is_exposed`, `input_guide`,
                            `name`, `coupon_id`, `created_by`, `created_at`, `updated_by`, `updated_at`)
VALUES ('MARKETING', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_NEVER',
        null, null, 'common!', 'https://squars.io', 'Submit', 'https://squars.io', false, 'input your email', 'test',
        0, 1000000000, now(), 1000000000, now()),
       ('MARKETING', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_NEVER',
        null, null, 'common!', 'https://squars.io', 'Submit', 'https://squars.io', false, 'input your email', 'test',
        0, 1000000000, now(), 1000000000, now()),
       ('SUBMISSION', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_DAY',
        'NUMBER', '3', 'common!', 'https://squars.io', 'Submit', 'https://squars.io', false, 'input your email', 'test',
        0, 1000000000, now(), 1000000000, now()),
       ('SUBMISSION', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_DAY',
        'NUMBER', '3', 'common!', 'https://squars.io', 'Submit', 'https://squars.io', false, 'input your email', 'test',
        1000000000, 1000000000, now(), 1000000000, now()),
       ('SUBMISSION', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_DAY',
        'NUMBER', '3', 'common!', 'https://squars.io', 'Submit', 'https://squars.io', true, 'input your email', 'test',
        1000000004, 1000000000, now(), 1000000000, now()),
       ('SUBMISSION', 'SQUARS', 'https://squars.io', 'https://squars.io', '<p>test</p>', 'SELECTIVE_DEACTIVATION_DAY',
        'NUMBER', '3', 'common!', 'https://squars.io', 'Submit', 'https://squars.io', false, 'input your email', 'test',
        1000000001, 1000000000, now(), 1000000000, now());
