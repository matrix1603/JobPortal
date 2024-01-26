package com.jobportal.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.ApplicationTestConfiguration;
import com.jobportal.JobPortalServiceApplication;
import controllers.job.jobControllersImpl.JobControllerImpl;
import lombok.extern.slf4j.Slf4j;
import models.response.ServiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import entity.job.Job;
import entity.job.Skills;
import services.job.jobServiceImpl.JobServiceImpl;
import services.job.models.CreateJobRequest;
import services.job.models.UpdateJobRequest;

import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({ApplicationTestConfiguration.class})
@WebMvcTest(controllers = JobControllerImpl.class)
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {JobPortalServiceApplication.class})
@AutoConfigureDataJpa
@Slf4j
public class JobControllerTests {

    @MockBean
    private JobServiceImpl jobService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private CreateJobRequest createJobRequest;

    private UpdateJobRequest updateJobRequest;

    private Job job;

    private List<Skills> skillList;

    private ServiceResponse<Job> jobServiceResponse = new ServiceResponse<>();

    private ServiceResponse<List<Job>> listJobServiceResponse = new ServiceResponse<>();

    private ServiceResponse<String> stringServiceResponse = new ServiceResponse<>();

    @Before
    public void setupData() {
        createJobRequest = CreateJobRequest.builder().jobTitle("TEST TITLE").company("TEST COMPANY").numberOfOpenings(22).country("TEST COUNTRY").language("TEST LANGUAGE").jobType("TEST JOB TYPE").schedule("TEST SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("TEST BENEFITS").jobCategory("TEST CATEGORY").jobMode("REMOTE").jobDescription("TEST DESCRIPTION").isRemote(false).location("TEST LOCATION").skills(List.of("SKILL-1")).build();

        updateJobRequest = UpdateJobRequest.builder().jobTitle("UPDATED TITLE").company("UPDATED COMPANY").numberOfOpenings(22).country("UPDATED COUNTRY").language("UPDATED LANGUAGE").jobType("UPDATED JOB TYPE").schedule("UPDATED SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("UPDATED BENEFITS").skills(List.of("SKILL-1")).jobDescription("UPDATED DESCRIPTION").jobCategory("TEST CATEGORY").jobMode("TEST MODE").isRemote(false).location("UPDATED LOCATION").build();
        skillList = List.of(Skills.builder().skill("SKILL-1").id(1).build());
        job = Job.builder().id(1).jobTitle(createJobRequest.getJobTitle()).company(createJobRequest.getCompany()).numberOfOpenings(createJobRequest.getNumberOfOpenings()).country(createJobRequest.getCountry()).language(createJobRequest.getLanguage()).jobType(createJobRequest.getJobType()).schedule(createJobRequest.getSchedule()).minPay(createJobRequest.getMinPay()).maxPay(createJobRequest.getMaxPay()).supplementalPay(createJobRequest.getSupplementalPay()).benefits(createJobRequest.getBenefits()).jobDescription(createJobRequest.getJobDescription()).isRemote(createJobRequest.isRemote()).location(createJobRequest.getLocation()).skills(skillList).jobMode("REMOTE").jobCategory("IT").createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();
        jobServiceResponse.setData(job);
        jobServiceResponse.setHttpStatus(HttpStatus.OK);
        listJobServiceResponse.setData(List.of(job));
        listJobServiceResponse.setHttpStatus(HttpStatus.OK);
    }

    @Test
    public void should_Return200_WhenCreateNewJobTest() throws Exception {
        when(jobService.createJob(createJobRequest)).thenReturn(jobServiceResponse);

        var result = this.mockMvc.perform(post("/jobs/createjob").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest))).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.data.jobTitle").value(job.getJobTitle())).andReturn();

        log.info("HTTP RESPONSE RECEIVED : \n");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return500_WhenInternalServerErrorOnCreateNewJobTest() throws Exception {
        jobServiceResponse.setData(null);
        jobServiceResponse.setErrorMessage("Internal Server error");
        jobServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        when(jobService.createJob(createJobRequest)).thenReturn(jobServiceResponse);

        var result = this.mockMvc.perform(post("/jobs/createjob").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest))).andExpect(status().isInternalServerError()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return400_WhenInvalidRequestForCreateNewJobTest() throws Exception {
        createJobRequest.setJobCategory(null);

        var result = this.mockMvc.perform(post("/jobs/createjob").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createJobRequest))).andExpect(status().isBadRequest()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        createJobRequest.setJobCategory("TEST CATEGORY");
        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return200_WhenSearchJobByTitleTest() throws Exception {
        when(jobService.searchByTitle("TEST TITLE", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/search").queryParam("title", "TEST TITLE").queryParam("pageNo", "0")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return200_WhenSearchJobByLocationTest() throws Exception {
        when(jobService.searchByLocation("TEST LOCATION", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("location", "TEST LOCATION").queryParam("pageNo", "0")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return404_WhenJobNotFoundForRequestedJobTitleTest() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        listJobServiceResponse.setErrorMessage("No job found for the requested title : TEST TITLE");
        when(jobService.searchByTitle("TEST TITLE", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/search").queryParam("title", "TEST TITLE").queryParam("pageNo", "0")).andExpect(status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return404_WhenJobNotFoundForRequestedJobLocationTest() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        listJobServiceResponse.setErrorMessage("No job found for the requested title : TEST TITLE");
        when(jobService.searchByLocation("TEST LOCATION", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("location", "TEST LOCATION").queryParam("pageNo", "0")).andExpect(status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return204_WhenNoMoreContentToShowForRequestedPageNumber() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NO_CONTENT);
        listJobServiceResponse.setErrorMessage("No more results to show for requested job title");
        when(jobService.searchByTitle("TEST TITLE", 1)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/search").queryParam("title", "TEST TITLE").queryParam("pageNo", "1")).andExpect(status().isNoContent());

    }

    @Test
    public void should_Return204_WhenNoMoreContentToShowForRequestedPageNumber_ForJobLocationTest() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NO_CONTENT);
        listJobServiceResponse.setErrorMessage("No more results to show for requested job location");
        when(jobService.searchByLocation("TEST LOCATION", 1)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("location", "TEST LOCATION").queryParam("pageNo", "1")).andExpect(status().isNoContent());

    }

    @Test
    public void should_Return500_WhenInternalServerErrorForSearchingJobByTitleTest() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        listJobServiceResponse.setErrorMessage("Error while searching a job for title : TEST TITLE");
        when(jobService.searchByTitle("TEST TITLE", 1)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/search").queryParam("title", "TEST TITLE").queryParam("pageNo", "1")).andExpect(status().isInternalServerError()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return500_WhenInternalServerErrorForSearchingJobByLocationTest() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        listJobServiceResponse.setErrorMessage("Error while searching a job for location : TEST LOCATION");

        when(jobService.searchByLocation("TEST LOCATION", 1)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("location", "TEST LOCATION").queryParam("pageNo", "1")).andExpect(status().isInternalServerError()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }


    @Test
    public void should_Return400_WhenNoTitlePassedInQuery_WhenSearchingJob() throws Exception {
        var result = this.mockMvc.perform(get("/jobs/search").queryParam("pageNo", "1")).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    public void should_Return400_WhenNoJobLocationPassedInQuery_WhenSearchingJob() throws Exception {
        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("pageNo", "1")).andExpect(status().isBadRequest()).andReturn();

    }

    @Test
    public void should_Return200_WhenNoPageNumberPassedAndJobExists() throws Exception {

        when(jobService.searchByTitle("TEST TITLE", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/search").queryParam("title", "TEST TITLE")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return200_WhenNoPageNumberPassedAndJobExistsSearchingWithLocation() throws Exception {

        when(jobService.searchByLocation("TEST LOCATION", 0)).thenReturn(listJobServiceResponse);

        var result = this.mockMvc.perform(get("/jobs/searchByLocation").queryParam("location", "TEST LOCATION")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return200_WhenDeleteJobByJobId() throws Exception {
        stringServiceResponse.setData("Job Deleted Successfully");
        stringServiceResponse.setHttpStatus(HttpStatus.OK);
        when(jobService.deleteJobById(anyInt())).thenReturn(stringServiceResponse);
        var result = this.mockMvc.perform(delete("/jobs/delete").queryParam("jobId", "1")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }


    @Test
    public void should_Return404_WhenJobNotFoundForRequestedJobId() throws Exception {
        stringServiceResponse.setErrorMessage("No job found for requested job id");
        stringServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        when(jobService.deleteJobById(anyInt())).thenReturn(stringServiceResponse);

        var result = this.mockMvc.perform(delete("/jobs/delete").queryParam("jobId", "12")).andExpect(status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return500_WhenErrorDeletingAJob() throws Exception {

        stringServiceResponse.setErrorMessage("Error while deleting a job for requested job id");
        stringServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        when(jobService.deleteJobById(anyInt())).thenReturn(stringServiceResponse);
        var result = this.mockMvc.perform(delete("/jobs/delete").queryParam("jobId", "1")).andExpect(status().isInternalServerError()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return400_WhenNoJobIdIsPassedInQueryParameter() throws Exception {
        var result = this.mockMvc.perform(delete("/jobs/delete")).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void should_Return200_WhenUpdateJobRecord() throws Exception {
        when(jobService.updateJobRecord(updateJobRequest, 1)).thenReturn(jobServiceResponse);

        var result = this.mockMvc.perform(put("/jobs/update/" + 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest))) // APPENDING THE JOB ID TO URL PATH
                .andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();


        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return500_WhenErrorOccurredDuringUpdateJobRecord() throws Exception {
        jobServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        jobServiceResponse.setErrorMessage("Internal server error while updating job record");
        jobServiceResponse.setData(null);
        when(jobService.updateJobRecord(updateJobRequest, 1)).thenReturn(jobServiceResponse);

        var result = this.mockMvc.perform(put("/jobs/update/" + 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest))) // APPENDING THE JOB ID TO URL PATH
                .andExpect(status().isInternalServerError()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();


        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());

    }

    @Test
    public void should_Return404_WhenNoJobFoundForRequestedJobIdToUpdate() throws Exception {

        jobServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        jobServiceResponse.setErrorMessage("No job found for requested job id");
        jobServiceResponse.setData(null);
        when(jobService.updateJobRecord(updateJobRequest, 1)).thenReturn(jobServiceResponse);

        var result = this.mockMvc.perform(put("/jobs/update/" + 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest))) // APPENDING THE JOB ID TO URL PATH
                .andExpect(status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return400_BadRequest_WhenUpdateJobRequestIsMalformed() throws Exception {
        updateJobRequest.setJobTitle(null);
        var result = this.mockMvc.perform(put("/jobs/update/" + 1).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateJobRequest))) // APPENDING THE JOB ID TO URL PATH
                .andExpect(status().isBadRequest()).andReturn();

        updateJobRequest.setJobTitle("TEST TITLE");

    }


    @Test
    public void should_Return200_WhenSearchJobByJobMode() throws Exception {
        when(jobService.searchJobByJobMode("REMOTE", 0)).thenReturn(listJobServiceResponse);
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("jobMode", "REMOTE").queryParam("pageNo", "0")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());

    }

    @Test
    public void should_Return404_WhenSearchJobByJobModeAndNoJobFound() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
        listJobServiceResponse.setErrorMessage("No job found for requested job mode");

        when(jobService.searchJobByJobMode("INVALID JOB MODE", 0)).thenReturn(listJobServiceResponse);
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("jobMode", "INVALID JOB MODE").queryParam("pageNo", "0")).andExpect(status().isNotFound()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return204_WhenNoMoreJobsToShowForRequestJobMode() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.NO_CONTENT);
        listJobServiceResponse.setErrorMessage("No more job results to show for requested job mode");
        when(jobService.searchJobByJobMode("REMOTE", 1)).thenReturn(listJobServiceResponse);
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("jobMode", "REMOTE").queryParam("pageNo", "1")).andExpect(status().isNoContent()).andReturn();

    }

    @Test
    public void should_Return500_WhenInternalServerErrorForSearchingJob() throws Exception {
        listJobServiceResponse.setData(null);
        listJobServiceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        listJobServiceResponse.setErrorMessage("No more job results to show for requested job mode");

        when(jobService.searchJobByJobMode("REMOTE", 0)).thenReturn(listJobServiceResponse);
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("jobMode", "REMOTE").queryParam("pageNo", "0")).andExpect(status().isInternalServerError()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isNotEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }

    @Test
    public void should_Return400_WhenNoJobModeIsPassedForSearchingJob() throws Exception {
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("pageNo", "0")).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void should_Return200_WhenNoPageNoIsPassedWhenSearchingJob() throws Exception {
        when(jobService.searchJobByJobMode("REMOTE", 0)).thenReturn(listJobServiceResponse);
        var result = this.mockMvc.perform(get("/jobs/searchByMode").queryParam("jobMode", "REMOTE")).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.data").isNotEmpty()).andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage").isEmpty()).andReturn();

        log.info("HTTP RESPONSE : \n ");
        log.info(result.getResponse().getContentAsString());
    }


}
