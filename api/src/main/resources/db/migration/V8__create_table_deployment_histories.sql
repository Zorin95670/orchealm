CREATE TABLE IF NOT EXISTS deployments_history
(
    dph_id     BIGSERIAL PRIMARY KEY,
    dpl_id     UUID        NOT NULL,
    operation  VARCHAR(10) NOT NULL CHECK (operation IN ('INSERT', 'UPDATE', 'DELETE')),
    snapshot   JSONB       NOT NULL,
    changed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    changed_by TEXT        NOT NULL DEFAULT current_user,
    txid       BIGINT      NOT NULL DEFAULT txid_current()
);

CREATE OR REPLACE FUNCTION audit_deployments()
    RETURNS TRIGGER AS
$$
DECLARE
    deployment_row  JSONB;
    project_row     JSONB;
    environment_row JSONB;
    team_row        JSONB;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        deployment_row := to_jsonb(OLD);
    ELSE
        deployment_row := to_jsonb(NEW);
    END IF;

    SELECT to_jsonb(p)
    INTO project_row
    FROM projects p
    WHERE p.pro_id = (deployment_row ->> 'pro_id')::UUID;

    SELECT to_jsonb(e)
    INTO environment_row
    FROM environments e
    WHERE e.env_id = (deployment_row ->> 'env_id')::UUID;

    SELECT to_jsonb(t)
    INTO team_row
    FROM teams t
    WHERE t.key = deployment_row ->> 'team_key';

    INSERT INTO deployments_history (dpl_id,
                                     operation,
                                     snapshot)
    VALUES ((deployment_row ->> 'dpl_id')::UUID,
            TG_OP,
            jsonb_build_object(
                    'deployment', deployment_row,
                    'project', project_row,
                    'environment', environment_row,
                    'team', team_row
            ));

    IF (TG_OP = 'DELETE') THEN
        RETURN OLD;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_audit_deployments
    AFTER INSERT OR UPDATE OR DELETE
    ON deployments
    FOR EACH ROW
EXECUTE FUNCTION audit_deployments();

CREATE INDEX idx_deployments_history_dpl_id
    ON deployments_history (dpl_id);

CREATE INDEX idx_deployments_history_changed_at
    ON deployments_history (changed_at);

CREATE INDEX idx_deployments_history_snapshot
    ON deployments_history
        USING GIN (snapshot);

REVOKE UPDATE, DELETE ON deployments_history FROM PUBLIC;
