Feature: Create job record

  Scenario: Call api for create job and it successfully create job record
    When the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    And job title should be "TEST TITLE"

  Scenario: Call api for create job and it returns bad request
    Given client creates the bad request payload for create job
    When the client calls endpoint "/jobs/createjob"
    Then response status code is 400

  Scenario: Call api for create job and it returns Unauthorized
    Given client sends the invalid bearer token
    When the client calls endpoint "/jobs/createjob"
    Then response status code is 401

