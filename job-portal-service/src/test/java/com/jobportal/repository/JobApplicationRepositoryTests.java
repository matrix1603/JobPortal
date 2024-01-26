package com.jobportal.repository;

import com.jobportal.ApplicationTestConfiguration;
import entity.job.Job;
import entity.jobApplication.JobApplication;
import entity.job.Skills;
import entity.user.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;
import repository.job.JobRepository;
import repository.jobApplication.JobApplicationRepository;
import repository.user.UserRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
@Import({ApplicationTestConfiguration.class})
public class JobApplicationRepositoryTests {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    private JobApplication jobApplication;

    private List<Skills> skills = new ArrayList<>();
    private User user;

    private Job job;


    @Before
    public void setupData() {
        skills.add(Skills.builder().skill("TEST SKILL").build());
        job = Job.builder()
                .jobTitle("TEST JOB TITLE")
                .skills(skills)
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
                .createdDate(new Date(System.currentTimeMillis()))
                .updatedDate(new Date(System.currentTimeMillis()))
                .build();

        user = User.builder().emailId("test@gmail.com").phoneNumber("8888-8888-80").createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();
        User savedUser = userRepository.save(user);
        Job savedJob = jobRepository.save(job);
        jobApplication = JobApplication.builder().job(savedJob).user(savedUser).createdDate(new Date(System.currentTimeMillis())).build();
    }

    @Test
    public void should_SaveJobApplicationRecordInDb() {
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        assertNotNull(savedJobApplication);
        assertNotEquals(0, savedJobApplication.getId());
    }

    @Test
    public void should_ThrowInvalidDataAccessApiUsageException_WhenSavingNullEntity() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            jobApplicationRepository.save(null);
        });
    }

    @Test
    public void should_GetJobApplicationsOfUserByItsEmail() {
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        assertNotNull(savedJobApplication);
        List<JobApplication> jobApplications = jobApplicationRepository.findByUserEmailId("test@gmail.com", PageRequest.of(0, 5,
                Sort.by("createdDate").descending()));
        assertNotNull(jobApplications);
        assertNotEquals(true, jobApplications.isEmpty());
        assertNotEquals(0, jobApplications.size());
    }

    @Test
    public void should_GetEmptyList_WhenNoJobApplicationIsFoundForUserEmailId() {
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        assertNotNull(savedJobApplication);
        List<JobApplication> jobApplications = jobApplicationRepository.findByUserEmailId("wrongid@gmail.com", PageRequest.of(0, 5,
                Sort.by("createdDate").descending()));
        assertNotNull(jobApplications);
        assertEquals(true, jobApplications.isEmpty());
        assertEquals(0, jobApplications.size());
    }

    @Test
    public void should_ReturnEmptyList_WhenNoMoreRecordsToShowForPageNo() {
        JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
        assertNotNull(savedJobApplication);
        List<JobApplication> jobApplications = jobApplicationRepository.findByUserEmailId("test@gmail.com", PageRequest.of(1, 5,
                Sort.by("createdDate").descending()));

        assertNotNull(jobApplications);
        assertEquals(true, jobApplications.isEmpty());
        assertEquals(0, jobApplications.size());
    }

}
