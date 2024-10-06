/*
 23.06.21 DCAR Sprint 38
*/

/* start Seungwook-VIRNECT */
-- Jira Issue : DCAR-6661
ALTER TABLE account.external_service_mappings
    DROP is_syncable;

ALTER TABLE account.external_service_mappings_aud
    DROP is_syncable;
