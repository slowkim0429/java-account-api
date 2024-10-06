set FOREIGN_KEY_CHECKS = 0;
truncate table `invite`;
set FOREIGN_KEY_CHECKS = 1;

ALTER TABLE `invite`
    ALTER COLUMN `id` RESTART WITH 1000000000;

insert into invite (created_at, updated_at, organization_id, email, expired_at, invite_type, invite_status, invite_role,
                    workspace_id, group_id, created_by, updated_by)
values (now(), now(), 1000000000, 'inviteUser1@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser3@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser4@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser5@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser6@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'GROUP',
        'CANCEL',
        'ROLE_GROUP_USER', 1000000000, 1000000000, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'GROUP',
        'PENDING',
        'ROLE_GROUP_USER', 1000000000, 1000000000, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'notregister@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'GROUP',
        'PENDING',
        'ROLE_GROUP_USER', 1000000000, 1000000000, 1000000001, 1000000001),
       (now(), now(), 1000000005, 'notregister@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000005, null, 1000000005, 1000000005),
       (now(), now(), 1000000000, 'inviteUser@virnect.com', TIMESTAMPADD(YEAR, 1, now()), 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001),
       (now(), now(), 1000000000, 'inviteUser7@virnect.com', '2022-11-22 16:57:38', 'WORKSPACE',
        'PENDING',
        'ROLE_WORKSPACE_USER', 1000000000, null, 1000000001, 1000000001)
;
