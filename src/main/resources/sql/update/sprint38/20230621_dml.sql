/*
 23.06.21 DCAR Sprint 38
*/

/* start Seungwook-VIRNECT */
-- Jira Issue : DCAR-6661
UPDATE account.external_service_mappings
SET is_syncable = true;

UPDATE account.external_service_mappings_aud
SET is_syncable = true;
