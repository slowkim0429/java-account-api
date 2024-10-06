CREATE DATABASE IF NOT EXISTS `account` collate utf8mb4_bin;

USE account;

CREATE TABLE account.admin_user_roles
(
    id            bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NOT NULL,
    created_by    bigint       NOT NULL,
    updated_by    bigint       NOT NULL,
    admin_user_id bigint       NOT NULL,
    role          varchar(100) NOT NULL,
    status        varchar(50)  NOT NULL
);

CREATE INDEX idx_admin_user_id
    ON account.admin_user_roles (admin_user_id);

CREATE INDEX idx_role
    ON account.admin_user_roles (role);

CREATE INDEX idx_status
    ON account.admin_user_roles (status);

CREATE TABLE account.admin_users
(
    id                                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                         datetime     NOT NULL,
    updated_at                         datetime     NOT NULL,
    created_by                         bigint       NOT NULL,
    updated_by                         bigint       NOT NULL,
    approval_status                    varchar(50)  NOT NULL,
    email                              varchar(100) NULL,
    language                           varchar(10)  NOT NULL,
    service_region_locale_mapping_code varchar(255) NOT NULL,
    service_region_locale_mapping_id   bigint       NOT NULL,
    nickname                           varchar(30)  NOT NULL,
    password                           varchar(100) NOT NULL,
    profile_color                      varchar(20)  NOT NULL,
    profile_image                      varchar(255) NULL,
    service_region_aws_code            varchar(255) NOT NULL,
    service_region_code                varchar(255) NOT NULL,
    service_region_id                  bigint       NOT NULL,
    status                             varchar(50)  NOT NULL,
    CONSTRAINT UK_cp8822350s9vtyww7xdbgeuvu
        UNIQUE (email)
);

CREATE INDEX idx_approval_status
    ON account.admin_users (approval_status);

CREATE INDEX idx_nickname
    ON account.admin_users (nickname);

CREATE INDEX idx_service_region_locale_mapping_id
    ON account.admin_users (service_region_locale_mapping_id);

CREATE INDEX idx_status
    ON account.admin_users (status);

CREATE TABLE account.control_mode_access_histories
(
    id                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at         datetime    NOT NULL,
    updated_at         datetime    NOT NULL,
    created_by         bigint      NOT NULL,
    updated_by         bigint      NOT NULL,
    access_result_type varchar(30) NOT NULL
);

CREATE TABLE account.coupon_delivery_history
(
    id                    bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at            datetime     NOT NULL,
    updated_at            datetime     NOT NULL,
    created_by            bigint       NOT NULL,
    updated_by            bigint       NOT NULL,
    coupon_id             bigint       NOT NULL,
    event_popup_id        bigint       NOT NULL,
    receiver_email        varchar(100) NOT NULL,
    receiver_email_domain varchar(255) NOT NULL,
    receiver_user_id      bigint       NULL
);

CREATE INDEX idx_coupon_id
    ON account.coupon_delivery_history (coupon_id);

CREATE INDEX idx_receiver_email
    ON account.coupon_delivery_history (receiver_email);

CREATE INDEX idx_receiver_email_domain
    ON account.coupon_delivery_history (receiver_email_domain);

CREATE INDEX idx_receiver_user_id
    ON account.coupon_delivery_history (receiver_user_id);

CREATE TABLE account.coupons
(
    id                                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                              datetime     NOT NULL,
    updated_at                              datetime     NOT NULL,
    created_by                              bigint       NOT NULL,
    updated_by                              bigint       NOT NULL,
    benefit_option                          varchar(50)  NOT NULL,
    benefit_value                           bigint       NOT NULL,
    code                                    varchar(30)  NOT NULL,
    coupon_license_grade_matching_type      varchar(50)  NOT NULL,
    coupon_recurring_interval_matching_type varchar(50)  NOT NULL,
    coupon_type                             varchar(50)  NOT NULL,
    description                             varchar(255) NOT NULL,
    expired_at                              datetime     NOT NULL,
    max_count                               bigint       NOT NULL,
    name                                    varchar(30)  NOT NULL,
    status                                  varchar(50)  NOT NULL,
    use_status                              varchar(50)  NOT NULL
);

CREATE INDEX idx_code
    ON account.coupons (code);

CREATE INDEX idx_coupon_type
    ON account.coupons (coupon_type);

CREATE INDEX idx_name
    ON account.coupons (name);

CREATE INDEX idx_status
    ON account.coupons (status);

CREATE TABLE account.domains
(
    id                bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at        datetime     NOT NULL,
    updated_at        datetime     NOT NULL,
    created_by        bigint       NOT NULL,
    updated_by        bigint       NOT NULL,
    record_name       varchar(20)  NOT NULL,
    record_value      varchar(100) NOT NULL,
    service_region_id bigint       NULL,
    url               varchar(100) NOT NULL
);

CREATE INDEX idx_created_by
    ON account.domains (created_by);

CREATE INDEX idx_record_name
    ON account.domains (record_name);

CREATE INDEX idx_service_region_id
    ON account.domains (service_region_id);

CREATE INDEX idx_updated_by
    ON account.domains (updated_by);

CREATE TABLE account.email_customizing_managements
(
    id                        bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                datetime     NOT NULL,
    updated_at                datetime     NOT NULL,
    created_by                bigint       NOT NULL,
    updated_by                bigint       NOT NULL,
    contents_inline_image_url varchar(255) NOT NULL,
    description               varchar(255) NULL,
    email_type                varchar(100) NOT NULL,
    use_status                varchar(50)  NOT NULL
);

CREATE INDEX idx_email_type
    ON account.email_customizing_managements (email_type);

CREATE INDEX idx_use_status
    ON account.email_customizing_managements (use_status);

CREATE TABLE account.errors
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime      NOT NULL,
    updated_at              datetime      NOT NULL,
    created_by              bigint        NOT NULL,
    updated_by              bigint        NOT NULL,
    auth_token              varchar(1000) NULL,
    client_service_name     varchar(50)   NULL,
    controller              varchar(100)  NULL,
    device                  varchar(300)  NULL,
    elapsed_time            bigint        NULL,
    header                  text          NULL,
    method                  varchar(100)  NULL,
    method_name             varchar(100)  NULL,
    origin_type             varchar(20)   NULL,
    query_string            varchar(1000) NULL,
    request_body            text          NULL,
    response_body           text          NULL,
    response_status         int           NULL,
    service                 varchar(50)   NOT NULL,
    stack_trace             text          NULL,
    third_party_stack_trace text          NULL,
    url                     varchar(250)  NOT NULL
);

