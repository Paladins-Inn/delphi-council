Feature: Provide Service Version Information

  Scenario: Check Version
    Given the version service is running
    When the client calls the url /api/version
    Then the version should be @project.version@