set FOREIGN_KEY_CHECKS = 0;
truncate table `licenses`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `licenses`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `licenses`
(`id`, `product_id`, `name`, `description`, `sales_target`, `license_grade_id`, `status`, `created_by`, `created_at`,
 `updated_by`, `updated_at`, `use_status`)
VALUES (1000000000, 1000000000, 'workspace license1', 'this is default license', 'For starter', 1000000000, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000001, 1000000001, 'remote license1', 'this is remote license', 'For starter', 1000000000, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000002, 1000000000, 'workspace license2', 'this is workspace license', 'For starter', 1000000003,
        'REGISTER', 1000000000, now(), 1000000000, now(), 'DELETE'),
       (1000000003, 1000000001, 'remote license2', 'this is remote license', 'For starter', 1000000002, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'NONE'),
       (1000000004, 1000000000, 'squars license', 'this is squars license', 'For starter', 1000000002, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000005, 1000000000, 'workstation license', 'this is workstation license', 'For starter', 1000000003,
        'APPROVED', 1000000000, now(), 1000000000, now(), 'USE'),
       (1000000006, 1000000001, 'remote license2', 'this is remote license', 'For starter', 1000000002, 'REGISTER',
        1000000000, now(), 1000000000, now(), 'NONE'),
       (1000000007, 1000000000, 'squars license2', 'this is squars license', 'For starter', 1000000001, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000008, 1000000000, 'workspace license3', 'this is workspace license', 'For starter', 1000000003,
        'APPROVED', 1000000000, now(), 1000000000, now(), 'USE'),
       (1000000009, 1000000000, 'squars license3', 'this squars license', 'For starter', 1000000001, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000010, 1000000000, 'squars license4', 'this squars license', 'For starter', 1000000004, 'REGISTER',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000011, 1000000000, 'squars license5', 'this squars license', 'For starter', 1000000001, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE'),
       (1000000012, 1000000001, 'squars license6', 'this squars license', 'For starter', 1000000001, 'APPROVED',
        1000000001, now(), 1000000001, now(), 'USE'),
       (1000000013, 1000000000, 'squars free plus', 'this is free license', 'For starter', 1000000000, 'APPROVED',
        1000000000, now(), 1000000000, now(), 'USE')
;