CREATE INDEX idx_created_by
    ON account.errors (created_by);

CREATE INDEX idx_device
    ON account.errors (device);

CREATE INDEX idx_method
    ON account.errors (method);

CREATE INDEX idx_method_name
    ON account.errors (method_name);

CREATE INDEX idx_origin_type
    ON account.errors (origin_type);

CREATE INDEX idx_response_status
    ON account.errors (response_status);

CREATE INDEX idx_service
    ON account.errors (service);

CREATE INDEX idx_url
    ON account.errors (url);

CREATE TABLE account.event_popups
(
    id                             bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                     datetime     NOT NULL,
    updated_at                     datetime     NOT NULL,
    created_by                     bigint       NOT NULL,
    updated_by                     bigint       NOT NULL,
    button_label                   varchar(30)  NULL,
    button_url                     varchar(255) NULL,
    content_description            longtext     NULL,
    coupon_id                      bigint       NULL,
    email_content_inline_image_url varchar(255) NULL,
    email_title                    varchar(50)  NULL,
    event_type                     varchar(50)  NOT NULL,
    exposure_option_data_type      varchar(50)  NULL,
    exposure_option_type           varchar(50)  NOT NULL,
    exposure_option_value          varchar(50)  NULL,
    image_link_url                 varchar(255) NULL,
    image_url                      varchar(255) NOT NULL,
    input_guide                    varchar(255) NULL,
    is_exposed                     bit          NOT NULL,
    name                           varchar(50)  NOT NULL,
    service_type                   varchar(50)  NOT NULL
);

CREATE INDEX idx_coupon_id
    ON account.event_popups (coupon_id);

CREATE INDEX idx_event_type
    ON account.event_popups (event_type);

CREATE INDEX idx_is_exposed
    ON account.event_popups (is_exposed);

CREATE INDEX idx_name
    ON account.event_popups (name);

CREATE INDEX idx_service_type
    ON account.event_popups (service_type);

CREATE TABLE account.external_service_mappings
(
    id                          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                  datetime     NOT NULL,
    updated_at                  datetime     NOT NULL,
    created_by                  bigint       NOT NULL,
    updated_by                  bigint       NOT NULL,
    external_domain             varchar(50)  NOT NULL,
    external_mapping_id         varchar(100) NULL,
    external_service            varchar(50)  NOT NULL,
    internal_domain             varchar(50)  NOT NULL,
    internal_mapping_id         bigint       NOT NULL,
    is_latest_mapping_succeeded bit          NOT NULL
);

CREATE INDEX idx_external_domain
    ON account.external_service_mappings (external_domain);

CREATE INDEX idx_internal_domain
    ON account.external_service_mappings (internal_domain);

CREATE INDEX idx_internal_mapping_id
    ON account.external_service_mappings (internal_mapping_id);

CREATE INDEX idx_is_latest_mapping_succeeded
    ON account.external_service_mappings (is_latest_mapping_succeeded);

CREATE TABLE account.invite
(
    id                  bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at          datetime     NOT NULL,
    updated_at          datetime     NOT NULL,
    created_by          bigint       NOT NULL,
    updated_by          bigint       NOT NULL,
    email               varchar(100) NOT NULL,
    expired_at          datetime     NOT NULL,
    group_id            bigint       NULL,
    invite_role         varchar(20)  NOT NULL,
    invite_status       varchar(10)  NOT NULL,
    invite_type         varchar(20)  NOT NULL,
    organization_id     bigint       NOT NULL,
    workspace_id        bigint       NOT NULL,
    workspace_invite_id bigint       NULL
);

CREATE INDEX idx_email
    ON account.invite (email);

CREATE INDEX idx_group_id
    ON account.invite (group_id);

CREATE INDEX idx_invite_status
    ON account.invite (invite_status);

CREATE INDEX idx_invite_type
    ON account.invite (invite_status);

CREATE INDEX idx_workspaceId
    ON account.invite (workspace_id);

CREATE TABLE account.item_payment_links
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime     NOT NULL,
    updated_at   datetime     NOT NULL,
    created_by   bigint       NOT NULL,
    updated_by   bigint       NOT NULL,
    email        varchar(255) NOT NULL,
    email_domain varchar(255) NOT NULL,
    expired_at   datetime     NOT NULL,
    item_id      bigint       NOT NULL,
    user_id      bigint       NOT NULL
);

CREATE INDEX idx_email
    ON account.item_payment_links (email);

CREATE INDEX idx_email_domain
    ON account.item_payment_links (email_domain);

CREATE INDEX idx_item_id
    ON account.item_payment_links (item_id);

CREATE INDEX idx_user_id
    ON account.item_payment_links (user_id);

CREATE TABLE account.items
(
    id                   bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at           datetime       NOT NULL,
    updated_at           datetime       NOT NULL,
    created_by           bigint         NOT NULL,
    updated_by           bigint         NOT NULL,
    amount               decimal(19, 2) NOT NULL,
    currency_type        varchar(20)    NOT NULL,
    hubspot_product_id   bigint         NULL,
    is_exposed           bit            NOT NULL,
    item_type            varchar(20)    NOT NULL,
    license_attribute_id bigint         NULL,
    license_id           bigint         NOT NULL,
    monthly_used_amount  decimal(19, 2) NOT NULL,
    name                 varchar(50)    NOT NULL,
    payment_type         varchar(20)    NOT NULL,
    recurring_interval   varchar(20)    NOT NULL,
    status               varchar(20)    NOT NULL,
    use_status           varchar(20)    NOT NULL
);

CREATE INDEX idx_hubspot_product_id
    ON account.items (hubspot_product_id);

CREATE INDEX idx_item_type
    ON account.items (item_type);

CREATE INDEX idx_license_id
    ON account.items (license_id);

CREATE INDEX idx_name
    ON account.items (name);

CREATE INDEX idx_payment_type
    ON account.items (payment_type);

CREATE INDEX idx_recurring_interval
    ON account.items (recurring_interval);

CREATE INDEX idx_status
    ON account.items (status);

CREATE INDEX idx_use_status
    ON account.items (use_status);

