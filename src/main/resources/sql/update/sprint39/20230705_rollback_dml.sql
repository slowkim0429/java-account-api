/*
 23.07.05 DCAR Sprint 39
*/

/* start Jukyoung-VIRNECT */
-- Jira Issue : DCAR-6744
DELETE
FROM account.license_attributes la
WHERE la.license_attribute_type = 'CUSTOMIZING_SPLASH_SCREEN';

DELETE
FROM account.organization_license_attributes ola
WHERE ola.license_attribute_type = 'CUSTOMIZING_SPLASH_SCREEN';
