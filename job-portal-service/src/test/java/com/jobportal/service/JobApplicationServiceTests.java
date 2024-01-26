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
import org.springframework.data.domain.PageRequest;
import producers.job.jobNotification.JobNotificationProducer;
import producers.job.models.JobApplicationCreatedNotification;
import repository.job.JobRepository;
import repository.jobApplication.JobApplicationRepository;
import repository.user.UserRepository;
import entity.job.Job;
import entity.job.Skills;
import entity.jobApplication.JobApplication;
import services.jobApplication.jobApplicationServiceImpl.JobApplicationServiceImpl;
import services.jobApplication.models.CreateJobApplicationRequest;
import services.jobApplication.models.UserAppliedJobsResponse;
import entity.user.User;
import services.user.mapper.UserMapper;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@Import({ApplicationTestConfiguration.class})
public class JobApplicationServiceTests {

    @Mock
    private JobApplicationRepository jobApplicationRepository;

    @Mock
    private JobNotificationProducer jobNotificationProducer;

    @Mock
    private JobRepository jobRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private JobApplicationServiceImpl jobApplicationService;

    private CreateJobApplicationRequest createJobApplicationRequest;

    private Job job;
    private List<Skills> skillList = new ArrayList<>();

    private User user;

    private JobApplication jobApplication;

    private JobApplicationCreatedNotification jobApplicationCreatedNotification;

    @Before
    public void setupData() {
        createJobApplicationRequest = CreateJobApplicationRequest.builder().jobId(1).userEmail("test@gmail.com").build();
        skillList = List.of(Skills.builder().skill("SKILL-1").id(1).build());
        job = Job
                .builder()
                .id(1)
                .jobTitle("TEST JOB TITLE")
                .company("TEST COMPANY")
                .numberOfOpenings(22)
                .country("TEST COUNTRY")
                .language("TEST LANGUAGE")
                .jobType("TEST JOB TYPE")
                .schedule("TEST SCHEDULE")
                .minPay(50000)
                .maxPay(70000)
                .supplementalPay("Good Growth")
                .benefits("Great work culture")
                .jobDescription("A great job for skilled developers")
                .isRemote(false)
                .location("TEST LOCATION")
                .skills(skillList)
                .createdDate(new Date(System.currentTimeMillis()))
                .updatedDate(new Date(System.currentTimeMillis()))
                .build();

        user = User.builder().id(1).emailId("test@gmail.com").phoneNumber("8888-8888-00").createdDate(new Date(System.currentTimeMillis())).updatedDate(null).build();


        jobApplication = JobApplication.builder()
                .id(1)
                .job(job)
                .user(user)
                .createdDate(new Date(System.currentTimeMillis()))
                .build();

        jobApplicationCreatedNotification = JobApplicationCreatedNotification.builder()
                .jobId(jobApplication.getJob().getId())
                .jobTitle(jobApplication.getJob().getJobTitle())
                .companyName(jobApplication.getJob().getCompany())
                .userEmail(jobApplication.getUser().getEmailId())
                .message(String.format("Job application record created for Job Post : %s by email id : %s",
                        jobApplication.getJob().getJobTitle(),
                        jobApplication.getUser().getEmailId()))
                .build();
    }

    @Test
    public void should_CreateJobApplicationTest() {
        when(jobRepository.findById(createJobApplicationRequest.getJobId())).thenReturn(Optional.of(job));
        when(userRepository.findByEmailId(createJobApplicationRequest.getUserEmail())).thenReturn(user);
        when(jobApplicationRepository.save(any(JobApplication.class))).thenReturn(jobApplication);
        doNothing().when(jobNotificationProducer).sendJobApplicationCreatedNotification(jobApplicationCreatedNotification);

        ServiceResponse<JobApplication> jobApplicationServiceResponse = jobApplicationService.submitJobApplication(createJobApplicationRequest);

        assertNotNull(jobApplicationServiceResponse);
        assertNotEquals(0, jobApplicationServiceResponse.getData().getId());
    }

    @Test
    public void should_GetUserAppliedJobs() {
        when(userMapper.extractEmailFromToken(anyString())).thenReturn("test@gmail.com");
        when(jobApplicationRepository.findByUserEmailId(anyString(), any(PageRequest.class))).thenReturn(List.of(jobApplication));

        ServiceResponse<List<UserAppliedJobsResponse>> userAppliedJobResponse = jobApplicationService.getUserAppliedJobs(anyString(), 0);
        assertNotNull(userAppliedJobResponse);
        assertNotEquals(true, userAppliedJobResponse.getData().isEmpty());
        assertEquals(1, userAppliedJobResponse.getData().size());
        assertEquals("TEST JOB TITLE", userAppliedJobResponse.getData().get(0).getJob().getJobTitle());
    }

}
