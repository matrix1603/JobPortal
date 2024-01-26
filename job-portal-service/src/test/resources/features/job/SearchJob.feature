Feature: Search job record

  # SEARCH OPERATIONS BY JOB TITLE

  Scenario: Call api to search job by job title and returns results
    Given the client calls endpoint "/jobs/createjob"
    When the client calls endpoint to get results "/jobs/search?title=" for query "TEST TITLE" and page "&pageNo=0"
    Then response status code is 200
    And search result list is not empty

  Scenario: Call api to search job by job title but no result found
    When the client calls endpoint to get results "/jobs/search?title=" for query "TEST TITLE 2" and page "&pageNo=0"
    Then response status code is 404
    And no data received

  Scenario: Call api to search job by title job exists but for requested page no more results to show
    Given the client calls endpoint "/jobs/createjob"
    When the client calls endpoint to get results "/jobs/search?title=" for query "TEST TITLE" and page "&pageNo=1"
    Then response status code is 204

  Scenario: Call api to search job but title not passed in query parameter return bad request
    When the client calls endpoint to get results "/jobs/search" for query "" and page ""
    Then response status code is 400



  # SEARCH OPERATIONS BY JOB LOCATION

  Scenario: Calls api to search job by location and returns results
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When the client calls endpoint to get results "/jobs/searchByLocation?location=" for query "TEST LOCATION" and page "&pageNo=0"
    Then response status code is 200
    And search result list is not empty

  Scenario: Call api to search job by job location but no result found
    When the client calls endpoint to get results "/jobs/searchByLocation?location=" for query "TEST Location 2" and page "&pageNo=0"
    Then response status code is 404
    And no data received

  Scenario: Call api to search job by job location job exists but for requested page no more results to show
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When the client calls endpoint to get results "/jobs/searchByLocation?location=" for query "TEST LOCATION" and page "&pageNo=1"
    Then response status code is 204

  Scenario: Call api to search job but no job location passed in query parameter return bad request
    When the client calls endpoint to get results "/jobs/searchByLocation" for query "" and page ""
    Then response status code is 400

  # SEARCH OPERATIONS BY SKILL SET

#  Scenario: Call api to search job by skills and returns success response
#    Given the client calls endpoint "/jobs/createjob"
#    Then response status code is 200
#    When the client calls endpoint to get results "/jobs/searchBySkills" for page "?pageNo=0"
#    Then response status code is 200
#    And search result list is not empty
#
#  Scenario: Call api to search job by skills but no job found for requested skill set and return not found
#    Given the client calls endpoint "/jobs/createjob"
#    Then response status code is 200
#    Given update the skills to search with "TEST-SKILL"
#    When the client calls endpoint to get results "/jobs/searchBySkills?data=" for page "&pageNo=0"
#    Then response status code is 404
#
#  Scenario: Call api to search job by skills but no more results to show for requested page no
#    Given the client calls endpoint "/jobs/createjob"
#    Then response status code is 200
#    Given update the skills to search with "SKILL-3"
#    When the client calls endpoint to get results "/jobs/searchBySkills?data=" for page "&pageNo=1"
#    Then response status code is 204
#
#  Scenario: Calls api to search job by skills but no skills are passed in request query parameter
#    Given the client calls endpoint "/jobs/createjob"
#    Then response status code is 200
#    When the client calls endpoint to get results "/jobs/searchBySkills" for query "" and page ""
#    Then response status code is 400


  # SEARCH OPERATIONS BY JOB MODE

  Scenario: Calls api to search job by job mode and returns results
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When the client calls endpoint to get results "/jobs/searchByMode?jobMode=" for query "REMOTE" and page "&pageNo=0"
    Then response status code is 200
    And search result list is not empty

  Scenario: Calls api to search job by job mode but no result found
    When the client calls endpoint to get results "/jobs/searchByMode?jobMode=" for query "INVALID-MODE" and page "&pageNo=0"
    Then response status code is 404
    And no data received

  Scenario: Calls api to search job by job mode job exists but no more results to show on requested page no
    Given the client calls endpoint "/jobs/createjob"
    Then response status code is 200
    When the client calls endpoint to get results "/jobs/searchByMode?jobMode=" for query "INVALID-MODE" and page "&pageNo=1"
    Then response status code is 204

  Scenario: Call api to search job but no job mode passed in query parameter return bad request
    When the client calls endpoint to get results "/jobs/searchByMode" for query "" and page ""
    Then response status code is 400







