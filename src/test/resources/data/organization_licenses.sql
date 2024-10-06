set FOREIGN_KEY_CHECKS = 0;
truncate table `organization_licenses`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `organization_licenses`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO organization_licenses (id, created_at, updated_at, created_by, updated_by, contract_id, expired_at,
                                           item_id, license_description, license_grade_description, license_grade_id,
                                           license_grade_name, license_grade_type, license_id, license_name, license_sales_target,
                                           organization_contract_id, organization_id, product_id, product_name,
                                           product_type, started_at, status)
VALUES (1000000000, now(), now(), 1000000000, 1000000000, 1000000000, '2023-04-01 00:00:00', 1000000000, 'license desc',
        'license grade desc', 1000000002, 'license grade name', 'PROFESSIONAL', 1000000010,
        'license name', 'license sales target', 1000000000, 1000000000, 1000000000, 'product name', 'SQUARS', '2022-02-01 00:00:00', 'CANCELED'),
       (1000000001, now(), now(), 1000000000, 1000000000, 1000000001, '2023-04-01 00:00:00', 1000000000, 'license desc',
        'license grade desc', 1000000002, 'license grade name', 'PROFESSIONAL', 1000000010,
        'license name', 'license sales target', 1000000000, 1000000000, 1000000000, 'product name', 'SQUARS', '2022-02-01 00:00:00', 'CANCELED'),
       (1000000002, now(), now(), 1000000000, 1000000000, 1000000002, '2023-04-01 00:00:00', 1000000008, 'license desc',
        'license grade desc', 1000000002, 'license grade name', 'PROFESSIONAL', 1000000011,
        'license name', 'license sales target', 1000000000, 1000000000, 1000000000, 'product name', 'SQUARS', '2022-02-01 00:00:00', 'CANCELED'),
       (1000000003, now(), now(), 1000000000, 1000000000, 1000000003, '9999-04-01 00:00:00', 1000000008, 'license desc',
        'license grade desc', 1000000002, 'license grade name', 'FREE_PLUS', 1000000004,
        'license name', 'license sales target', 1000000000, 1000000000, 1000000000, 'product name', 'SQUARS', '2022-02-01 00:00:00', 'PROCESSING'),
       (1000000004, now(), now(), 1000000000, 1000000000, 1000000004, '9999-04-01 00:00:00', 1000000008, 'license desc',
        'license grade desc', 1000000002, 'license grade name', 'FREE_PLUS', 1000000004,
        'license name', 'license sales target', 1000000000, 1000000004, 1000000000, 'product name', 'SQUARS', '2022-02-01 00:00:00', 'PROCESSING'),
       (1000000005, now(), now(), 1000000001, 1000000001, null, '9999-04-01 00:00:00', 1000000020, 'license desc',
        'license grade desc', 1000000000, 'license grade name', 'FREE_PLUS', 1000000001,
        'license name', 'license sales target', null, 1000000001, 1000000001, 'product name', 'TRACK', '2022-02-01 00:00:00', 'PROCESSING');
