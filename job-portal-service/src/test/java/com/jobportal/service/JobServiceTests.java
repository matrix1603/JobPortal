package com.jobportal.service;

import com.jobportal.ApplicationTestConfiguration;
import models.response.ServiceResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import producers.job.jobNotification.JobNotificationProducer;
import producers.job.models.JobCreatedNotification;
import repository.job.JobRepository;
import repository.job.SkillRepository;
import repository.jobApplication.JobApplicationRepository;
import entity.job.Job;
import entity.job.Skills;
import services.job.jobServiceImpl.JobServiceImpl;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@Import({ApplicationTestConfiguration.class})
public class JobServiceTests {

    @Mock
    private JobRepository jobRepository;

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    JobNotificationProducer jobNotificationProducer = mock(JobNotificationProducer.class);


    @Mock
    private SkillRepository skillRepository;


    @InjectMocks
    JobServiceImpl jobService;

    private CreateJobRequest createJobRequest;

    private UpdateJobRequest updateJobRequest;

    private List<Skills> skillList = new ArrayList<>();

    private Job job;

    private Job updatedJobEntity;
    private JobCreatedNotification jobCreatedNotification;

    private JobBySkillsRequest jobBySkillsRequest;

    @Before
    public void setupData() {
        createJobRequest = CreateJobRequest.builder().jobTitle("TEST TITLE").company("TEST COMPANY").numberOfOpenings(22).country("TEST COUNTRY").language("TEST LANGUAGE").jobType("TEST JOB TYPE").schedule("TEST SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("TEST BENEFITS").skills(List.of("SKILL-1")).jobDescription("TEST DESCRIPTION").isRemote(false).location("TEST LOCATION").build();

        updateJobRequest = UpdateJobRequest.builder().jobTitle("UPDATED TITLE").company("UPDATED COMPANY").numberOfOpenings(22).country("UPDATED COUNTRY").language("UPDATED LANGUAGE").jobType("UPDATED JOB TYPE").schedule("UPDATED SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("GREAT WORK ENVIRONMENT").benefits("UPDATED BENEFITS").skills(List.of("SKILL-1")).jobDescription("UPDATED DESCRIPTION").isRemote(false).location("UPDATED LOCATION").build();

        skillList = List.of(Skills.builder().skill("SKILL-1").id(1).build());
        updatedJobEntity = Job.builder().id(1).jobTitle(updateJobRequest.getJobTitle()).company(updateJobRequest.getCompany()).numberOfOpenings(updateJobRequest.getNumberOfOpenings()).country(updateJobRequest.getCountry()).language(updateJobRequest.getLanguage()).jobType(updateJobRequest.getJobType()).schedule(updateJobRequest.getSchedule()).minPay(updateJobRequest.getMinPay()).maxPay(updateJobRequest.getMaxPay()).supplementalPay(updateJobRequest.getSupplementalPay()).benefits(updateJobRequest.getBenefits()).jobDescription(updateJobRequest.getJobDescription()).isRemote(updateJobRequest.isRemote()).location(updateJobRequest.getLocation()).skills(skillList).createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();


        job = Job.builder().jobTitle(createJobRequest.getJobTitle()).company(createJobRequest.getCompany()).numberOfOpenings(createJobRequest.getNumberOfOpenings()).country(createJobRequest.getCountry()).language(createJobRequest.getLanguage()).jobType(createJobRequest.getJobType()).schedule(createJobRequest.getSchedule()).minPay(createJobRequest.getMinPay()).maxPay(createJobRequest.getMaxPay()).supplementalPay(createJobRequest.getSupplementalPay()).benefits(createJobRequest.getBenefits()).jobDescription(createJobRequest.getJobDescription()).isRemote(createJobRequest.isRemote()).location(createJobRequest.getLocation()).skills(skillList).jobMode("REMOTE").jobCategory("IT").createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();


        jobCreatedNotification = JobCreatedNotification.builder().jobId(1).jobTitle(job.getJobTitle()).message("JOB CREATED SUCCESSFULLY").build();

        jobBySkillsRequest = JobBySkillsRequest.builder().skills(List.of("SKILL-1")).build();

    }

    @Test
    public void should_CreateNewJobFromExistingSkillsTest() {

        when(skillRepository.findBySkillIn(createJobRequest.getSkills())).thenReturn(skillList);
        job.setId(1);
        when(jobRepository.save(any(Job.class))).thenReturn(job);

        ServiceResponse<Job> serviceResponse = jobService.createJob(createJobRequest);

        verify(jobNotificationProducer, times(1)).sendJobCreatedNotification(jobCreatedNotification);
        assertNotNull(serviceResponse);
        assertNotEquals(0, serviceResponse.getData().getId());
        assertEquals(serviceResponse.getData().getJobTitle(), "TEST TITLE");
    }

    @Test
    public void should_CreateJobFromNewSkillsTest() {
        Job newJobEntity = Job.builder().id(1).jobTitle(createJobRequest.getJobTitle()).company(createJobRequest.getCompany()).numberOfOpenings(createJobRequest.getNumberOfOpenings()).country(createJobRequest.getCountry()).language(createJobRequest.getLanguage()).jobType(createJobRequest.getJobType()).schedule(createJobRequest.getSchedule()).minPay(createJobRequest.getMinPay()).maxPay(createJobRequest.getMaxPay()).supplementalPay(createJobRequest.getSupplementalPay()).benefits(createJobRequest.getBenefits()).jobDescription(createJobRequest.getJobDescription()).isRemote(createJobRequest.isRemote()).location(createJobRequest.getLocation()).skills(List.of(Skills.builder().skill("NEW-TEST-SKILL-1").build())).createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();


        createJobRequest.setSkills(List.of("NEW-TEST-SKILL-1"));
        when(skillRepository.findBySkillIn(createJobRequest.getSkills())).thenReturn(new ArrayList<>());
        when(jobRepository.save(any(Job.class))).thenReturn(newJobEntity);

        ServiceResponse<Job> serviceResponse = jobService.createJob(createJobRequest);
        verify(jobNotificationProducer, times(1)).sendJobCreatedNotification(jobCreatedNotification);
        assertNotNull(serviceResponse);
        assertNotEquals(0, serviceResponse.getData().getId());
        assertEquals("TEST TITLE", serviceResponse.getData().getJobTitle());
        assertEquals(1, serviceResponse.getData().getId());
    }

    @Test
    public void should_SearchJobByJobTitleTest() {

        List<Job> jobList = new ArrayList<>();
        jobList.add(job);
        Page<Job> page = new PageImpl<>(jobList);

        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchByTitle(job.getJobTitle(), 0);

        assertNotNull(serviceResponse);
        assertNotEquals(0, serviceResponse.getData().size());
        assertEquals(job.getJobTitle(), serviceResponse.getData().get(0).getJobTitle());
    }

    @Test
    public void should_SearchJobByJobLocationTest() {
        List<Job> jobList = new ArrayList<>();
        jobList.add(job);
        Page<Job> page = new PageImpl<>(jobList);

        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchByLocation(job.getLocation(), 0);

        assertNotNull(serviceResponse);
        assertNotEquals(0, serviceResponse.getData().size());
        assertEquals(job.getJobTitle(), serviceResponse.getData().get(0).getJobTitle());
    }

    @Test
    public void should_DeleteJobByJobIdTest() {
        job.setId(1);
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        doNothing().when(jobApplicationRepository).deleteByJobId(job.getId());
        doNothing().when(jobRepository).deleteById(job.getId());

        ServiceResponse<String> serviceResponse = jobService.deleteJobById(job.getId());

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getData());
        assertEquals(HttpStatus.OK, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_ReturnNoJobFoundResponse_WhenNoJobIsFoundForRequestedJobIdTest() {
        job.setId(1);
        when(jobRepository.findById(job.getId())).thenReturn(Optional.empty());

        ServiceResponse<String> serviceResponse = jobService.deleteJobById(job.getId());

        assertNotNull(serviceResponse);
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_UpdateExistingJobRecord_WhenNoSkillsUpdatedTest() {
        job.setId(1);
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        when(skillRepository.findBySkillIn(updateJobRequest.getSkills())).thenReturn(skillList);
        when(jobRepository.save(any(Job.class))).thenReturn(updatedJobEntity);

        ServiceResponse<Job> updatedJobResponse = jobService.updateJobRecord(updateJobRequest, 1);

        assertNotNull(updatedJobResponse);
        assertEquals("UPDATED TITLE", updatedJobResponse.getData().getJobTitle());
        assertEquals(1, updatedJobResponse.getData().getId());
    }

    @Test
    public void should_UpdateExistingJobRecord_WhenSkillsAreUpdatedTest() {
        updatedJobEntity.setSkills(List.of(Skills.builder().skill("NEW-TEST-SKILL-1").build()));
        updateJobRequest.setSkills(List.of("NEW-TEST-SKILL-1"));
        when(jobRepository.findById(job.getId())).thenReturn(Optional.of(job));
        when(skillRepository.findBySkillIn(updateJobRequest.getSkills())).thenReturn(new ArrayList<>());

        when(jobRepository.save(any(Job.class))).thenReturn(updatedJobEntity);

        ServiceResponse<Job> serviceResponse = jobService.updateJobRecord(updateJobRequest, job.getId());
        assertNotNull(serviceResponse);
        assertNotEquals(0, serviceResponse.getData().getId());
        assertNotEquals(true, serviceResponse.getData().getSkills().isEmpty());
        assertEquals(1, serviceResponse.getData().getSkills().size());
        assertEquals("NEW-TEST-SKILL-1", serviceResponse.getData().getSkills().get(0).getSkill());

    }

    @Test
    public void should_SearchJobBySkillsTest() {
        when(jobRepository.findBySkillsSkillIn(anyList(), any(PageRequest.class))).thenReturn(List.of(job));

        ServiceResponse<List<Job>> jobsForSkills = jobService.searchJobBySkills(jobBySkillsRequest, 0);

        assertNotNull(jobsForSkills);
        assertNotEquals(true, jobsForSkills.getData().isEmpty());
        assertEquals("SKILL-1", jobsForSkills.getData().get(0).getSkills().get(0).getSkill());
    }


    @Test
    public void should_SearchJobByJobModeTest() {
        when(jobRepository.findByJobMode(anyString(), any(PageRequest.class))).thenReturn(List.of(job));

        ServiceResponse<List<Job>> jobForMode = jobService.searchJobByJobMode("REMOTE", 0);

        assertNotNull(jobForMode);
        assertNotEquals(true, jobForMode.getData().isEmpty());

    }

    @Test
    public void should_ReturnInternalServerErrorResponse_WhenJobNotCreatedTest() {
        when(skillRepository.findBySkillIn(createJobRequest.getSkills())).thenReturn(skillList);
        when(jobRepository.save(any(Job.class))).thenReturn(null);

        ServiceResponse<Job> serviceResponse = jobService.createJob(createJobRequest);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, serviceResponse.getHttpStatus());
    }


    @Test
    public void should_ReturnNotFoundResponse_WhenNoJobFoundForRequestedTitleOnFirstPageTest() {
        Page<Job> page = new PageImpl<>(new ArrayList<>());
        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);
        ServiceResponse<List<Job>> serviceResponse = jobService.searchByTitle("TEST TITLE", 0);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());

    }

    @Test
    public void should_ReturnNoContentResponse_WhenNoMoreResultsToShowForTitleTest() {
        Page<Job> page = new PageImpl<>(new ArrayList<>());
        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchByTitle("TEST TITLE", 1);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NO_CONTENT, serviceResponse.getHttpStatus());

    }


