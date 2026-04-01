CREATE VIEW projects_view WITH (security_invoker = true) AS
SELECT projects.pro_id,
       projects.team_key,
       projects.parent,
       projects.organization,
       projects.name,
       projects.organization || ' - ' || projects.name AS fullname,
       projects.color,
       projects.text_color,
       projects.parent IS NULL                         AS is_master,
       projects.created_by,
       projects.updated_by,
       projects.insert_date,
       projects.update_date,
       projects.row_version,
       parent.name                                     AS parent_name,
       parent.organization                             AS parent_organization,
       parent.organization || ' - ' || parent.name     AS parent_fullname
FROM projects
         LEFT OUTER JOIN projects parent ON projects.parent = parent.pro_id;
