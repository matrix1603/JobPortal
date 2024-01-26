Feature: Update job record

  Scenario: Calls api to update the existing job record and should return success
    When the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When the client calls endpoint "/jobs/update" for update with newly create job id
    Then response status code is 200

  Scenario: Calls api to update the job record and should return not found because job not found for job id
    When the client calls endpoint "/jobs/update" for update with job id "?jobId=123"
    Then response status code is 404

  Scenario: Calls api to update the job record and should return bad request because no job id is passed in request parameter
    When the client calls endpoint "/jobs/update" for update with job id ""
    Then response status code is 400

  Scenario: Calls api to update the job record with invalid request payload and return bad request
    Given client creates the bad request payload for update job
    When the client calls endpoint "/jobs/update" for update with job id "?jobId=1"
    Then response status code is 400