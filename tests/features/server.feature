Feature: Server feature

  Scenario: Check if application is up
    Given I set http header "Content-Type" with "application/x-www-form-urlencoded"
    And   I store "defaultUser" as "superAdmin" in context

    When  I request "{{env.E2E_KEYCLOAK_URL}}" with method "POST" with body:
    """
    grant_type=password&client_id={{env.E2E_KEYCLOAK_CLIENT_ID}}&username={{ctx.defaultUser}}&password=password
    """
    Then I expect status code is 200
    And  I set http header "Authorization" with "{{response.body.token_type}} {{response.body.access_token}}"
    And  I set http header "Content-Type" with "application/json"

    When I request "{{env.E2E_API_URL}}/actuator/health" with method "GET"
    Then I expect status code is 200
    And  I expect "{{response.body.status}}" is "UP"

  Scenario Outline: Create and retrieve it's own team
    Given I set http header "Content-Type" with "application/x-www-form-urlencoded"
    And   I store "defaultUser" as "<username>" in context

    When  I request "{{env.E2E_KEYCLOAK_URL}}" with method "POST" with body:
    """
    grant_type=password&client_id={{env.E2E_KEYCLOAK_CLIENT_ID}}&username={{ctx.defaultUser}}&password=password
    """
    Then I expect status code is 200
    And  I set http header "Authorization" with "{{response.body.token_type}} {{response.body.access_token}}"
    And  I set http header "Content-Type" with "application/json"

    When I request "{{env.E2E_API_URL}}/teams/<team>" with method "GET"
    And I log "{{ response.body | json}}"
    Then I expect status code is <status>

    Examples:
      | username | team   | status |
      | team1    | TEAM_2 | 404    |
      | team2    | TEAM_1 | 404    |
