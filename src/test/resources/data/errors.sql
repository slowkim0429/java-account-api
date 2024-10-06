set FOREIGN_KEY_CHECKS = 0;
truncate table `errors`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `errors` ALTER COLUMN id RESTART WITH 1000000000;


insert into`errors`
    (`device`, `elapsed_time`, `header`, `method`, `request_body`, `response_body`, `response_status`, `service`, `url`, `created_by`, `created_at`, `updated_by`, `updated_at`)
values
    ('PostmanRuntime/7.28.4', 2446, '{content-length=68, postman-token=93437372-90b1-4bbb-ae45-019f4e343e4f, host=localhost:8521, content-type=application/json, connection=keep-alive, accept-encoding=gzip, deflate, br, user-agent=PostmanRuntime/7.28.4, accept=*/*}',
     'POST', '{email=slowkim@virnect.com, password=********************}', '{"status":401,"error":"UNAUTHORIZED","customError":"INVALID_USER","message":"Please check your ID or password.","path":"/api/auth/login","timestamp":"2021-12-20 08:11:51","errors":null}',
     '401', 'account-api', '/api/auth/login?lang=en', 1, now(), 1, now())