    @Test
    public void should_ReturnNotFoundResponse_WhenNoJobFoundForRequestedLocationOnFirstPageTest() {
        Page<Job> page = new PageImpl<>(new ArrayList<>());
        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchByLocation("TEST LOCATION", 0);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_ReturnNoContentResponse_WhenNoMoreResultsToShowOnAnyPageNoForJobLocationTest() {
        Page<Job> page = new PageImpl<>(new ArrayList<>());
        when(jobRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchByLocation("TEST LOCATION", 1);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NO_CONTENT, serviceResponse.getHttpStatus());

    }

    @Test
    public void should_ReturnNotFoundResponse_WhenNoJobFoundForJobIdTest() {
        when(jobRepository.findById(anyInt())).thenReturn(Optional.empty());

        ServiceResponse<Job> serviceResponse = jobService.updateJobRecord(updateJobRequest, 0);
        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_ReturnNotFoundResponse_WhenNoJobFoundForSkillSetTest() {
        List<Job> emptyJobList = new ArrayList<>();
        when(jobRepository.findBySkillsSkillIn(anyList(), any(PageRequest.class))).thenReturn(emptyJobList);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchJobBySkills(jobBySkillsRequest, 0);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_ReturnNoContentResponse_WhenNoMoreResultsToShowForSkillsTest() {
        List<Job> emptyJobList = new ArrayList<>();
        when(jobRepository.findBySkillsSkillIn(anyList(), any(PageRequest.class))).thenReturn(emptyJobList);

        ServiceResponse<List<Job>> serviceResponse = jobService.searchJobBySkills(jobBySkillsRequest, 1);


        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NO_CONTENT, serviceResponse.getHttpStatus());
    }

    @Test
    public void should_ReturnNotFoundResponse_WhenNoJobFoundForJobModeTest() {
        List<Job> emptyJobList = new ArrayList<>();
        when(jobRepository.findByJobMode(anyString(), any(PageRequest.class))).thenReturn(emptyJobList);
        ServiceResponse<List<Job>> serviceResponse = jobService.searchJobByJobMode("HYBRID", 0);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NOT_FOUND, serviceResponse.getHttpStatus());

    }

    @Test
    public void should_ReturnNoContentResponse_WhenNoMoreResultsToShowForJobModeTest() {
        List<Job> emptyJobList = new ArrayList<>();
        when(jobRepository.findByJobMode(anyString(), any(PageRequest.class))).thenReturn(emptyJobList);
        ServiceResponse<List<Job>> serviceResponse = jobService.searchJobByJobMode("HYBRID", 1);

        assertNotNull(serviceResponse);
        assertNotNull(serviceResponse.getErrorMessage());
        assertNull(serviceResponse.getData());
        assertEquals(HttpStatus.NO_CONTENT, serviceResponse.getHttpStatus());

    }


}
