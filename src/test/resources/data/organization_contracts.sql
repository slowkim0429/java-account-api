set FOREIGN_KEY_CHECKS = 0;
truncate table `organization_contracts`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `organization_contracts`
    ALTER COLUMN id RESTART WITH 1000000000;


INSERT INTO organization_contracts (id, created_at, updated_at, created_by, updated_by, contract_id, end_date,
                                            item_id, item_name, item_type, organization_id, recurring_interval,
                                            start_date, status, payment_type, coupon_id)
VALUES (1000000000, now(), now(), 1000000000, 1000000000, 1000000000,
        '2023-04-01 01:47:13', 1000000000, 'professional for test m', 'LICENSE', 1000000000, 'MONTH',
        '2023-02-01 01:47:13', 'CANCELED', 'SUBSCRIPTION', null),
       (1000000001, now(), now(), 1000000000, 1000000000, 1000000001,
        '2023-04-01 01:47:13', 1000000000, 'professional for test m', 'LICENSE', 1000000000, 'MONTH',
        '2023-02-01 01:47:13', 'CANCELED', 'SUBSCRIPTION', null),
       (1000000002, now(), now(), 1000000000, 1000000000, 1000000002,
        '2023-04-01 01:47:13', 1000000000, 'professional for test m', 'LICENSE', 1000000000, 'MONTH',
        '2023-02-01 01:47:13', 'CANCELED', 'SUBSCRIPTION', null),
       (1000000003, now(), now(), 1000000000, 1000000000, 1000000003,
        '2023-02-01 01:47:13', 1000000008, 'professional for test m', 'ATTRIBUTE', 1000000000, 'NONE',
        '2023-02-01 01:47:13', 'PROCESSING', 'NONRECURRING', null);
