CREATE TABLE IF NOT EXISTS environments
(
    env_id      UUID PRIMARY KEY                                                                 DEFAULT gen_random_uuid(),
    team_key    VARCHAR(255) REFERENCES teams (key) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    name        VARCHAR(100)                                                            NOT NULL,
    shortname   VARCHAR(10)                                                             NOT NULL,
    position    INT                                                                     NOT NULL DEFAULT 0 CHECK (position >= 0),
    created_by  TEXT                                                                    NOT NULL,
    updated_by  TEXT                                                                    NOT NULL,
    insert_date TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    update_date TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    CONSTRAINT uc_environments_team_key_name UNIQUE (team_key, name),
    CONSTRAINT uc_environments_team_key_shortname UNIQUE (team_key, shortname)
);

COMMENT ON TABLE environments IS 'Table containing all environments (e.g., Development, Integration, Pre-production, Production) used for deployments and project configurations.';

COMMENT ON COLUMN environments.env_id IS 'Primary key identifying the environment.';
COMMENT ON COLUMN environments.team_key IS 'Foreign key referencing the team this environment belongs to.';
COMMENT ON COLUMN environments.name IS 'Full name of the environment (e.g., Development, Production), unique within the team.';
COMMENT ON COLUMN environments.shortname IS 'Short abbreviation for the environment (e.g., DEV, PROD), unique within the team.';
COMMENT ON COLUMN environments.position IS 'Numeric order for display or sorting purposes.';
COMMENT ON COLUMN environments.created_by IS 'Identifier of the user who created this environment.';
COMMENT ON COLUMN environments.updated_by IS 'Identifier of the last user who modified this environment.';
COMMENT ON COLUMN environments.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN environments.update_date IS 'Timestamp of the last update to this row.';

CREATE INDEX idx_environments_team_key ON environments (team_key);

ALTER TABLE environments
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY environments_select_policy ON environments
    FOR SELECT
    USING (
    EXISTS (SELECT 1
            FROM teams
            WHERE teams.key = environments.team_key
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

CREATE POLICY environments_insert_policy ON environments
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

CREATE POLICY environments_update_policy ON environments
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

CREATE POLICY environments_delete_policy ON environments
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