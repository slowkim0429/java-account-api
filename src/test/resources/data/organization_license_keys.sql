set
FOREIGN_KEY_CHECKS = 0;
truncate table `organization_license_keys`;
set
FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `organization_license_keys`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO organization_license_keys
(`id`, `organization_id`, `organization_license_id`, `email`, `license_key`, `use_status`, `created_by`, `created_at`,
 `updated_by`, `updated_at`)
VALUES (1000000000, 1000000001, 1000000005, 'user@virnect.com', 'abc.abc.abc', 'USE', 1000000000, now(), 1000000000,
        now()),
       (1000000001, 1000000001, 1000000005, 'user@virnect.com', 'abc.abc.abc.abc', 'UNUSE', 1000000000, now(),
        1000000000, now());
