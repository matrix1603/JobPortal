package com.jobportal.cucumberglue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.cucumberglue.utils.CucumberTestUtils;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import models.response.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;
import services.jobApplication.models.CreateJobApplicationRequest;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
public class CucumberGlueCode {

    @LocalServerPort
    String port;

    @Autowired
    private CucumberTestUtils testUtils;


    private RestTemplate restTemplate = new RestTemplate();

    private CreateJobRequest createJobRequest;

    private UpdateJobRequest updateJobRequest;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ApplicationResponse recentResponse;

    private JobBySkillsRequest jobBySkillsRequest;

    HttpEntity<CreateJobRequest> httpEntityCreateJobRequest;
    HttpEntity<CreateJobApplicationRequest> httpEntityJobApplication;

    HttpEntity<JobBySkillsRequest> httpEntityJobBySkillsRequest;
    HttpHeaders headers;

    HttpHeaders jobBySkillsHttpHeaders;

    private String bearerToken = "";

    private CreateJobApplicationRequest createJobApplicationRequest;


    @Before
    public void setupData() throws JsonProcessingException {
        bearerToken = testUtils.getBearerToken();

        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", bearerToken);

        jobBySkillsHttpHeaders = new HttpHeaders();

        jobBySkillsHttpHeaders.setContentType(MediaType.APPLICATION_JSON);
        jobBySkillsHttpHeaders.add("Authorization", bearerToken);
        jobBySkillsHttpHeaders.setContentLength(24);


        createJobRequest = CreateJobRequest.builder().jobTitle("TEST TITLE").company("TEST COMPANY").numberOfOpenings(22).country("TEST COUNTRY").language("TEST LANGUAGE").jobType("TEST JOB TYPE").schedule("TEST SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("TEST BENEFITS").skills(List.of("SKILL-1")).jobDescription("TEST DESCRIPTION").isRemote(false).jobMode("REMOTE").jobCategory("TEST CATEGORY").location("TEST LOCATION").build();
        updateJobRequest = UpdateJobRequest.builder().jobTitle("UPDATED JOB TITLE").company("TEST COMPANY").numberOfOpenings(22).country("TEST COUNTRY").language("TEST LANGUAGE").jobType("TEST JOB TYPE").schedule("TEST SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("TEST BENEFITS").skills(List.of("SKILL-1")).jobDescription("TEST DESCRIPTION").isRemote(false).jobMode("REMOTE").jobCategory("TEST CATEGORY").location("TEST LOCATION").build();
        jobBySkillsRequest = JobBySkillsRequest.builder().skills(createJobRequest.getSkills()).build();
        createJobApplicationRequest = CreateJobApplicationRequest.builder().jobId(1).userEmail("rsstyle9794@gmail.com").build();


        httpEntityCreateJobRequest = new HttpEntity<>(createJobRequest, headers);
        httpEntityJobApplication = new HttpEntity<>(createJobApplicationRequest, headers);
        httpEntityJobBySkillsRequest = new HttpEntity<>(jobBySkillsRequest, jobBySkillsHttpHeaders);

    }


    @When("the client calls endpoint {string}")
    public void whenClientCalls(String url) {
        try {
            recentResponse = restTemplate.postForObject("http://localhost:" + port + url, httpEntityCreateJobRequest, ApplicationResponse.class);
        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = httpClientErrorException.getStatusCode().value() == 401 ?
                    new ApplicationResponse("UNAUTHORIZED", 401)
                    : httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
        }
    }

    @When("the client calls endpoint {string} for update with newly create job id")
    public void whenClientCallsUpdateApi(String url) throws URISyntaxException {
        try {
            var recentJobId = ((LinkedHashMap) this.recentResponse.getData()).get("id");
            var response = restTemplate.exchange(RequestEntity.put(new URI("http://localhost:" + port + url + "?jobId=" + recentJobId)).headers(headers).body(updateJobRequest), ApplicationResponse.class);
            recentResponse = response.getBody();

            if (response.getStatusCode().value() == 204)
                this.recentResponse = new ApplicationResponse("", 204);

        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = new ApplicationResponse("", httpClientErrorException.getStatusCode().value());
        }
    }

    @When("the client calls endpoint {string} for update with job id {string}")
    public void whenClientCallsUpdateApiWithJobId(String url, String jobId) throws URISyntaxException {
        try {
            var response = restTemplate.exchange(RequestEntity.put(new URI("http://localhost:" + port + url + jobId)).headers(headers).body(updateJobRequest), ApplicationResponse.class);
            recentResponse = response.getBody();

            if (response.getStatusCode().value() == 204)
                this.recentResponse = new ApplicationResponse("", 204);

        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = new ApplicationResponse("", httpClientErrorException.getStatusCode().value());
        }
    }

    @When("the client calls endpoint to get results {string} for query {string} and page {string}")
    public void whenClientCallsForGetRequest(String url, String query, String page) throws URISyntaxException {
        try {
            var response = restTemplate.exchange(RequestEntity.get(new URI("http://localhost:" + port + url + URLEncoder.encode(query, StandardCharsets.UTF_8) + page)).headers(headers).build(), ApplicationResponse.class);
            recentResponse = response.getBody();

            if (response.getStatusCode().value() == 204)
                this.recentResponse = new ApplicationResponse("", 204);

        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
            recentResponse.setStatusCode(httpClientErrorException.getStatusCode().value());
        }
    }

    @When("the client calls endpoint to get results {string} for page {string}")
    public void whenClientCallsForSearchBySkills(String url, String page) throws URISyntaxException {
        try {
            var response = restTemplate.exchange("http://localhost:" + port + url + page, HttpMethod.GET, httpEntityJobBySkillsRequest, ApplicationResponse.class);
            recentResponse = response.getBody();
            if (response.getStatusCode().value() == 204)
                this.recentResponse = new ApplicationResponse("", 204);

        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
            recentResponse.setStatusCode(httpClientErrorException.getStatusCode().value());
        }
    }

    @When("the client calls endpoint {string} to get user applied jobs for page {string}")
    public void whenClientCallsForGettingAppliedJobs(String url, String pageNo) {
        try {
            var response = restTemplate.exchange(RequestEntity.get("http://localhost:" + port + url + pageNo).headers(headers).build(), ApplicationResponse.class);
            recentResponse = response.getBody();

            if (response.getStatusCode().value() == 204)
                this.recentResponse = new ApplicationResponse("", 204);

        } catch (HttpClientErrorException httpClientErrorException) {

            recentResponse = httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
            recentResponse.setStatusCode(httpClientErrorException.getStatusCode().value());

        }
    }

    @When("client calls api to register user")
    public void whenClientRegistersUser() {
        try {
            var response = restTemplate.exchange(RequestEntity.get("http://localhost:" + port + "/user/register").headers(headers).build(), ApplicationResponse.class);
            recentResponse = response.getBody();
        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = new ApplicationResponse("", httpClientErrorException.getStatusCode().value());
        }
    }

    @When("client sends delete request for url {string} for id {string} of saved job record")
    public void whenDeleteJobRecord(String url, String jobId) throws URISyntaxException {

        try {
            var response = restTemplate.exchange(RequestEntity.delete(new URI("http://localhost:" + port + url + jobId)).headers(headers).build(), ApplicationResponse.class);
            recentResponse = response.getBody();
            if (response.getStatusCode().value() == 204)
                recentResponse = new ApplicationResponse("", 204);
            System.out.println("TESTING IT ");

        } catch (HttpClientErrorException httpClientErrorException) {
            recentResponse = httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
            recentResponse.setStatusCode(httpClientErrorException.getStatusCode().value());
        }


    }


    @Given("apply for the latest job created")
    public void setLatestJobIdInJobApplicationRequest() {
        var recentJobId = ((LinkedHashMap) this.recentResponse.getData()).get("id");
        var response = httpEntityJobApplication.getBody();
        response.setJobId((Integer) recentJobId);
        this.httpEntityJobApplication = new HttpEntity<>(response, headers);
    }


    @Given("update the skills to search with {string}")
    public void updateTheSkillsToSearch(String skill) {
        jobBySkillsRequest.setSkills(List.of(skill));
    }

    @When("the client calls endpoint {string} for create job application")
    public void whenClientCreatesJobApplication(String url) {
        try {
            recentResponse = restTemplate.postForObject("http://localhost:" + port + url, httpEntityJobApplication, ApplicationResponse.class);
        } catch (HttpClientErrorException httpClientErrorException) {

            recentResponse = httpClientErrorException.getStatusCode().value() == 401 ?
                    new ApplicationResponse("UNAUTHORIZED", 401)
                    : httpClientErrorException.getResponseBodyAs(ApplicationResponse.class);
        }
    }

    @Given("the job id in request is {int}")
    public void setUserJobId(int jobId) {
        var updatedJobApplicationRequest = httpEntityJobApplication.getBody();
        updatedJobApplicationRequest.setJobId(jobId);
        httpEntityJobApplication = new HttpEntity<>(updatedJobApplicationRequest, headers);
    }


    @Given("the user email in request is {string}")
    public void setUserEmailId(String email) {
        var updatedJobApplicationRequest = httpEntityJobApplication.getBody();
        updatedJobApplicationRequest.setUserEmail(email);
        httpEntityJobApplication = new HttpEntity<>(updatedJobApplicationRequest, headers);
    }

    @Given("user sends invalid request payload for create job application")
    public void sendInvalidRequestPayload() {
        var updatedJobApplicationRequest = httpEntityJobApplication.getBody();
        updatedJobApplicationRequest.setUserEmail(null);
        httpEntityJobApplication = new HttpEntity<>(updatedJobApplicationRequest, headers);
    }

    @Then("response status code is {int}")
    public void thenStatusCodee(int expected) {
        assertNotNull(recentResponse);
        assertEquals(expected, recentResponse.getStatusCode());
    }

    @And("job title should be {string}")
    public void savedJobTitleShouldBe(String title) {
        assertEquals(title, ((LinkedHashMap) recentResponse.getData()).get("jobTitle"));
    }

    @Given("client creates the bad request payload for create job")
    public void clientSendsBadRequest() {
        var createJobBadRequest = httpEntityCreateJobRequest.getBody();
        createJobBadRequest.setJobTitle(null); // CREATING THE BAD REQUEST
        this.httpEntityCreateJobRequest = new HttpEntity<>(createJobBadRequest, headers);
    }

    @Given("client creates the bad request payload for update job")
    public void clientSendsBadRequestForUpdateJob() {
        updateJobRequest.setJobTitle(null); // CREATING THE BAD REQUEST
    }

    @Given("client sends the invalid bearer token")
    public void clientSendsInvalidBearerToken() {
        this.headers.remove("Authorization");
        this.headers.add("Authorization", "");
        this.httpEntityCreateJobRequest = new HttpEntity<>(createJobRequest, headers);
    }

    @And("search result list is not empty")
    public void searchResultListIsNotEmpty() {
        var listResponse = (List) this.recentResponse.getData();
        assertNotNull(listResponse);
        assertNotEquals(0, listResponse.size());
    }


    @And("no data received")
    public void noDataReceivedVerification() {
        assertNull(this.recentResponse.getData());
    }

}
