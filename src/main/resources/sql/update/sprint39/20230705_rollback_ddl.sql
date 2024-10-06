/*
 23.07.05 DCAR Sprint 39
*/

/* start Mijin-VIRNECT */
-- Jira Issue : DCAR-6337
DROP TABLE IF EXISTS account.service_time_zones;

-- Jira Issue : DCAR-6815
ALTER TABLE account.users DROP COLUMN zone_id;
ALTER TABLE account.users_aud DROP COLUMN zone_id;

-- Jira Issue : DCAR-6816
DROP INDEX idx_locale_code
    ON account.service_time_zones;
