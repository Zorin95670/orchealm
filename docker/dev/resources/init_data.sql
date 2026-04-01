-- TEAM

INSERT INTO teams (key,
                   name,
                   description,
                   is_public,
                   created_by,
                   updated_by)
VALUES ('PLATFORM',
        'Équipe Plateforme',
        'Équipe responsable des applications et services transverses de l’entreprise',
        true,
        'system',
        'system')
ON CONFLICT DO NOTHING;

INSERT INTO teams (key,
                   name,
                   description,
                   created_by,
                   updated_by)
VALUES ('TEAM_1',
        'Equipe Produit',
        'Equipe responsable des applications métier',
        'system',
        'system')
ON CONFLICT DO NOTHING;

-- ENVIRONMENTS

INSERT INTO environments (team_key,
                          name,
                          shortname,
                          position,
                          created_by,
                          updated_by)
VALUES ('PLATFORM',
        'Official',
        'OFFICIAL',
        1,
        'system',
        'system'),
       ('PLATFORM',
        'Integration',
        'INTEG',
        2,
        'system',
        'system'),
       ('PLATFORM',
        'Production',
        'PROD',
        3,
        'system',
        'system')
ON CONFLICT DO NOTHING;

INSERT INTO environments (team_key,
                          name,
                          shortname,
                          position,
                          created_by,
                          updated_by)
VALUES ('TEAM_1',
        'Development',
        'DEV',
        1,
        'system',
        'system'),
       ('TEAM_1',
        'Integration',
        'INTEG',
        2,
        'system',
        'system'),
       ('TEAM_1',
        'Pre-Production',
        'PREPROD',
        3,
        'system',
        'system'),
       ('TEAM_1',
        'Production',
        'PROD',
        4,
        'system',
        'system')
ON CONFLICT DO NOTHING;

-- PROJETS

INSERT INTO projects (team_key,
                      parent,
                      organization,
                      name,
                      color,
                      text_color,
                      is_master,
                      created_by,
                      updated_by)
VALUES ('PLATFORM',
        NULL,
        'Github',
        'Grafana',
        '#074650',
        'white',
        TRUE,
        'system',
        'system')
ON CONFLICT DO NOTHING;

INSERT INTO projects (team_key,
                      parent,
                      organization,
                      name,
                      color,
                      text_color,
                      is_master,
                      created_by,
                      updated_by)
VALUES ('TEAM_1',
        NULL,
        'ACME',
        'Portail Client',
        '#074650',
        'white',
        TRUE,
        'system',
        'system'),
       ('TEAM_1',
        NULL,
        'ACME',
        'API Metier',
        '#5C0001',
        'white',
        TRUE,
        'system',
        'system'),
       ('TEAM_1',
        NULL,
        'ACME',
        'Application Mobile',
        '#8BC7F7',
        'black',
        TRUE,
        'system',
        'system')
ON CONFLICT DO NOTHING;

