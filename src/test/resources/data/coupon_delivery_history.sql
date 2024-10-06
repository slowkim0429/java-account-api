set
    FOREIGN_KEY_CHECKS = 0;
truncate table `coupon_delivery_history`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `coupon_delivery_history`
    ALTER COLUMN id RESTART WITH 1000000000;


insert into coupon_delivery_history
(id, created_at, updated_at, created_by, updated_by, coupon_id, event_popup_id, receiver_user_id, receiver_email, receiver_email_domain)
values (1000000000, now(), now(), 1000000000, 1000000000, 1000000000, 1000000003, 1000000000, 'slowkim@gggg.com', 'gggg.com'),
       (1000000001, now(), now(), 0, 0, 1000000000, 1000000003, null, 'couponuser1@virnect.com', 'virnect.com'),
       (1000000002, now(), now(), 0, 0, 1000000000, 1000000003, null, 'couponuser2@virnect.com', 'virnect.com');
