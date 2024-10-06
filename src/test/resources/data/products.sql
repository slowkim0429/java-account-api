set FOREIGN_KEY_CHECKS = 0;
truncate table `products`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `products`
    ALTER COLUMN id RESTART WITH 1000000000;


INSERT INTO `products`
(`name`, `product_type`, `status`, `created_by`, `created_at`, `updated_by`, `updated_at`)
VALUES ('this is squars', 'SQUARS', 'APPROVED', 1000000000, now(), 1000000000, now()),
       ('TRACK SDK', 'TRACK', 'APPROVED', '0', now(), '0', now());
