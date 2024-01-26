Feature: Delete job record

  Scenario: Call api to delete job by job id and returns success response
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When client sends delete request for url "/jobs/delete" for id "?jobId=1" of saved job record
    Then response status code is 200

  Scenario: Call api to delete job by job id and returns not found because job not found for requested id
    When client sends delete request for url "/jobs/delete" for id "?jobId=123" of saved job record
    Then response status code is 404

  Scenario: Call api to delete job but no job id passed in query parameters should return bad request
    When client sends delete request for url "/jobs/delete" for id "" of saved job record
    Then  response status code is 400

