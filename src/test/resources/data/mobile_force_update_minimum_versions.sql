set
    FOREIGN_KEY_CHECKS = 0;
truncate table `mobile_force_update_minimum_versions`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `mobile_force_update_minimum_versions`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `mobile_force_update_minimum_versions` (bundle_id, version, force_update_type, created_at, updated_at,
                                                          created_by, updated_by)
VALUES ('com.virnect.Squars-iOS', '1.0.0', 'UNUSED', now(), now(), 0, 0);
