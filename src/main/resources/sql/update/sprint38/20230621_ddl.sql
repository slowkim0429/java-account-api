/*
 23.06.21 DCAR Sprint 38
*/

/* start Seungwook-VIRNECT */
-- Jira Issue : DCAR-6661
ALTER TABLE account.external_service_mappings
    ADD is_syncable bit NOT NULL;

ALTER TABLE account.external_service_mappings_aud
    ADD is_syncable bit NULL;
