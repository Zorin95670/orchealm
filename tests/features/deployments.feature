Feature: Test plan Deployment

  Scenario: Plan a deployment
    Given I set http header "Content-Type" with "application/x-www-form-urlencoded"
    And   I store "defaultUser" as "team1" in context

    When  I request "{{env.E2E_KEYCLOAK_URL}}" with method "POST" with body:
    """
    grant_type=password&client_id={{env.E2E_KEYCLOAK_CLIENT_ID}}&username={{ctx.defaultUser}}&password=password
    """
    Then I expect status code is 200
    And  I set http header "Authorization" with "{{response.body.token_type}} {{response.body.access_token}}"
    And  I set http header "Content-Type" with "application/json"

    When I request "{{env.E2E_API_URL}}/environments" with method "POST" with body:
    """
    {
      "teamKey": "TEAM_1",
      "name": "Development",
      "shortName": "DEV",
      "position": 0
    }
    """
    And I log "{{ response.body | json }}"
    Then I expect status code is 201
    And I store "env_id" as "{{response.body.id}}" in context

    When I request "{{env.E2E_API_URL}}/projects" with method "POST" with body:
    """
    {
      "teamKey": "TEAM_1",
      "organization": "test",
      "name": "app",
      "colorR": 255,
      "colorG": 255,
      "colorB": 255
    }
    """
    And I log "{{ response.body | json }}"
    Then I expect status code is 201
    And I store "pro_id" as "{{response.body.id}}" in context

    When I request "{{env.E2E_API_URL}}/deployments" with method "POST" with body:
    """
    {
      "teamKey": "TEAM_1",
      "environment": "Development",
      "projectOrganization": "test",
      "projectName": "app",
      "version": "1.0.0",
      "client": "internal",
      "plannedPeriod": {
        "start": "2026-05-01T10:00:00.000Z",
        "end": "2026-05-01T12:00:00.000Z"
      }
    }
    """
    And I log "{{ response.body | json }}"
    Then I expect status code is 201

    When I request "{{env.E2E_API_URL}}/deployments/start" with method "POST" with body:
    """
    {
      "teamKey": "TEAM_1",
      "environment": "Development",
      "projectOrganization": "test",
      "projectName": "app",
      "version": "1.0.0",
      "client": "internal"
    }
    """
    And I log "{{ response.body | json }}"
    Then I expect status code is 201
    And I wait 5s

    When I request "{{env.E2E_API_URL}}/deployments/stop" with method "POST" with body:
    """
    {
      "teamKey": "TEAM_1",
      "environment": "Development",
      "projectOrganization": "test",
      "projectName": "app",
      "version": "1.0.0",
      "client": "internal"
    }
    """
    And I log "{{ response.body | json }}"
    Then I expect status code is 201

    When I request "{{env.E2E_API_URL}}/environments/{{ctx.env_id}}" with method "DELETE"
    Then I expect status code is 204

    When I request "{{env.E2E_API_URL}}/projects/{{ctx.pro_id}}" with method "DELETE"
    Then I expect status code is 204

