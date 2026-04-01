CREATE TABLE IF NOT EXISTS teams
(
    tea_id      UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    key         VARCHAR(255) UNIQUE NOT NULL CHECK (key ~ '^[A-Z][A-Z0-9_]*[A-Z0-9]$'),
    name        VARCHAR(100)        NOT NULL,
    description VARCHAR(255),
    is_public   boolean             NOT NULL DEFAULT FALSE,
    created_by  TEXT                NOT NULL,
    updated_by  TEXT                NOT NULL,
    row_version BIGINT              NOT NULL DEFAULT 0,
    insert_date TIMESTAMPTZ         NOT NULL DEFAULT now(),
    update_date TIMESTAMPTZ         NOT NULL DEFAULT now()
);

COMMENT ON TABLE teams IS 'Table containing all teams in the application. Each team groups a set of applications and users.';

COMMENT ON COLUMN teams.tea_id IS 'Primary key identifying the team.';
COMMENT ON COLUMN teams.key IS 'Unique key identifier for the team, must be uppercase letters and underscores.';
COMMENT ON COLUMN teams.name IS 'Human-readable name of the team.';
COMMENT ON COLUMN teams.description IS 'Optional description of the team''s purpose or scope.';
COMMENT ON COLUMN teams.is_public IS 'Indicates whether the team is publicly visible.';
COMMENT ON COLUMN teams.created_by IS 'Identifier of the user who created this row.';
COMMENT ON COLUMN teams.updated_by IS 'Identifier of the last user who modified this row.';
COMMENT ON COLUMN teams.row_version IS 'Optimistic locking version, incremented on each update.';
COMMENT ON COLUMN teams.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN teams.update_date IS 'Timestamp of the last update to this row.';

CREATE UNIQUE INDEX idx_teams_key ON teams (key);

ALTER TABLE teams
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY teams_select_policy ON teams
    FOR SELECT
    USING (
        is_public = TRUE
        OR app_is_admin()
        OR app_in('manager_of', key)
        OR app_in('viewer_of', key)
        OR app_in('deployer_of', key)
    );

CREATE POLICY teams_insert_policy ON teams
    FOR INSERT
    WITH CHECK (
        app_is_admin()
        OR app_in('manager_of', key)
    );

CREATE POLICY teams_update_policy ON teams
    FOR UPDATE
    USING (
        app_is_admin()
        OR app_in('manager_of', key)
    );

CREATE POLICY teams_delete_policy ON teams
    FOR DELETE
    USING (
        app_is_admin()
        OR app_in('manager_of', key)
    );
