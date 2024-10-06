set FOREIGN_KEY_CHECKS = 0;
truncate table `external_service_mappings`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `external_service_mappings`
    ALTER COLUMN `id` RESTART WITH 1000000000;

INSERT INTO external_service_mappings (id, created_at, updated_at, created_by, updated_by, external_domain,
                                               external_mapping_id, external_service, internal_domain,
                                               internal_mapping_id, is_latest_mapping_succeeded, is_syncable)
VALUES (1000000000, '2022-11-22 16:57:38', '2022-11-22 16:57:40', 0, 0, 'CONTACT', 45678, 'HUBSPOT', 'USER', 1000000000,
        true, true),
       (1000000001, '2022-11-22 16:57:38', '2022-11-22 16:57:40', 0, 0, 'COMPANY', 98765, 'HUBSPOT', 'ORGANIZATION',
        1000000000, true, true),
       (1000000002, '2022-11-22 16:57:38', '2022-11-22 16:57:40', 0, 0, 'PRODUCT', 98765, 'HUBSPOT', 'ITEM',
        1000000000, true, true);