CREATE TABLE account.license_attributes
(
    id                                bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                        datetime    NOT NULL,
    updated_at                        datetime    NOT NULL,
    created_by                        bigint      NOT NULL,
    updated_by                        bigint      NOT NULL,
    license_additional_attribute_type varchar(30) NULL,
    license_attribute_dependency_type varchar(30) NOT NULL,
    license_attribute_type            varchar(30) NULL,
    data_type                         varchar(10) NOT NULL,
    data_value                        varchar(10) NOT NULL,
    license_id                        bigint      NOT NULL,
    status                            varchar(50) NOT NULL
);

CREATE INDEX idx_license_additional_attribute_type
    ON account.license_attributes (license_additional_attribute_type);

CREATE INDEX idx_license_attribute_dependency_type
    ON account.license_attributes (license_attribute_dependency_type);

CREATE INDEX idx_license_attribute_type
    ON account.license_attributes (license_attribute_type);

CREATE INDEX idx_license_id
    ON account.license_attributes (license_id);

CREATE INDEX idx_status
    ON account.license_attributes (status);

CREATE TABLE account.license_grades
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NOT NULL,
    updated_at  datetime     NOT NULL,
    created_by  bigint       NOT NULL,
    updated_by  bigint       NOT NULL,
    description varchar(255) NOT NULL,
    grade_type  varchar(50)  NOT NULL,
    name        varchar(100) NOT NULL,
    status      varchar(10)  NOT NULL
);

CREATE INDEX idx_name
    ON account.license_grades (name);

CREATE INDEX idx_status
    ON account.license_grades (status);

CREATE TABLE account.licenses
(
    id               bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at       datetime     NOT NULL,
    updated_at       datetime     NOT NULL,
    created_by       bigint       NOT NULL,
    updated_by       bigint       NOT NULL,
    description      varchar(255) NOT NULL,
    license_grade_id bigint       NOT NULL,
    name             varchar(50)  NOT NULL,
    product_id       bigint       NOT NULL,
    sales_target     varchar(50)  NOT NULL,
    status           varchar(50)  NOT NULL,
    use_status       varchar(50)  NOT NULL
);

CREATE INDEX idx_created_by
    ON account.licenses (created_by);

CREATE INDEX idx_license_grade_id
    ON account.licenses (license_grade_id);

CREATE INDEX idx_name
    ON account.licenses (name);

CREATE INDEX idx_product_id
    ON account.licenses (product_id);

CREATE INDEX idx_status
    ON account.licenses (status);

CREATE INDEX idx_updated_by
    ON account.licenses (updated_by);

CREATE INDEX idx_use_status
    ON account.licenses (use_status);

CREATE TABLE account.mobile_force_update_minimum_versions
(
    id                bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at        datetime     NOT NULL,
    updated_at        datetime     NOT NULL,
    created_by        bigint       NOT NULL,
    updated_by        bigint       NOT NULL,
    bundle_id         varchar(255) NOT NULL,
    force_update_type varchar(50)  NOT NULL,
    version           varchar(50)  NOT NULL
);

CREATE TABLE account.mobile_managements
(
    id                    bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at            datetime     NOT NULL,
    updated_at            datetime     NOT NULL,
    created_by            bigint       NOT NULL,
    updated_by            bigint       NOT NULL,
    notice_is_exposed     bit          NOT NULL,
    notice_message        varchar(200) NULL,
    control_mode_password varchar(100) NOT NULL
);

CREATE TABLE account.organization_contracts
(
    id                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at         datetime     NOT NULL,
    updated_at         datetime     NOT NULL,
    created_by         bigint       NOT NULL,
    updated_by         bigint       NOT NULL,
    contract_id        bigint       NOT NULL,
    coupon_id          bigint       NULL,
    end_date           datetime     NOT NULL,
    item_id            bigint       NOT NULL,
    item_name          varchar(100) NOT NULL,
    item_type          varchar(20)  NOT NULL,
    organization_id    bigint       NOT NULL,
    payment_type       varchar(50)  NOT NULL,
    recurring_interval varchar(50)  NOT NULL,
    start_date         datetime     NOT NULL,
    status             varchar(50)  NOT NULL
);

CREATE INDEX idx_contract_id
    ON account.organization_contracts (contract_id);

CREATE INDEX idx_item_id
    ON account.organization_contracts (item_id);

CREATE INDEX idx_organization_id
    ON account.organization_contracts (organization_id);

CREATE INDEX idx_status
    ON account.organization_contracts (status);

CREATE TABLE account.organization_license_additional_attributes
(
    id                                   bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                           datetime    NOT NULL,
    updated_at                           datetime    NOT NULL,
    created_by                           bigint      NOT NULL,
    updated_by                           bigint      NOT NULL,
    contract_id                          bigint      NOT NULL,
    data_type                            varchar(10) NOT NULL,
    data_value                           varchar(10) NOT NULL,
    license_additional_attribute_type    varchar(30) NOT NULL,
    license_attribute_id                 bigint      NOT NULL,
    organization_contract_id             bigint      NOT NULL,
    status                               varchar(50) NOT NULL,
    subscription_organization_license_id bigint      NOT NULL
);

CREATE INDEX idx_contract_id
    ON account.organization_license_additional_attributes (contract_id);

CREATE INDEX idx_organization_contract_id
    ON account.organization_license_additional_attributes (organization_contract_id);

CREATE INDEX idx_status
    ON account.organization_license_additional_attributes (status);

CREATE INDEX idx_subscription_organization_license_id
    ON account.organization_license_additional_attributes (subscription_organization_license_id);

CREATE TABLE account.organization_license_attributes
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime    NOT NULL,
    updated_at              datetime    NOT NULL,
    created_by              bigint      NOT NULL,
    updated_by              bigint      NOT NULL,
    data_type               varchar(10) NOT NULL,
    data_value              varchar(10) NOT NULL,
    license_attribute_id    bigint      NOT NULL,
    license_attribute_type  varchar(30) NOT NULL,
    organization_license_id bigint      NOT NULL,
    status                  varchar(50) NOT NULL
);

CREATE INDEX idx_organization_license_id
    ON account.organization_license_attributes (organization_license_id);

CREATE INDEX idx_status
    ON account.organization_license_attributes (status);

