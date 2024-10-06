set
    FOREIGN_KEY_CHECKS = 0;
truncate table `service_regions`;
set
    FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `service_regions`
    ALTER COLUMN id RESTART WITH 1000000000;

INSERT INTO `service_regions` (`created_at`, `updated_at`, `created_by`, `updated_by`, `aws_code`, `code`,
                               `name`)
VALUES (now(), now(), 1000000000, 1000000000, 'ap-northeast-2', 'KR', 'Asia Pacific (Seoul)'),
       (now(), now(), 1000000000, 1000000000, 'us-east-1', 'US', '미국 동부(버지니아 북부)'),
       (now(), now(), 1000000000, 1000000000, 'eu-central-1', 'EU', 'Europe (Frankfurt)');