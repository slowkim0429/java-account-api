set FOREIGN_KEY_CHECKS = 0;
truncate table `organization_license_attributes`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `organization_license_attributes`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO organization_license_attributes (id, created_at, updated_at, created_by, updated_by, data_value,
                                                     data_type, license_attribute_id, license_attribute_type,
                                                     organization_license_id, status)
VALUES (1000000000, now(), now(), 1000000000, 1000000000, '3', 'NUMBER', 1000000002,
        'MAXIMUM_GROUP_USER', 1000000000, 'USE'),
       (1000000001, now(), now(), 1000000000, 1000000000, '3', 'NUMBER', 1000000001,
        'MAXIMUM_GROUP', 1000000000, 'USE'),
       (1000000002, now(), now(), 1000000000, 1000000000, 'false', 'BOOL', 1000000007,
        'EXCLUSION_WATERMARK', 1000000000, 'USE'),
       (1000000003, now(), now(), 1000000000, 1000000000, '1000', 'NUMBER', 1000000006,
        'MAXIMUM_VIEW_PER_MONTH', 1000000000, 'USE'),
       (1000000004, now(), now(), 1000000000, 1000000000, '3', 'NUMBER', 1000000005,
        'MAXIMUM_PUBLISHING_PROJECT', 1000000000, 'USE'),
       (1000000005, now(), now(), 1000000000, 1000000000, '10', 'NUMBER', 1000000004,
        'MAXIMUM_PROJECT', 1000000000, 'USE'),
       (1000000006, now(), now(), 1000000000, 1000000000, '500', 'NUMBER', 1000000003,
        'STORAGE_SIZE_PER_MB', 1000000000, 'USE'),
       (1000000007, now(), now(), 1000000000, 1000000000, '1', 'NUMBER', 1000000000,
        'MAXIMUM_WORKSPACE', 1000000000, 'USE');
