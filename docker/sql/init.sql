## database container init.sql
create database `organization` collate utf8mb4_bin;

ALTER TABLE organization.users auto_increment = 1000000000;
ALTER TABLE organization.user_roles auto_increment = 1000000000;
ALTER TABLE organization.products auto_increment = 1000000000;
ALTER TABLE organization.licenses auto_increment = 1000000000;
ALTER TABLE organization.license_attributes auto_increment = 1000000000;
ALTER TABLE organization.accounts auto_increment = 1000000000;
ALTER TABLE organization.account_licenses auto_increment = 1000000000;
ALTER TABLE organization.invite auto_increment = 1000000000;
ALTER TABLE organization.definition_license_types auto_increment = 1000000000;
ALTER TABLE organization.definition_license_attributes auto_increment = 1000000000;
ALTER TABLE organization.virnect_bank_accounts auto_increment = 1000000000;
ALTER TABLE organization.service_regions auto_increment = 1000000000;
ALTER TABLE organization.service_region_locale_mappings auto_increment = 1000000000;
ALTER TABLE organization.account_license_tokens auto_increment = 1000000000;
ALTER TABLE organization.domains auto_increment = 1000000000;
