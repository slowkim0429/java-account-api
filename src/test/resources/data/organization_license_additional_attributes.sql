set FOREIGN_KEY_CHECKS = 0;
truncate table `organization_license_additional_attributes`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `organization_license_additional_attributes`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO organization_license_additional_attributes (id, created_at, updated_at, created_by, updated_by,
                                                        data_value, data_type,
                                                        license_additional_attribute_type, license_attribute_id,
                                                        organization_contract_id, contract_id, status,
                                                        subscription_organization_license_id)
VALUES (1000000000, now(), now(), 1000000000, 1000000000, '1000', 'NUMBER', 'MAXIMUM_VIEW', 1000000036, 1000000000,
        1000000000, 'UNUSE',
        1000000000),
       (1000000001, now(), now(), 1000000000, 1000000000, '1000', 'NUMBER', 'MAXIMUM_VIEW', 1000000036, 1000000001,
        1000000001, 'UNUSE',
        1000000001),
       (1000000002, now(), now(), 1000000000, 1000000000, '1000', 'NUMBER', 'MAXIMUM_VIEW', 1000000036, 1000000002,
        1000000002, 'UNUSE',
        1000000002),
       (1000000003, now(), now(), 1000000000, 1000000000, '1000', 'NUMBER', 'MAXIMUM_VIEW', 1000000036, 1000000003,
        1000000003, 'USE',
        1000000003);

