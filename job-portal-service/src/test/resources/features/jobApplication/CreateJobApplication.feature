Feature: Create job application

  Scenario: Call api to create job application and return successful response
    Given client calls api to register user
    Then response status code is 200
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    Given apply for the latest job created
    When the client calls endpoint "/application/create" for create job application
    Then response status code is 200

  Scenario: Call api to create job application but no job found for requested job id and return bad request
    Given the job id in request is 123
    When the client calls endpoint "/application/create" for create job application
    Then response status code is 400

  Scenario: Call api to create job application but user is not registered for requested user email returns bad request
    Given the user email in request is "test2@gmail.com"
    When the client calls endpoint "/application/create" for create job application
    Then response status code is 400

  Scenario: Call api to create job application with invalid request payload and returns bad request
    Given user sends invalid request payload for create job application
    When the client calls endpoint "/application/create" for create job application
    Then response status code is 400

