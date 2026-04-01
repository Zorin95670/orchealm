CREATE TABLE IF NOT EXISTS projects
(
    pro_id       UUID PRIMARY KEY                                                                 DEFAULT gen_random_uuid(),
    team_key     VARCHAR(255) REFERENCES teams (key) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    parent       UUID                                                                    REFERENCES projects (pro_id) ON DELETE SET NULL,
    organization VARCHAR(255)                                                            NOT NULL,
    name         VARCHAR(100)                                                            NOT NULL,
    color        VARCHAR(7)                                                              NOT NULL,
    text_color   VARCHAR(100)                                                            NOT NULL,
    is_master    BOOLEAN                                                                 NOT NULL DEFAULT TRUE,
    created_by   TEXT                                                                    NOT NULL,
    updated_by   TEXT                                                                    NOT NULL,
    insert_date  TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    update_date  TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    CONSTRAINT uc_projects_team_key_organisation_name UNIQUE (team_key, organization, name)
);

COMMENT ON TABLE projects IS 'Table containing all projects managed in Orchealm, with metadata including name and display color.';

COMMENT ON COLUMN projects.pro_id IS 'Primary key identifying the project.';
COMMENT ON COLUMN projects.team_key IS 'Foreign key referencing the team this project belongs to.';
COMMENT ON COLUMN projects.parent IS 'Reference to the master project. NULL if this project is a master or has no parent.';
COMMENT ON COLUMN projects.organization IS 'Organization or owner of the project.';
COMMENT ON COLUMN projects.name IS 'Full name of the project.';
COMMENT ON COLUMN projects.color IS 'Project display color stored as a hex color code (e.g. #RRGGBB).';
COMMENT ON COLUMN projects.text_color IS 'Project text color using Quasar color tokens (e.g. white, black, grey-8, primary).';
COMMENT ON COLUMN projects.is_master IS 'Flag indicating if this project is a master project (TRUE) or a sub-project (FALSE).';
COMMENT ON COLUMN projects.created_by IS 'Identifier of the user who created this project.';
COMMENT ON COLUMN projects.updated_by IS 'Identifier of the last user who modified this project.';
COMMENT ON COLUMN projects.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN projects.update_date IS 'Timestamp of the last update to this row.';

CREATE INDEX idx_projects_team_key ON projects (team_key);
CREATE INDEX idx_projects_parent ON projects (parent);

ALTER TABLE projects
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY projects_select_policy ON projects
    FOR SELECT
    USING (
    EXISTS (SELECT 1
            FROM teams
            WHERE teams.key = projects.team_key
              AND teams.is_public = TRUE)
        OR (
        current_setting('app.roles', true) IS NOT NULL
            AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
        )
        OR (
        current_setting('app.teams', true) IS NOT NULL
            AND (current_setting('app.teams', true)::jsonb ? team_key)
            AND (
            (current_setting('app.teams', true)::jsonb -> team_key)
                ?| array ['VIEWER', 'EDITOR']
            )
        )
    );

CREATE POLICY projects_insert_policy ON projects
    FOR INSERT
    WITH CHECK (
    (
        current_setting('app.roles', true) IS NOT NULL
            AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
        )
        OR (
        current_setting('app.teams', true) IS NOT NULL
            AND (current_setting('app.teams', true)::jsonb ? team_key)
            AND (
            (current_setting('app.teams', true)::jsonb -> team_key)
                ? 'EDITOR'
            )
        )
    );

CREATE POLICY projects_update_policy ON projects
    FOR UPDATE
    USING (
    (
        current_setting('app.roles', true) IS NOT NULL
            AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
        )
        OR (
        current_setting('app.teams', true) IS NOT NULL
            AND (current_setting('app.teams', true)::jsonb ? team_key)
            AND (
            (current_setting('app.teams', true)::jsonb -> team_key)
                ? 'EDITOR'
            )
        )
    );

CREATE POLICY projects_delete_policy ON projects
    FOR DELETE
    USING (
    (
        current_setting('app.roles', true) IS NOT NULL
            AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
        )
        OR (
        current_setting('app.teams', true) IS NOT NULL
            AND (current_setting('app.teams', true)::jsonb ? team_key)
            AND (
            (current_setting('app.teams', true)::jsonb -> team_key)
                ? 'EDITOR'
            )
        )
    );