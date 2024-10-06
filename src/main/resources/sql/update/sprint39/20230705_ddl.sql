/*
 23.07.05 DCAR Sprint 39
*/

/* start Mijin-VIRNECT */
-- Jira Issue : DCAR-6337
CREATE TABLE account.service_time_zones
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    locale_code     varchar(30)     NULL,
    zone_id         varchar(50)     NOT NULL,
    utc_offset      varchar(10)     NOT NULL,
    name            varchar(60)     NULL
);

ALTER TABLE account.service_time_zones
    AUTO_INCREMENT = 10000000000;

-- Jira Issue : DCAR-6815
ALTER TABLE account.users
    ADD zone_id varchar(50) NOT NULL;

ALTER TABLE account.users_aud
    ADD zone_id varchar(50) NULL;

-- Jira Issue : DCAR-6816
CREATE INDEX idx_locale_code
    ON account.service_time_zones (locale_code);
