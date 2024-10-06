set FOREIGN_KEY_CHECKS = 0;
truncate table `item_payment_links`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `item_payment_links`
    ALTER COLUMN id RESTART WITH 1000000000;


insert into item_payment_links
(id, created_at, updated_at, created_by, updated_by, item_id, user_id, email, email_domain, expired_at)
values
    (1000000000, now(), now(), 1000000000, 1000000000, 1000000009, 1000000001, 'organiowner@virnect.com', 'virnect.com', '9999-12-31 11:59:00')
;
