set
    FOREIGN_KEY_CHECKS = 0;
truncate table `items`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `items`
    ALTER COLUMN id RESTART WITH 1000000000;


insert into items
(id, created_at, updated_at, created_by, updated_by, status, item_type, name, license_id, license_attribute_id,
 recurring_interval, use_status, payment_type, currency_type, amount, monthly_used_amount, is_exposed)
values (1000000000, now(), now(), 1000000000, 1000000000, 'REGISTER', 'LICENSE', 'Remote Goods1', 1000000001, null,
        'MONTH', 'USE', 'SUBSCRIPTION', 'EUR', 100, 100, false),
       (1000000001, now(), now(), 1000000000, 1000000000, 'REJECT', 'LICENSE', 'Remote Goods2', 1000000001, null,
        'MONTH', 'USE', 'NONE', 'EUR', 100, 100, false),
       (1000000002, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Remote Goods3', 1000000001, null,
        'MONTH', 'USE', 'NONRECURRING', 'EUR', 100, 100, false),
       (1000000003, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Workspace Goods1', 1000000005, null,
        'MONTH', 'DELETE', 'NONE', 'EUR', 100, 100, false),
       (1000000004, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Studio Goods1', 1000000004, null,
        'MONTH', 'USE', 'NONE', 'EUR', 100, 100, false),
       (1000000005, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Studio Goods2', 1000000004, null,
        'MONTH', 'USE', 'NONE', 'EUR', 100, 100, false),
       (1000000006, now(), now(), 1000000000, 1000000000, 'REGISTER', 'LICENSE', 'Free Squars License', 1000000004,
        null, 'NONE', 'NONE', 'NONE', 'EUR', 0, 0, false),
       (1000000007, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Professional Squars License',
        1000000004, null, 'MONTH', 'USE', 'SUBSCRIPTION', 'EUR', 10000, 0, false),
       (1000000008, now(), now(), 1000000000, 1000000000, 'APPROVED', 'ATTRIBUTE',
        'Professional Squars License Attribute', 1000000004, 1000000018, 'NONE', 'USE', 'NONRECURRING', 'EUR', 10000, 0,
        false),
       (1000000009, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Enterpriese Squars License',
        1000000011, null, 'MONTH', 'USE', 'SUBSCRIPTION', 'EUR', 1000, 1000, false),
       (1000000010, now(), now(), 1000000000, 1000000000, 'APPROVED', 'ATTRIBUTE',
        'Enterpriese Squars License Attribute', 1000000011, 1000000018, 'NONE', 'USE', 'NONRECURRING', 'EUR', 10000, 0,
        false),
       (1000000011, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Enterpriese Squars License License',
        1000000012, 1000000018, 'YEAR', 'USE', 'SUBSCRIPTION', 'EUR', 1100, 100, false),
       (1000000012, now(), now(), 1000000000, 1000000000, 'REGISTER', 'ATTRIBUTE',
        'Enterpriese Squars License Attribute', 1000000011, 1000000018, 'NONE', 'NONE', 'NONRECURRING', 'EUR', 10000, 0,
        false),
       (1000000013, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Free Squars License', 1000000013,
        null, 'NONE', 'NONE', 'NONE', 'EUR', 0, 0, true),
       (1000000014, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Free Squars License', 1000000013,
        null, 'NONE', 'NONE', 'NONE', 'EUR', 0, 0, false),
       (1000000015, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE',
        'Professional Squars License Attribute', 1000000004, 1000000018, 'NONE', 'USE', 'NONRECURRING', 'EUR', 10000, 0,
        true),
       (1000000016, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE',
        'Professional Squars License Attribute', 1000000004, 1000000018, 'YEAR', 'USE', 'NONRECURRING', 'EUR', 10000, 0,
        true),
       (1000000017, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE',
        'Professional Squars License Attribute', 1000000004, 1000000018, 'MONTH', 'USE', 'NONRECURRING', 'EUR', 10000,
        0, true),
       (1000000018, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE',
        'Free + item. but recurringInterval invalid data', 1000000013, 1000000018, 'MONTH', 'USE',
        'NONRECURRING', 'EUR', 10000, 0, false),
       (1000000019, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE',
        'Standard item - month', 1000000005, 1000000018, 'MONTH', 'USE', 'NONE', 'EUR', 10000, 0, false),
       (1000000020, now(), now(), 1000000000, 1000000000, 'APPROVED', 'LICENSE', 'Track Free Plus Item', 1000000001, null,
        'NONE', 'USE', 'SUBSCRIPTION', 'EUR', 0, 0, true)
;