CREATE TABLE account.organization_license_keys
(
    id                      bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at              datetime     NOT NULL,
    updated_at              datetime     NOT NULL,
    created_by              bigint       NOT NULL,
    updated_by              bigint       NOT NULL,
    email                   varchar(200) NOT NULL,
    license_key             varchar(500) NULL,
    organization_id         bigint       NOT NULL,
    organization_license_id bigint       NOT NULL,
    use_status              varchar(10)  NOT NULL
);

CREATE INDEX idx_organization_id
    ON account.organization_license_keys (organization_id);

CREATE TABLE account.organization_license_track_sdk_usage_histories
(
    id                          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                  datetime     NOT NULL,
    updated_at                  datetime     NOT NULL,
    created_by                  bigint       NOT NULL,
    updated_by                  bigint       NOT NULL,
    content                     varchar(100) NOT NULL,
    organization_license_key_id bigint       NOT NULL
);

CREATE INDEX idx_content
    ON account.organization_license_track_sdk_usage_histories (content);

CREATE INDEX idx_organization_license_key_id
    ON account.organization_license_track_sdk_usage_histories (organization_license_key_id);

CREATE TABLE account.organization_licenses
(
    id                        bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                datetime     NOT NULL,
    updated_at                datetime     NOT NULL,
    created_by                bigint       NOT NULL,
    updated_by                bigint       NOT NULL,
    contract_id               bigint       NULL,
    expired_at                datetime     NOT NULL,
    item_id                   bigint       NOT NULL,
    license_description       varchar(255) NOT NULL,
    license_grade_description varchar(255) NOT NULL,
    license_grade_id          bigint       NOT NULL,
    license_grade_name        varchar(50)  NOT NULL,
    license_grade_type        varchar(50)  NOT NULL,
    license_id                bigint       NOT NULL,
    license_name              varchar(50)  NOT NULL,
    license_sales_target      varchar(50)  NOT NULL,
    organization_contract_id  bigint       NULL,
    organization_id           bigint       NOT NULL,
    product_id                bigint       NOT NULL,
    product_name              varchar(100) NOT NULL,
    product_type              varchar(100) NOT NULL,
    started_at                datetime     NOT NULL,
    status                    varchar(20)  NOT NULL
);

CREATE INDEX idx_item_id
    ON account.organization_licenses (item_id);

CREATE INDEX idx_license_grade_type
    ON account.organization_licenses (license_grade_type);

CREATE INDEX idx_license_id
    ON account.organization_licenses (license_id);

CREATE INDEX idx_organization_contract_id
    ON account.organization_licenses (organization_contract_id);

CREATE INDEX idx_organization_id
    ON account.organization_licenses (organization_id);

CREATE INDEX idx_status
    ON account.organization_licenses (status);

CREATE TABLE account.organizations
(
    id                                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                         datetime     NOT NULL,
    updated_at                         datetime     NOT NULL,
    created_by                         bigint       NOT NULL,
    updated_by                         bigint       NOT NULL,
    address                            varchar(200) NULL,
    city                               varchar(50)  NULL,
    corporate_number                   varchar(30)  NULL,
    country_calling_code               int          NULL,
    customer_type                      varchar(30)  NULL,
    email                              varchar(200) NOT NULL,
    email_domain                       varchar(50)  NULL,
    first_name                         varchar(100) NULL,
    hubspot_company_id                 bigint       NULL,
    is_encrypted                       bit          NOT NULL,
    last_name                          varchar(100) NULL,
    service_region_locale_mapping_code varchar(30)  NOT NULL,
    service_region_locale_mapping_id   bigint       NOT NULL,
    service_region_locale_mapping_name varchar(100) NOT NULL,
    name                               varchar(50)  NOT NULL,
    phone_number                       varchar(100) NULL,
    postal_code                        varchar(20)  NULL,
    province                           varchar(50)  NULL,
    state_code                         varchar(30)  NULL,
    state_name                         varchar(100) NULL,
    status                             varchar(10)  NOT NULL,
    vat_identification_number          varchar(200) NULL
);

CREATE INDEX hubspot_company_id
    ON account.organizations (hubspot_company_id);

CREATE INDEX idx_created_by
    ON account.organizations (created_by);

CREATE INDEX idx_email
    ON account.organizations (email);

CREATE INDEX idx_email_domain
    ON account.organizations (email_domain);

CREATE INDEX idx_name
    ON account.organizations (name);

CREATE INDEX idx_province
    ON account.organizations (province);

CREATE INDEX idx_service_region_locale_mapping_name
    ON account.organizations (service_region_locale_mapping_name);

CREATE INDEX idx_state_name
    ON account.organizations (state_name);

CREATE INDEX idx_status
    ON account.organizations (status);

CREATE INDEX idx_updated_by
    ON account.organizations (updated_by);

CREATE INDEX idx_vat_identification_number
    ON account.organizations (vat_identification_number);

CREATE TABLE account.products
(
    id           bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at   datetime     NOT NULL,
    updated_at   datetime     NOT NULL,
    created_by   bigint       NOT NULL,
    updated_by   bigint       NOT NULL,
    name         varchar(100) NOT NULL,
    product_type varchar(100) NOT NULL,
    status       varchar(50)  NOT NULL
);

CREATE INDEX idx_created_by
    ON account.products (created_by);

CREATE INDEX idx_product_type
    ON account.products (product_type);

CREATE INDEX idx_status
    ON account.products (status);

CREATE INDEX idx_updated_by
    ON account.products (updated_by);

CREATE TABLE account.revinfo
(
    rev      int AUTO_INCREMENT
        PRIMARY KEY,
    revtstmp bigint NULL
);