DO
$$
    DECLARE
        p1      UUID;
        p2      UUID;
        p3      UUID;
        dev     UUID;
        integ   UUID;
        preprod UUID;
        prod    UUID;
    BEGIN
        SELECT pro_id INTO p1 FROM projects WHERE name = 'Portail Client';
        SELECT pro_id INTO p2 FROM projects WHERE name = 'API Metier';
        SELECT pro_id INTO p3 FROM projects WHERE name = 'Application Mobile';

        SELECT env_id INTO dev FROM environments WHERE shortname = 'DEV';
        SELECT env_id INTO integ FROM environments WHERE shortname = 'INTEG';
        SELECT env_id INTO preprod FROM environments WHERE shortname = 'PREPROD';
        SELECT env_id INTO prod FROM environments WHERE shortname = 'PROD';

        -- P1 DEV CLIENT A => latest 4.3.3 COMPLETED
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p1, dev, 'Client A', 'v4.3.2', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system'),
               ('COMPLETED', 'TEAM_1', p1, dev, 'Client A', 'v4.3.3', null,
                tstzrange('2024-01-05T12:00:00Z', '2025-01-05T13:00:00Z', '[)'), 'system', 'system'),
               ('FAILED', 'TEAM_1', p1, dev, 'Client A', 'v4.3.4', null,
                tstzrange('2024-01-06T12:00:00Z', '2025-01-06T13:00:00Z', '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        -- P1 DEV CLIENT B => latest 4.3.3 IN_PROGRESS
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p1, dev, 'Client B', 'v4.3.2', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system'),
               ('IN_PROGRESS', 'TEAM_1', p1, dev, 'Client B', 'v4.3.3', null,
                tstzrange('2025-01-05T12:00:00Z', null, '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        -- P2 DEV Internal => latest 2.1.0 COMPLETED
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p2, dev, 'Internal', 'v2.1.0', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        -- P2 INTEG Internal => latest 2.0.11 COMPLETED
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p2, integ, 'Internal', 'v2.0.11', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        -- P2 PRE-PROD client A / client B => latest 2.0.5 COMPLETED
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p2, preprod, 'Client A', 'v2.0.5', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system'),
               ('COMPLETED', 'TEAM_1', p2, preprod, 'Client B', 'v2.0.5', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        -- P2 PRE-PROD client A / client B => latest 2.0.5 / 2.0.0 COMPLETED
        INSERT INTO deployments(status, team_key, pro_id, env_id, client, version, planned_period, actual_period,
                                created_by, updated_by)
        VALUES ('COMPLETED', 'TEAM_1', p2, prod, 'Client A', 'v2.0.5', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system'),
               ('COMPLETED', 'TEAM_1', p2, prod, 'Client B', 'v2.0.0', null,
                tstzrange('2024-12-31T00:00:00Z', '2024-12-31T01:00:00Z', '[)'), 'system', 'system')
        ON CONFLICT DO NOTHING;

        INSERT INTO deployments (status,
                                 team_key,
                                 pro_id,
                                 env_id,
                                 client,
                                 version,
                                 planned_period,
                                 actual_period,
                                 created_by,
                                 updated_by)
        VALUES

-- ==========================
-- MOIS PRECEDENT
-- ==========================

('COMPLETED', 'TEAM_1', p1, dev, 'Client A', 'v4.4.0',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '2 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '2 day 10 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '2 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '2 day 10 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p2, integ, 'Internal', 'v2.1.0',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '5 day 14 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '5 day 15 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '5 day 14 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '5 day 15 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p3, preprod, 'Client B', 'v1.8.0',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '8 day 10 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '8 day 11 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '8 day 10 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '8 day 11 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p1, prod, 'Client B', 'v4.4.1',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '11 day 15 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '11 day 16 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '11 day 15 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '11 day 16 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p2, preprod, 'Client A', 'v2.1.1',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '14 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '14 day 10 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '14 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '14 day 10 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p3, dev, 'Internal', 'v1.8.1',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '17 day 11 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '17 day 12 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '17 day 11 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '17 day 12 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p1, integ, 'Client A', 'v4.4.2',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '20 day 14 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '20 day 15 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '20 day 14 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '20 day 15 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p2, prod, 'Client B', 'v2.1.2',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '23 day 10 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '23 day 11 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '23 day 10 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '23 day 11 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p3, integ, 'Client A', 'v1.8.2',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '26 day 15 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '26 day 16 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '26 day 15 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '26 day 16 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p1, preprod, 'Internal', 'v4.4.3',
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '28 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '28 day 10 hour', '[)'),
 tstzrange(date_trunc('month', current_date) - interval '1 month' + interval '28 day 09 hour',
           date_trunc('month', current_date) - interval '1 month' + interval '28 day 10 hour', '[)'),
 'system', 'system'),

-- ==========================
-- MOIS COURANT
-- ==========================

('COMPLETED', 'TEAM_1', p1, dev, 'Client A', 'v4.5.0',
 tstzrange(date_trunc('month', current_date) + interval '2 day 09 hour',
           date_trunc('month', current_date) + interval '2 day 10 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '2 day 09 hour',
           date_trunc('month', current_date) + interval '2 day 10 hour', '[)'),
 'system', 'system'),

('COMPLETED', 'TEAM_1', p2, integ, 'Internal', 'v2.2.0',
 tstzrange(date_trunc('month', current_date) + interval '5 day 14 hour',
           date_trunc('month', current_date) + interval '5 day 15 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '5 day 14 hour',
           date_trunc('month', current_date) + interval '5 day 15 hour', '[)'),
 'system', 'system'),

('IN_PROGRESS', 'TEAM_1', p3, preprod, 'Client B', 'v1.9.0',
 tstzrange(date_trunc('month', current_date) + interval '8 day 10 hour',
           date_trunc('month', current_date) + interval '8 day 11 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '8 day 10 hour', NULL, '[)'),
 'system', 'system'),

('PLANNED', 'TEAM_1', p1, prod, 'Client B', 'v4.5.1',
 tstzrange(date_trunc('month', current_date) + interval '11 day 15 hour',
           date_trunc('month', current_date) + interval '11 day 16 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p2, preprod, 'Client A', 'v2.2.1',
 tstzrange(date_trunc('month', current_date) + interval '14 day 09 hour',
           date_trunc('month', current_date) + interval '14 day 10 hour', '[)'),
 NULL,
 'system', 'system'),

('COMPLETED', 'TEAM_1', p3, dev, 'Internal', 'v1.9.1',
 tstzrange(date_trunc('month', current_date) + interval '17 day 11 hour',
           date_trunc('month', current_date) + interval '17 day 12 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '17 day 11 hour',
           date_trunc('month', current_date) + interval '17 day 12 hour', '[)'),
 'system', 'system'),

('PLANNED', 'TEAM_1', p1, integ, 'Client A', 'v4.5.2',
 tstzrange(date_trunc('month', current_date) + interval '20 day 14 hour',
           date_trunc('month', current_date) + interval '20 day 15 hour', '[)'),
 NULL,
 'system', 'system'),

('IN_PROGRESS', 'TEAM_1', p2, prod, 'Client B', 'v2.2.2',
 tstzrange(date_trunc('month', current_date) + interval '23 day 10 hour',
           date_trunc('month', current_date) + interval '23 day 11 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '23 day 10 hour', NULL, '[)'),
 'system', 'system'),

('PLANNED', 'TEAM_1', p3, integ, 'Client A', 'v1.9.2',
 tstzrange(date_trunc('month', current_date) + interval '26 day 15 hour',
           date_trunc('month', current_date) + interval '26 day 16 hour', '[)'),
 NULL,
 'system', 'system'),

('COMPLETED', 'TEAM_1', p1, preprod, 'Internal', 'v4.5.3',
 tstzrange(date_trunc('month', current_date) + interval '28 day 09 hour',
           date_trunc('month', current_date) + interval '28 day 10 hour', '[)'),
 tstzrange(date_trunc('month', current_date) + interval '28 day 09 hour',
           date_trunc('month', current_date) + interval '28 day 10 hour', '[)'),
 'system', 'system'),

-- ==========================
-- MOIS SUIVANT
-- ==========================

('PLANNED', 'TEAM_1', p1, dev, 'Client A', 'v4.6.0',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '2 day 09 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '2 day 10 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p2, integ, 'Internal', 'v2.3.0',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '5 day 14 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '5 day 15 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p3, preprod, 'Client B', 'v2.0.0',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '8 day 10 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '8 day 11 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p1, prod, 'Client B', 'v4.6.1',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '11 day 15 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '11 day 16 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p2, preprod, 'Client A', 'v2.3.1',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '14 day 09 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '14 day 10 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p3, dev, 'Internal', 'v2.0.1',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '17 day 11 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '17 day 12 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p1, integ, 'Client A', 'v4.6.2',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '20 day 14 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '20 day 15 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p2, prod, 'Client B', 'v2.3.2',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '23 day 10 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '23 day 11 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p3, integ, 'Client A', 'v2.0.2',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '26 day 15 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '26 day 16 hour', '[)'),
 NULL,
 'system', 'system'),

('PLANNED', 'TEAM_1', p1, preprod, 'Internal', 'v4.6.3',
 tstzrange(date_trunc('month', current_date) + interval '1 month' + interval '28 day 09 hour',
           date_trunc('month', current_date) + interval '1 month' + interval '28 day 10 hour', '[)'),
 NULL,
 'system', 'system');

    END
$$;
