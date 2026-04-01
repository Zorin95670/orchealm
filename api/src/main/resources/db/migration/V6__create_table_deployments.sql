CREATE TABLE IF NOT EXISTS deployments
(
    dpl_id         UUID PRIMARY KEY                                                                 DEFAULT gen_random_uuid(),
    team_key       VARCHAR(255) REFERENCES teams (key) ON UPDATE CASCADE ON DELETE CASCADE NOT NULL,
    env_id         UUID                                                                    NOT NULL,
    pro_id         UUID                                                                    NOT NULL,
    version        VARCHAR(100)                                                            NOT NULL,
    client         VARCHAR(100)                                                            NOT NULL DEFAULT 'Internal',
    planned_period TSTZRANGE,
    actual_period  TSTZRANGE,
    status         varchar(25)                                                             NOT NULL DEFAULT 'PLANNED',
    created_by     TEXT                                                                    NOT NULL,
    updated_by     TEXT                                                                    NOT NULL,
    row_version    BIGINT                                                                  NOT NULL DEFAULT 0,
    insert_date    TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    update_date    TIMESTAMPTZ                                                             NOT NULL DEFAULT now(),
    CONSTRAINT fk_deployments_environment FOREIGN KEY (team_key, env_id)
        REFERENCES environments (team_key, env_id) ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_deployments_project FOREIGN KEY (team_key, pro_id)
        REFERENCES projects (team_key, pro_id) ON UPDATE CASCADE ON DELETE CASCADE
);

COMMENT ON TABLE deployments IS 'Table recording deployments of projects to environments, including planned and actual deployment times, client, version, and status flags.';

COMMENT ON COLUMN deployments.dpl_id IS 'Primary key identifying the deployment.';
COMMENT ON COLUMN deployments.team_key IS 'Foreign key referencing the team this deployment belongs to.';
COMMENT ON COLUMN deployments.env_id IS 'Reference to the environment where the project is deployed.';
COMMENT ON COLUMN deployments.pro_id IS 'Reference to the project being deployed.';
COMMENT ON COLUMN deployments.version IS 'Version of the deployed project.';
COMMENT ON COLUMN deployments.client IS 'Client for whom the deployment is performed.';
COMMENT ON COLUMN deployments.planned_period IS 'Scheduled deployment period (start/end)';
COMMENT ON COLUMN deployments.actual_period IS 'Actual deployment period (start/end)';
COMMENT ON COLUMN deployments.status IS 'Deployment status: PLANNED, IN_PROGRESS, COMPLETED, CANCELLED, FAILED.';
COMMENT ON COLUMN deployments.created_by IS 'Identifier of the user who created this deployment.';
COMMENT ON COLUMN deployments.updated_by IS 'Identifier of the last user who modified this deployment.';
COMMENT ON COLUMN deployments.row_version IS 'Optimistic locking version, incremented on each update.';
COMMENT ON COLUMN deployments.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN deployments.update_date IS 'Timestamp of the last update to this row.';

CREATE INDEX idx_deployments_team_key ON deployments (team_key);
ALTER TABLE deployments
    ENABLE ROW LEVEL SECURITY;

CREATE POLICY deployments_select_policy ON deployments
    FOR SELECT
    USING (
        EXISTS (SELECT 1 FROM teams WHERE teams.key = deployments.team_key AND teams.is_public = TRUE)
        OR app_is_admin()
        OR app_in('manager_of', team_key)
        OR app_in('viewer_of', team_key)
        OR app_in('deployer_of', team_key)
    );

CREATE POLICY deployments_insert_policy ON deployments
    FOR INSERT
    WITH CHECK (
        app_is_admin()
        OR app_in('manager_of', team_key)
        OR app_in('deployer_of', team_key)
    );

CREATE POLICY deployments_update_policy ON deployments
    FOR UPDATE
    USING (
        app_is_admin()
        OR app_in('manager_of', team_key)
        OR app_in('deployer_of', team_key)
    );

CREATE POLICY deployments_delete_policy ON deployments
    FOR DELETE
    USING (
        app_is_admin()
        OR app_in('manager_of', team_key)
    );