CREATE TABLE account.admin_user_roles_aud
(
    id            bigint       NOT NULL,
    rev           int          NOT NULL,
    revtype       tinyint      NULL,
    created_at    datetime     NULL,
    updated_at    datetime     NULL,
    created_by    bigint       NULL,
    updated_by    bigint       NULL,
    admin_user_id bigint       NULL,
    role          varchar(100) NULL,
    status        varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKbo3j1ystqg8pm4jodgtfnlyyh
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.admin_users_aud
(
    id                                 bigint       NOT NULL,
    rev                                int          NOT NULL,
    revtype                            tinyint      NULL,
    created_at                         datetime     NULL,
    updated_at                         datetime     NULL,
    created_by                         bigint       NULL,
    updated_by                         bigint       NULL,
    approval_status                    varchar(50)  NULL,
    email                              varchar(100) NULL,
    language                           varchar(10)  NULL,
    service_region_locale_mapping_code varchar(255) NULL,
    service_region_locale_mapping_id   bigint       NULL,
    nickname                           varchar(30)  NULL,
    password                           varchar(100) NULL,
    profile_color                      varchar(20)  NULL,
    profile_image                      varchar(255) NULL,
    service_region_aws_code            varchar(255) NULL,
    service_region_code                varchar(255) NULL,
    service_region_id                  bigint       NULL,
    status                             varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKqirn5t72380bio3of7efx6k81
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.coupon_delivery_history_aud
(
    id                    bigint       NOT NULL,
    rev                   int          NOT NULL,
    revtype               tinyint      NULL,
    created_at            datetime     NULL,
    updated_at            datetime     NULL,
    created_by            bigint       NULL,
    updated_by            bigint       NULL,
    coupon_id             bigint       NULL,
    event_popup_id        bigint       NULL,
    receiver_email        varchar(100) NULL,
    receiver_email_domain varchar(255) NULL,
    receiver_user_id      bigint       NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKqfqis3mnsxy6rd1hu09n4feau
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.coupons_aud
(
    id                                      bigint       NOT NULL,
    rev                                     int          NOT NULL,
    revtype                                 tinyint      NULL,
    created_at                              datetime     NULL,
    updated_at                              datetime     NULL,
    created_by                              bigint       NULL,
    updated_by                              bigint       NULL,
    benefit_option                          varchar(50)  NULL,
    benefit_value                           bigint       NULL,
    code                                    varchar(30)  NULL,
    coupon_license_grade_matching_type      varchar(50)  NULL,
    coupon_recurring_interval_matching_type varchar(50)  NULL,
    coupon_type                             varchar(50)  NULL,
    description                             varchar(255) NULL,
    expired_at                              datetime     NULL,
    max_count                               bigint       NULL,
    name                                    varchar(30)  NULL,
    status                                  varchar(50)  NULL,
    use_status                              varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FK9gj9yggifbeqtqdxium4jntve
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.domains_aud
(
    id                bigint       NOT NULL,
    rev               int          NOT NULL,
    revtype           tinyint      NULL,
    created_at        datetime     NULL,
    updated_at        datetime     NULL,
    created_by        bigint       NULL,
    updated_by        bigint       NULL,
    record_name       varchar(20)  NULL,
    record_value      varchar(100) NULL,
    service_region_id bigint       NULL,
    url               varchar(100) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKof509c8utcebt7k3gog3xoikj
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.email_customizing_managements_aud
(
    id                        bigint       NOT NULL,
    rev                       int          NOT NULL,
    revtype                   tinyint      NULL,
    created_at                datetime     NULL,
    updated_at                datetime     NULL,
    created_by                bigint       NULL,
    updated_by                bigint       NULL,
    contents_inline_image_url varchar(255) NULL,
    description               varchar(255) NULL,
    email_type                varchar(100) NULL,
    use_status                varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKutyo5kq0dwp5w3hoa62j4ke3
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.event_popups_aud
(
    id                             bigint       NOT NULL,
    rev                            int          NOT NULL,
    revtype                        tinyint      NULL,
    created_at                     datetime     NULL,
    updated_at                     datetime     NULL,
    created_by                     bigint       NULL,
    updated_by                     bigint       NULL,
    button_label                   varchar(30)  NULL,
    button_url                     varchar(255) NULL,
    content_description            longtext     NULL,
    coupon_id                      bigint       NULL,
    email_content_inline_image_url varchar(255) NULL,
    email_title                    varchar(50)  NULL,
    event_type                     varchar(50)  NULL,
    exposure_option_data_type      varchar(50)  NULL,
    exposure_option_type           varchar(50)  NULL,
    exposure_option_value          varchar(50)  NULL,
    image_link_url                 varchar(255) NULL,
    image_url                      varchar(255) NULL,
    input_guide                    varchar(255) NULL,
    is_exposed                     bit          NULL,
    name                           varchar(50)  NULL,
    service_type                   varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKtjl9oda7eo1r8va2b5dq4c8rd
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.external_service_mappings_aud
(
    id                          bigint       NOT NULL,
    rev                         int          NOT NULL,
    revtype                     tinyint      NULL,
    created_at                  datetime     NULL,
    updated_at                  datetime     NULL,
    created_by                  bigint       NULL,
    updated_by                  bigint       NULL,
    external_domain             varchar(50)  NULL,
    external_mapping_id         varchar(100) NULL,
    external_service            varchar(50)  NULL,
    internal_domain             varchar(50)  NULL,
    internal_mapping_id         bigint       NULL,
    is_latest_mapping_succeeded bit          NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKpjkxmb3fh47nndi8m6so1g63a
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.invite_aud
(
    id                  bigint       NOT NULL,
    rev                 int          NOT NULL,
    revtype             tinyint      NULL,
    created_at          datetime     NULL,
    updated_at          datetime     NULL,
    created_by          bigint       NULL,
    updated_by          bigint       NULL,
    email               varchar(255) NULL,
    expired_at          datetime     NULL,
    group_id            bigint       NULL,
    invite_role         varchar(20)  NULL,
    invite_status       varchar(10)  NULL,
    invite_type         varchar(20)  NULL,
    organization_id     bigint       NULL,
    workspace_id        bigint       NULL,
    workspace_invite_id bigint       NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKb33mpi5tol0qylu6g8j4rd9fh
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.item_payment_links_aud
(
    id           bigint       NOT NULL,
    rev          int          NOT NULL,
    revtype      tinyint      NULL,
    created_at   datetime     NULL,
    updated_at   datetime     NULL,
    created_by   bigint       NULL,
    updated_by   bigint       NULL,
    email        varchar(255) NULL,
    email_domain varchar(255) NULL,
    expired_at   datetime     NULL,
    item_id      bigint       NULL,
    user_id      bigint       NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKr51512jodnqmm8l78pw8b7vgf
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.items_aud
(
    id                   bigint         NOT NULL,
    rev                  int            NOT NULL,
    revtype              tinyint        NULL,
    created_at           datetime       NULL,
    updated_at           datetime       NULL,
    created_by           bigint         NULL,
    updated_by           bigint         NULL,
    amount               decimal(19, 2) NULL,
    currency_type        varchar(20)    NULL,
    hubspot_product_id   bigint         NULL,
    is_exposed           bit            NULL,
    item_type            varchar(20)    NULL,
    license_attribute_id bigint         NULL,
    license_id           bigint         NULL,
    monthly_used_amount  decimal(19, 2) NULL,
    name                 varchar(50)    NULL,
    payment_type         varchar(20)    NULL,
    recurring_interval   varchar(20)    NULL,
    status               varchar(20)    NULL,
    use_status           varchar(20)    NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FK9btc5ke94st1j6hgxpjj9ldwp
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.license_attributes_aud
(
    id                                bigint      NOT NULL,
    rev                               int         NOT NULL,
    revtype                           tinyint     NULL,
    created_at                        datetime    NULL,
    updated_at                        datetime    NULL,
    created_by                        bigint      NULL,
    updated_by                        bigint      NULL,
    license_additional_attribute_type varchar(30) NULL,
    license_attribute_dependency_type varchar(30) NULL,
    license_attribute_type            varchar(30) NULL,
    data_type                         varchar(10) NULL,
    data_value                        varchar(10) NULL,
    license_id                        bigint      NULL,
    status                            varchar(50) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FK2qxkux2yftdjh2rbrba1688t6
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.license_grades_aud
(
    id          bigint       NOT NULL,
    rev         int          NOT NULL,
    revtype     tinyint      NULL,
    created_at  datetime     NULL,
    updated_at  datetime     NULL,
    created_by  bigint       NULL,
    updated_by  bigint       NULL,
    description varchar(255) NULL,
    grade_type  varchar(50)  NULL,
    name        varchar(100) NULL,
    status      varchar(10)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKoxjoypohd4h821qkeckns5wvy
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.licenses_aud
(
    id               bigint       NOT NULL,
    rev              int          NOT NULL,
    revtype          tinyint      NULL,
    created_at       datetime     NULL,
    updated_at       datetime     NULL,
    created_by       bigint       NULL,
    updated_by       bigint       NULL,
    description      varchar(255) NULL,
    license_grade_id bigint       NULL,
    name             varchar(50)  NULL,
    product_id       bigint       NULL,
    sales_target     varchar(50)  NULL,
    status           varchar(50)  NULL,
    use_status       varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKarab6be9qpy6q3hhc6dexd9d9
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.mobile_force_update_minimum_versions_aud
(
    id                bigint       NOT NULL,
    rev               int          NOT NULL,
    revtype           tinyint      NULL,
    created_at        datetime     NULL,
    updated_at        datetime     NULL,
    created_by        bigint       NULL,
    updated_by        bigint       NULL,
    bundle_id         varchar(255) NULL,
    force_update_type varchar(50)  NULL,
    version           varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKgabfe4l0gdfr8lt7x4r6hqkfp
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.mobile_managements_aud
(
    id                    bigint       NOT NULL,
    rev                   int          NOT NULL,
    revtype               tinyint      NULL,
    created_at            datetime     NULL,
    updated_at            datetime     NULL,
    created_by            bigint       NULL,
    updated_by            bigint       NULL,
    notice_is_exposed     bit          NULL,
    notice_message        varchar(200) NULL,
    control_mode_password varchar(100) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKnxgkqei3ajtvubm9oqud8qcc4
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.organization_contracts_aud
(
    id                 bigint       NOT NULL,
    rev                int          NOT NULL,
    revtype            tinyint      NULL,
    created_at         datetime     NULL,
    updated_at         datetime     NULL,
    created_by         bigint       NULL,
    updated_by         bigint       NULL,
    contract_id        bigint       NULL,
    coupon_id          bigint       NULL,
    end_date           datetime     NULL,
    item_id            bigint       NULL,
    item_name          varchar(100) NULL,
    item_type          varchar(20)  NULL,
    organization_id    bigint       NULL,
    payment_type       varchar(50)  NULL,
    recurring_interval varchar(50)  NULL,
    start_date         datetime     NULL,
    status             varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FK52jctl5und91a2ayluiknvl5o
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.organization_license_additional_attributes_aud
(
    id                                   bigint      NOT NULL,
    rev                                  int         NOT NULL,
    revtype                              tinyint     NULL,
    created_at                           datetime    NULL,
    updated_at                           datetime    NULL,
    created_by                           bigint      NULL,
    updated_by                           bigint      NULL,
    contract_id                          bigint      NULL,
    data_type                            varchar(10) NULL,
    data_value                           varchar(10) NULL,
    license_additional_attribute_type    varchar(30) NULL,
    license_attribute_id                 bigint      NULL,
    organization_contract_id             bigint      NULL,
    status                               varchar(50) NULL,
    subscription_organization_license_id bigint      NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKp296t43evavmpub56sq8fq7bf
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.organization_license_attributes_aud
(
    id                      bigint      NOT NULL,
    rev                     int         NOT NULL,
    revtype                 tinyint     NULL,
    created_at              datetime    NULL,
    updated_at              datetime    NULL,
    created_by              bigint      NULL,
    updated_by              bigint      NULL,
    data_type               varchar(10) NULL,
    data_value              varchar(10) NULL,
    license_attribute_id    bigint      NULL,
    license_attribute_type  varchar(30) NULL,
    organization_license_id bigint      NULL,
    status                  varchar(50) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FK7mqtn4fr931l6jlox3yb6gmku
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
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
    email                   varchar(200) NULL,
    license_key             varchar(500) NULL,
    organization_id         bigint       NULL,
    organization_license_id bigint       NULL,
    use_status              varchar(10)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKkqv8p2v5lb7yyg4s34n7b7vv2
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.organization_licenses_aud
(
    id                        bigint       NOT NULL,
    rev                       int          NOT NULL,
    revtype                   tinyint      NULL,
    created_at                datetime     NULL,
    updated_at                datetime     NULL,
    created_by                bigint       NULL,
    updated_by                bigint       NULL,
    contract_id               bigint       NULL,
    expired_at                datetime     NULL,
    item_id                   bigint       NULL,
    license_description       varchar(255) NULL,
    license_grade_description varchar(255) NULL,
    license_grade_id          bigint       NULL,
    license_grade_name        varchar(50)  NULL,
    license_grade_type        varchar(50)  NULL,
    license_id                bigint       NULL,
    license_name              varchar(50)  NULL,
    license_sales_target      varchar(50)  NULL,
    organization_contract_id  bigint       NULL,
    organization_id           bigint       NULL,
    product_id                bigint       NULL,
    product_name              varchar(100) NULL,
    product_type              varchar(100) NULL,
    started_at                datetime     NULL,
    status                    varchar(20)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKqixexiwrctbqo7qnbm2bphdoy
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.organizations_aud
(
    id                                 bigint       NOT NULL,
    rev                                int          NOT NULL,
    revtype                            tinyint      NULL,
    created_at                         datetime     NULL,
    updated_at                         datetime     NULL,
    created_by                         bigint       NULL,
    updated_by                         bigint       NULL,
    address                            varchar(200) NULL,
    city                               varchar(50)  NULL,
    corporate_number                   varchar(30)  NULL,
    country_calling_code               int          NULL,
    customer_type                      varchar(30)  NULL,
    email                              varchar(200) NULL,
    email_domain                       varchar(50)  NULL,
    first_name                         varchar(100) NULL,
    hubspot_company_id                 bigint       NULL,
    is_encrypted                       bit          NULL,
    last_name                          varchar(100) NULL,
    service_region_locale_mapping_code varchar(30)  NULL,
    service_region_locale_mapping_id   bigint       NULL,
    service_region_locale_mapping_name varchar(100) NULL,
    name                               varchar(50)  NULL,
    phone_number                       varchar(100) NULL,
    postal_code                        varchar(20)  NULL,
    province                           varchar(50)  NULL,
    state_code                         varchar(30)  NULL,
    state_name                         varchar(100) NULL,
    status                             varchar(10)  NULL,
    vat_identification_number          varchar(200) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKfxeq0lp5j0m5ftym4oq5xo2yq
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.products_aud
(
    id           bigint       NOT NULL,
    rev          int          NOT NULL,
    revtype      tinyint      NULL,
    created_at   datetime     NULL,
    updated_at   datetime     NULL,
    created_by   bigint       NULL,
    updated_by   bigint       NULL,
    name         varchar(255) NULL,
    product_type varchar(100) NULL,
    status       varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKis5p0x6t8gvib9m5ra1fiybi3
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.service_locale_states
(
    id          bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at  datetime     NOT NULL,
    updated_at  datetime     NOT NULL,
    created_by  bigint       NOT NULL,
    updated_by  bigint       NOT NULL,
    code        varchar(30)  NOT NULL,
    locale_code varchar(30)  NOT NULL,
    name        varchar(100) NOT NULL
);

CREATE TABLE account.service_locale_states_aud
(
    id          bigint       NOT NULL,
    rev         int          NOT NULL,
    revtype     tinyint      NULL,
    created_at  datetime     NULL,
    updated_at  datetime     NULL,
    created_by  bigint       NULL,
    updated_by  bigint       NULL,
    code        varchar(30)  NULL,
    locale_code varchar(30)  NULL,
    name        varchar(100) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKis27onppb1eoudbcjdwjbihsw
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.service_region_locale_mappings
(
    id                     bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at             datetime     NOT NULL,
    updated_at             datetime     NOT NULL,
    created_by             bigint       NOT NULL,
    updated_by             bigint       NOT NULL,
    code                   varchar(30)  NOT NULL,
    continent              varchar(100) NOT NULL,
    country_calling_code   int          NOT NULL,
    intermediate_continent varchar(100) NULL,
    name                   varchar(100) NOT NULL,
    service_region_id      bigint       NOT NULL,
    sub_continent          varchar(100) NOT NULL,
    CONSTRAINT UK_apl0pa4koiq7l18764awjd3yt
        UNIQUE (code)
);

CREATE INDEX idx_code
    ON account.service_region_locale_mappings (code);

CREATE INDEX idx_created_by
    ON account.service_region_locale_mappings (created_by);

CREATE INDEX idx_service_region_id
    ON account.service_region_locale_mappings (service_region_id);

CREATE INDEX idx_updated_by
    ON account.service_region_locale_mappings (updated_by);

CREATE TABLE account.service_region_locale_mappings_aud
(
    id                     bigint       NOT NULL,
    rev                    int          NOT NULL,
    revtype                tinyint      NULL,
    created_at             datetime     NULL,
    updated_at             datetime     NULL,
    created_by             bigint       NULL,
    updated_by             bigint       NULL,
    code                   varchar(30)  NULL,
    continent              varchar(100) NULL,
    country_calling_code   int          NULL,
    intermediate_continent varchar(100) NULL,
    name                   varchar(100) NULL,
    service_region_id      bigint       NULL,
    sub_continent          varchar(100) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKfswkeu7vs6r1n10nxx7uieu2s
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.service_regions
(
    id         bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    created_by bigint       NOT NULL,
    updated_by bigint       NOT NULL,
    aws_code   varchar(50)  NOT NULL,
    code       varchar(30)  NOT NULL,
    name       varchar(100) NOT NULL,
    CONSTRAINT UK_agmoditg6iqjfk9f09ffev181
        UNIQUE (code)
);

CREATE INDEX idx_aws_code
    ON account.service_regions (aws_code);

CREATE INDEX idx_code
    ON account.service_regions (code);

CREATE INDEX idx_created_by
    ON account.service_regions (created_by);

CREATE INDEX idx_updated_by
    ON account.service_regions (updated_by);

CREATE TABLE account.service_regions_aud
(
    id         bigint       NOT NULL,
    rev        int          NOT NULL,
    revtype    tinyint      NULL,
    created_at datetime     NULL,
    updated_at datetime     NULL,
    created_by bigint       NULL,
    updated_by bigint       NULL,
    aws_code   varchar(50)  NULL,
    code       varchar(30)  NULL,
    name       varchar(100) NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKp3a64766tvcyg45jmjfhykq1t
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.update_guides
(
    id              bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at      datetime     NOT NULL,
    updated_at      datetime     NOT NULL,
    created_by      bigint       NOT NULL,
    updated_by      bigint       NOT NULL,
    date_by_update  varchar(255) NOT NULL,
    description     longtext     NOT NULL,
    file_type       varchar(50)  NOT NULL,
    file_url        varchar(250) NOT NULL,
    is_exposed      bit          NOT NULL,
    name            varchar(50)  NOT NULL,
    service_type    varchar(50)  NOT NULL,
    sub_description longtext     NULL,
    sub_title       varchar(50)  NULL,
    title           varchar(50)  NOT NULL
);

CREATE INDEX idx_is_exposed
    ON account.update_guides (is_exposed);

CREATE INDEX idx_name
    ON account.update_guides (name);

CREATE TABLE account.update_guides_aud
(
    id              bigint       NOT NULL,
    rev             int          NOT NULL,
    revtype         tinyint      NULL,
    created_at      datetime     NULL,
    updated_at      datetime     NULL,
    created_by      bigint       NULL,
    updated_by      bigint       NULL,
    date_by_update  varchar(255) NULL,
    description     longtext     NULL,
    file_type       varchar(50)  NULL,
    file_url        varchar(250) NULL,
    is_exposed      bit          NULL,
    name            varchar(50)  NULL,
    service_type    varchar(50)  NULL,
    sub_description longtext     NULL,
    sub_title       varchar(50)  NULL,
    title           varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKmry9v10skpc4f983qijb43ovn
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.user_roles
(
    id         bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at datetime     NOT NULL,
    updated_at datetime     NOT NULL,
    created_by bigint       NOT NULL,
    updated_by bigint       NOT NULL,
    role       varchar(100) NOT NULL,
    status     varchar(50)  NOT NULL,
    user_id    bigint       NOT NULL
);

CREATE INDEX idx_role
    ON account.user_roles (role);

CREATE INDEX idx_status
    ON account.user_roles (status);

CREATE INDEX idx_user_id
    ON account.user_roles (user_id);

CREATE TABLE account.user_roles_aud
(
    id         bigint       NOT NULL,
    rev        int          NOT NULL,
    revtype    tinyint      NULL,
    created_at datetime     NULL,
    updated_at datetime     NULL,
    created_by bigint       NULL,
    updated_by bigint       NULL,
    role       varchar(100) NULL,
    status     varchar(50)  NULL,
    user_id    bigint       NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKox6xyy64fyq0y3dvv5ve53a0h
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

CREATE TABLE account.users
(
    id                                 bigint AUTO_INCREMENT
        PRIMARY KEY,
    created_at                         datetime     NOT NULL,
    updated_at                         datetime     NOT NULL,
    created_by                         bigint       NOT NULL,
    updated_by                         bigint       NOT NULL,
    email                              varchar(100) NULL,
    email_domain                       varchar(50)  NULL,
    hubspot_contact_id                 bigint       NULL,
    language                           varchar(10)  NOT NULL,
    last_login_date                    datetime     NULL,
    service_region_locale_mapping_code varchar(255) NOT NULL,
    service_region_locale_mapping_id   bigint       NOT NULL,
    market_info_receive                varchar(50)  NOT NULL,
    nickname                           varchar(30)  NOT NULL,
    organization_id                    bigint       NULL,
    organization_status                varchar(50)  NULL,
    password                           varchar(100) NOT NULL,
    privacy_policy                     varchar(50)  NOT NULL,
    profile_color                      varchar(20)  NOT NULL,
    profile_image                      varchar(255) NULL,
    referrer_url                       varchar(255) NULL,
    service_region_aws_code            varchar(255) NOT NULL,
    service_region_code                varchar(255) NOT NULL,
    service_region_id                  bigint       NOT NULL,
    status                             varchar(50)  NOT NULL,
    terms_of_service                   varchar(50)  NOT NULL,
    CONSTRAINT idx_email
        UNIQUE (email)
);

CREATE INDEX idx_created_at
    ON account.users (created_at);

CREATE INDEX idx_email_domain
    ON account.users (email_domain);

CREATE INDEX idx_hubspot_contact_id
    ON account.users (hubspot_contact_id);

CREATE INDEX idx_market_info_receive
    ON account.users (market_info_receive);

CREATE INDEX idx_nickname
    ON account.users (nickname);

CREATE INDEX idx_organization_id
    ON account.users (organization_id);

CREATE INDEX idx_organization_status
    ON account.users (organization_status);

CREATE INDEX idx_service_region_locale_mapping_id
    ON account.users (service_region_locale_mapping_id);

CREATE INDEX idx_status
    ON account.users (status);

CREATE INDEX idx_updated_at
    ON account.users (updated_at);

CREATE TABLE account.users_aud
(
    id                                 bigint       NOT NULL,
    rev                                int          NOT NULL,
    revtype                            tinyint      NULL,
    created_at                         datetime     NULL,
    updated_at                         datetime     NULL,
    created_by                         bigint       NULL,
    updated_by                         bigint       NULL,
    email                              varchar(100) NULL,
    email_domain                       varchar(50)  NULL,
    hubspot_contact_id                 bigint       NULL,
    language                           varchar(10)  NULL,
    last_login_date                    datetime     NULL,
    service_region_locale_mapping_code varchar(255) NULL,
    service_region_locale_mapping_id   bigint       NULL,
    market_info_receive                varchar(50)  NULL,
    nickname                           varchar(30)  NULL,
    organization_id                    bigint       NULL,
    organization_status                varchar(50)  NULL,
    password                           varchar(100) NULL,
    privacy_policy                     varchar(50)  NULL,
    profile_color                      varchar(20)  NULL,
    profile_image                      varchar(255) NULL,
    referrer_url                       varchar(255) NULL,
    service_region_aws_code            varchar(255) NULL,
    service_region_code                varchar(255) NULL,
    service_region_id                  bigint       NULL,
    status                             varchar(50)  NULL,
    terms_of_service                   varchar(50)  NULL,
    PRIMARY KEY (id, rev),
    CONSTRAINT FKc4vk4tui2la36415jpgm9leoq
        FOREIGN KEY (rev) REFERENCES account.revinfo (rev)
);

-- auto_increment
ALTER TABLE account.admin_user_roles
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.admin_users
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.domains
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.external_service_mappings
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.invite
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.item_payment_links
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.items
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.license_attributes
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.license_grades
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.licenses
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organizations
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.products
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.service_region_locale_mappings
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.service_regions
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.user_roles
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.users
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_contracts
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_license_attributes
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_license_additional_attributes
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_licenses
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.update_guides
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.coupons
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.event_popups
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.coupon_delivery_history
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.service_locale_states
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.mobile_managements
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.mobile_force_update_minimum_versions
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.control_mode_access_histories
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.email_customizing_managements
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_license_keys
    AUTO_INCREMENT = 10000000000;
ALTER TABLE account.organization_license_track_sdk_usage_histories
    AUTO_INCREMENT = 10000000000;
