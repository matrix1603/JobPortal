Feature: Register new user


  Scenario: Calls api to register user without passing bearer token then return unauthorized
    Given client sends the invalid bearer token
    When client calls api to register user
    Then response status code is 401
