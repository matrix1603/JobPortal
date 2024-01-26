Feature: Get user applied jobs
  # /application/applied GET WITH pageNo

  Scenario: Call api to get user applied jobs
    When the client calls endpoint "/application/applied" to get user applied jobs for page "?pageNo=0"
    Then response status code is 200





