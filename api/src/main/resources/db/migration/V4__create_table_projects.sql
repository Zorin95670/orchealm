CREATE TABLE IF NOT EXISTS projects
(
    pro_id       UUID PRIMARY KEY                                                                 DEFAULT gen_random_uuid(),
    team_key     VARCHAR(255) REFERENCES teams (key) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    parent       UUID,
    organization VARCHAR(255)                                                            NOT NULL,
    name         VARCHAR(100)                                                            NOT NULL,
    color        VARCHAR(7)                                                              NOT NULL,
    text_color   VARCHAR(100)                                                            NOT NULL,
    created_by   TEXT                                                                    NOT NULL,
    updated_by   TEXT                                                                    NOT NULL,
    row_version  BIGINT                                                                  NOT NULL DEFAULT 0,
    insert_date  TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    update_date  TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    CONSTRAINT uc_projects_team_key_organisation_name UNIQUE (team_key, organization, name),
    -- Target of the deployments composite FK: ensures a deployment uses a project of its own team.
    CONSTRAINT uc_projects_team_key_pro_id UNIQUE (team_key, pro_id),
    -- The parent must belong to the same team. Deleting the parent only clears the parent column.
    CONSTRAINT fk_projects_parent FOREIGN KEY (team_key, parent)
        REFERENCES projects (team_key, pro_id) ON UPDATE CASCADE ON DELETE SET NULL (parent)
);

COMMENT ON TABLE projects IS 'Table containing all projects managed in Orchealm, with metadata including name and display color.';

COMMENT ON COLUMN projects.pro_id IS 'Primary key identifying the project.';
COMMENT ON COLUMN projects.team_key IS 'Foreign key referencing the team this project belongs to.';
COMMENT ON COLUMN projects.parent IS 'Reference to the master project, of the same team. NULL if this project is a master.';
COMMENT ON COLUMN projects.organization IS 'Organization or owner of the project.';
COMMENT ON COLUMN projects.name IS 'Full name of the project.';
COMMENT ON COLUMN projects.color IS 'Project display color stored as a hex color code (e.g. #RRGGBB).';
COMMENT ON COLUMN projects.text_color IS 'Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).';
COMMENT ON COLUMN projects.created_by IS 'Identifier of the user who created this project.';
COMMENT ON COLUMN projects.updated_by IS 'Identifier of the last user who modified this project.';
COMMENT ON COLUMN projects.row_version IS 'Optimistic locking version, incremented on each update.';
COMMENT ON COLUMN projects.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN projects.update_date IS 'Timestamp of the last update to this row.';

CREATE INDEX idx_projects_team_key ON projects (team_key);
CREATE INDEX idx_projects_parent ON projects (parent);

ALTER TABLE projects
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY projects_select_policy ON projects
    FOR SELECT
    USING (
        EXISTS (SELECT 1 FROM teams WHERE teams.key = projects.team_key AND teams.is_public = TRUE)
        OR app_is_admin()
        OR app_in('manager_of', team_key)
        OR app_in('viewer_of', team_key)
        OR app_in('deployer_of', team_key)
    );

CREATE POLICY projects_insert_policy ON projects
    FOR INSERT
    WITH CHECK (
        app_is_admin()
        OR app_in('manager_of', team_key)
    );

CREATE POLICY projects_update_policy ON projects
    FOR UPDATE
    USING (
        app_is_admin()
        OR app_in('manager_of', team_key)
    );

CREATE POLICY projects_delete_policy ON projects
    FOR DELETE
    USING (
        app_is_admin()
        OR app_in('manager_of', team_key)
    );
