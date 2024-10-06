/*
 23.05.24 DCAR Sprint 36
*/

/* start Mijin-VIRNECT */
-- Jira Issue : DCAR-6173
DROP TABLE if exists actions;
DROP TABLE if exists actions_aud;

DROP TABLE if exists action_items;
DROP TABLE if exists action_items_aud;

DROP TABLE if exists action_item_access_authorities;
DROP TABLE if exists action_item_access_authorities_aud;

DROP TABLE if exists authorities;
DROP TABLE if exists authorities_aud;

DROP TABLE if exists authority_users;
DROP TABLE if exists authority_users_aud;

-- Jira Issue : DCAR-6290
CREATE TABLE account.organization_license_keys
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime     NOT NULL,
    updated_at              datetime     NOT NULL,
    created_by              bigint       NOT NULL,
    updated_by              bigint       NOT NULL,
    organization_id         bigint       NOT NULL,
    organization_license_id bigint       NOT NULL,
    email                   varchar(200) NOT NULL,
    license_key             varchar(500) NULL
);

CREATE TABLE account.organization_license_keys_aud
(
    id                      bigint       NOT NULL,
    rev                     int          NOT NULL,
    revtype                 tinyint      NULL,
    created_at              datetime     NULL,
    updated_at              datetime     NULL,
    created_by              bigint       NULL,
    updated_by              bigint       NULL,
    organization_id         bigint       NULL,
    organization_license_id bigint       NULL,
    email                   varchar(200) NULL,
    license_key             varchar(500) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKutyo5kq1sdp4e1ppq09k1ll3
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

ALTER TABLE account.organization_license_keys
    ADD use_status varchar(10) NOT NULL;

ALTER TABLE account.organization_license_keys_aud
    ADD use_status varchar(10) NULL;

create index idx_organization_id
    on account.organization_license_keys (organization_id);

/* start Seungwook-VIRNECT */
-- Jira Issue : DCAR-6213
ALTER TABLE account.errors
    ADD origin_type varchar(20) NULL;

CREATE INDEX idx_origin_type
    ON account.errors (origin_type);

ALTER TABLE account.users
    ON last_login_date datetime NULL;

ALTER TABLE account.users_aud
    ON last_login_date datetime NULL;

-- Jira Issue : DCAR-6181
create table account.organization_license_track_sdk_usage_histories
(
    id                          bigint auto_increment
        primary key,
    created_at                  datetime     not null,
    updated_at                  datetime     not null,
    created_by                  bigint       not null,
    updated_by                  bigint       not null,
    content                     varchar(100) not null,
    organization_license_key_id bigint       not null
);

create index idx_content
    on account.organization_license_track_sdk_usage_histories (content);

create index idx_organization_license_key_id
    on account.organization_license_track_sdk_usage_histories (organization_license_key_id);
