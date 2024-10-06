set
    FOREIGN_KEY_CHECKS = 0;
truncate table `coupons`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `coupons`
    ALTER COLUMN id RESTART WITH 1000000000;


insert into coupons
(id, created_at, updated_at, created_by, updated_by, status, code, name, description, coupon_type, benefit_option, benefit_value,
 coupon_license_grade_matching_type, coupon_recurring_interval_matching_type, max_count, expired_at, use_status)
values (1000000000, now(), now(), 1000000000, 1000000000, 'REGISTER', 'ABCDEFGHIJKLMN1', 'name 1', 'description 1', 'UPGRADE_LICENSE_ATTRIBUTE', 'MAXIMUM_WORKSPACE', 3,
        'STANDARD', 'MONTH', 100, TIMESTAMPADD(MINUTE , 10, now()), 'UNUSE'),
       (1000000001, now(), now(), 1000000000, 1000000000, 'REJECT', 'ABCDEFGHIJKLMN2', 'name 2', 'description 2', 'UPGRADE_LICENSE_ATTRIBUTE', 'MAXIMUM_GROUP', 5,
        'PROFESSIONAL', 'MONTH', 1000, TIMESTAMPADD(MINUTE, 10, now()), 'UNUSE'),
       (1000000002, now(), now(), 1000000000, 1000000000, 'REJECT', 'ABCDEFGHIJKLMN3', 'name 3', 'description 3', 'UPGRADE_LICENSE_ATTRIBUTE', 'MAXIMUM_WORKSPACE', 2,
        'PROFESSIONAL', 'YEAR', 1000, TIMESTAMPADD(MINUTE, 10, now()), 'UNUSE'),
       (1000000003, now(), now(), 1000000000, 1000000000, 'REGISTER', 'ABCDEFGHIJKLMN4', 'name 4', 'description 4', 'UPGRADE_LICENSE_ATTRIBUTE', 'MAXIMUM_VIEW_PER_MONTH', 10,
        'NONE', 'NONE', 250, TIMESTAMPADD(MINUTE, 10, now()), 'UNUSE'),
       (1000000004, now(), now(), 1000000000, 1000000000, 'APPROVED', 'ABCDEFGHIJKLMN5', 'name 5', 'description 5', 'UPGRADE_LICENSE_ATTRIBUTE', 'YEAR', 1,
        'STANDARD', 'YEAR', 10, TIMESTAMPADD(MINUTE, 10, now()), 'USE');
