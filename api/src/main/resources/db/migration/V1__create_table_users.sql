CREATE TABLE IF NOT EXISTS users (
    usr_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    external_id TEXT        NOT NULL UNIQUE,
    name        TEXT,
    email       TEXT                 UNIQUE,
    insert_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    update_date TIMESTAMPTZ NOT NULL DEFAULT now()
);

COMMENT ON TABLE users IS 'Table containing all application users. Users are linked to external identity providers and can be assigned roles.';

COMMENT ON COLUMN users.usr_id      IS 'Primary key identifying the user.';
COMMENT ON COLUMN users.external_id IS 'External identifier for the user (from SSO or OIDC token).';
COMMENT ON COLUMN users.name        IS 'First name and last name of the user.';
COMMENT ON COLUMN users.email       IS 'Email address of the user.';
COMMENT ON COLUMN users.insert_date IS 'Timestamp when this row was created.';
COMMENT ON COLUMN users.update_date IS 'Timestamp of the last update to this row.';

INSERT INTO users (usr_id, external_id, name, email)
VALUES (gen_random_uuid(), 'SYSTEM', 'SYSTEM', NULL)
ON CONFLICT (external_id) DO NOTHING;
