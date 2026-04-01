CREATE TABLE IF NOT EXISTS application_configuration (
    apc_id      UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    key         TEXT UNIQUE NOT NULL,
    value       TEXT NOT NULL,
    created_by  TEXT NOT NULL,
    updated_by  TEXT NOT NULL,
    insert_date TIMESTAMPTZ  NOT NULL DEFAULT now(),
    update_date TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_application_configuration_key ON application_configuration(key);

COMMENT ON TABLE application_configuration IS 'Generic table for storing dynamic application configuration parameters. Each row represents a key-value pair configurable via the front-end or backend.';

COMMENT ON COLUMN application_configuration.apc_id      IS 'Primary key identifying the application configuration.';
COMMENT ON COLUMN application_configuration.key         IS 'Unique identifier of the configuration parameter (e.g., export.cron, export.storage).';
COMMENT ON COLUMN application_configuration.value       IS 'The value of the configuration parameter stored as TEXT. Can contain JSON, YAML, or a simple string.';
COMMENT ON COLUMN application_configuration.created_by  IS 'Identifier of the user who created this configuration entry.';
COMMENT ON COLUMN application_configuration.updated_by  IS 'Identifier of the user who last updated this configuration entry.';
COMMENT ON COLUMN application_configuration.insert_date IS 'Timestamp when this configuration entry was created.';
COMMENT ON COLUMN application_configuration.update_date IS 'Timestamp of the last update to this configuration entry.';

ALTER TABLE application_configuration ENABLE ROW LEVEL SECURITY;

CREATE POLICY app_config_select_policy ON application_configuration
    FOR SELECT
    USING (
        current_setting('app.roles', true) IS NOT NULL
        AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
    );

CREATE POLICY app_config_insert_policy ON application_configuration
    FOR INSERT
    WITH CHECK (
        current_setting('app.roles', true) IS NOT NULL
        AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
    );

CREATE POLICY app_config_update_policy ON application_configuration
    FOR UPDATE
    USING (
        current_setting('app.roles', true) IS NOT NULL
        AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
    )
    WITH CHECK (
        current_setting('app.roles', true) IS NOT NULL
        AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
    );

CREATE POLICY app_config_delete_policy ON application_configuration
    FOR DELETE
    USING (
        current_setting('app.roles', true) IS NOT NULL
        AND current_setting('app.roles', true)::jsonb ? 'ADMIN'
    );