/*
 23.07.19 DCAR Sprint 40
*/

/* start Mijin-VIRNECT */
-- Jira Issue : DCAR-6981
CREATE TABLE account.authority_groups
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NOT NULL,
    updated_at  datetime     NOT NULL,
    created_by  bigint       NOT NULL,
    updated_by  bigint       NOT NULL,
    name        varchar(50)  NOT NULL,
    description varchar(250) NULL,
    status      varchar(10)  NOT NULL
);

ALTER TABLE account.authority_groups
    AUTO_INCREMENT = 10000000000;

CREATE TABLE account.authority_groups_aud
(
    id          bigint       NOT NULL,
    rev         int          NOT NULL,
    revtype     tinyint      NULL,
    created_at  datetime     NULL,
    updated_at  datetime     NULL,
    created_by  bigint       NULL,
    updated_by  bigint       NULL,
    name        varchar(50)  NULL,
    description varchar(250) NULL,
    status      varchar(10)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKutyo4ee1sdp4e1mnb01k1ll2
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

-- Jira Issue : DCAR-6982
CREATE INDEX idx_status
    ON account.authority_groups (status);

/* start Seunggeun-VIRNECT */
-- Jira Issue : DCAR-6996
ALTER TABLE account.admin_users
    ADD authority_group_id BIGINT NULL;

ALTER TABLE account.admin_users_aud
    ADD authority_group_id BIGINT NULL;
