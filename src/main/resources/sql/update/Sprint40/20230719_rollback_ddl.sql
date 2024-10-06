/*
 23.07.19 DCAR Sprint 40
*/

/* start Mijin-VIRNECT */
-- Jira Issue : DCAR-6981
DROP TABLE IF EXISTS account.authority_groups;
DROP TABLE IF EXISTS account.authority_groups_aud;

/* start Seunggeun-VIRNECT */
-- Jira Issue : DCAR-6996
ALTER TABLE account.admin_users
    DROP COLUMN authority_group_id;

ALTER TABLE account.admin_users_aud
    DROP COLUMN authority_group_id