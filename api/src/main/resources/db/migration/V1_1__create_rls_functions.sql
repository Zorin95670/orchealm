-- Helpers used by the RLS policies to read the context set by the API for the current transaction
-- (see RlsInterceptor):
--   app.is_admin     'true' when the user has the global ADMIN role
--   app.manager_of   JSON array of the keys of the teams the user manages
--   app.viewer_of    JSON array of the keys of the teams the user can view
--   app.deployer_of  JSON array of the keys of the teams the user can deploy to
--
-- current_setting(..., true) returns NULL when the variable was never set, and '' once a SET LOCAL
-- from a previous transaction has ended: both mean "no access".

CREATE OR REPLACE FUNCTION app_is_admin() RETURNS BOOLEAN
    LANGUAGE sql
    STABLE
AS
$$
SELECT coalesce(current_setting('app.is_admin', true), '') = 'true'
$$;

COMMENT ON FUNCTION app_is_admin() IS 'True when the current user has the global ADMIN role (app.is_admin).';

CREATE OR REPLACE FUNCTION app_in(setting TEXT, team_key TEXT) RETURNS BOOLEAN
    LANGUAGE sql
    STABLE
AS
$$
SELECT coalesce(nullif(current_setting('app.' || setting, true), ''), '[]')::jsonb ? team_key
$$;

COMMENT ON FUNCTION app_in(TEXT, TEXT) IS 'True when team_key is listed in app.<setting> (manager_of, viewer_of or deployer_of).';
