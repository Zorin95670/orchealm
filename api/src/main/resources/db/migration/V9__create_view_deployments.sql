CREATE OR REPLACE VIEW deployments_view AS
SELECT r.dpl_id                AS "dpl_id",
       r.team_key              AS "team_key",
       r.pro_id                AS "pro_id",
       r.planned_period        AS "planned_period",
       lower(r.planned_period) AS "planned_period_start",
       upper(r.planned_period) AS "planned_period_end",
       r.actual_period         AS "actual_period",
       lower(r.actual_period)  AS "actual_period_start",
       upper(r.actual_period)  AS "actual_period_end",
       CASE
           WHEN r.status = 'IN_PROGRESS'
               AND r.planned_period IS NOT NULL
               AND upper(r.planned_period) IS NOT NULL
               AND now() > upper(r.planned_period)
               THEN true
           ELSE false
           END                 AS "delayed",

       CASE
           WHEN r.update_date >= now() - interval '1 hour'
               THEN 'HOT'
           WHEN r.update_date >= now() - interval '1 day'
               THEN 'NEW'
           END                 AS "state",

       p.parent                AS master_pro_id,
       p.name                  AS project_name,
       mp.name                 AS master_project_name,

       p.color                 AS "project_color",
       p.text_color            AS "project_text_color",

       e.name                  AS environment_name,
       e.shortname             AS environment_shortname,
       e.position              AS environment_position,

       r.version,
       r.client,
       r.status,
       r.insert_date,
       r.update_date,
       r.created_by,
       r.updated_by

FROM deployments r
         LEFT JOIN projects p ON p.pro_id = r.pro_id
         LEFT JOIN projects mp ON mp.pro_id = p.parent
         LEFT JOIN environments e ON e.env_id = r.env_id;