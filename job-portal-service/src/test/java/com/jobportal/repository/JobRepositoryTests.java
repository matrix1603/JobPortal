package com.jobportal.repository;

import com.jobportal.ApplicationTestConfiguration;
import entity.job.Job;
import entity.job.Skills;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import repository.job.JobRepository;
import repository.job.specification.JobSpecification;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@DataJpaTest
@Import({ApplicationTestConfiguration.class})
public class JobRepositoryTests {

    @Autowired
    private JobRepository jobRepository;

    private Job job;

    private List<Skills> skills = new ArrayList();

    @Before
    public void setupData() {
        skills.add(Skills.builder().skill("TEST SKILL").build());
        job = Job.builder().jobTitle("TEST JOB TITLE").company("TEST COMPANY").numberOfOpenings(22).country("TEST COUNTRY").language("TEST LANGUAGE").jobType("TEST JOB TYPE").schedule("TEST SCHEDULE").minPay(50000).maxPay(70000).supplementalPay("Good Growth").benefits("Great work culture").jobDescription("A great job for skilled developers").isRemote(false).location("TEST LOCATION").skills(skills).createdDate(new Date(System.currentTimeMillis())).updatedDate(new Date(System.currentTimeMillis())).build();
    }

    @Test
    public void should_SaveJobEntityRecordInDbTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);
        assertNotEquals(0, savedJob.getId());
    }

    @Test
    public void should_SearchJobByTitleTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);

        var titleFilter = Specification.where(JobSpecification.titleLike("TEST JOB TITLE"));
        List<Job> jobs = jobRepository.findAll(titleFilter, PageRequest.of(0, 5, Sort.by("createdDate").descending())).stream().toList();

        assertNotNull(jobs);
        assertNotEquals(0, jobs.size());
        assertEquals("TEST JOB TITLE", jobs.get(0).getJobTitle());
    }


    @Test
    public void should_SearchJobByLocationTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);

        var locationFilter = Specification.where(JobSpecification.locationLike("TEST LOCATION"));
        List<Job> jobs = jobRepository.findAll(locationFilter, PageRequest.of(0, 5, Sort.by("createdDate").descending())).stream().toList();

        assertNotNull(jobs);
        assertNotEquals(0, jobs.size());
        assertEquals("TEST LOCATION", jobs.get(0).getLocation());
    }

    @Test
    public void should_ReturnEmptyJobListForLocation_WhenThereIsNoMoreJobToShowForRequestedPageNumberTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);
        var locationFilter = Specification.where(JobSpecification.locationLike("TEST LOCATION"));
        List<Job> jobs = jobRepository.findAll(locationFilter, PageRequest.of(1, 5, Sort.by("createdDate").descending())).stream().toList();
        assertNotNull(jobs);
        assertEquals(0, jobs.size());
        assertEquals(true, jobs.isEmpty());
    }


    @Test
    public void should_ReturnEmptyJobListForTitle_WhenThereIsNoMoreJobToShowForRequestedPageNumberTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);
        var locationFilter = Specification.where(JobSpecification.titleLike("TEST JOB TITLE"));
        List<Job> jobs = jobRepository.findAll(locationFilter, PageRequest.of(1, 5, Sort.by("createdDate").descending())).stream().toList();
        assertNotNull(jobs);
        assertEquals(0, jobs.size());
        assertEquals(true, jobs.isEmpty());
    }

    @Test
    public void should_FindJobByItsJobIdTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);
        var jobByIdOption = jobRepository.findById(savedJob.getId());
        assertEquals(false, jobByIdOption.isEmpty());
        assertDoesNotThrow(() -> jobByIdOption.get());
        assertNotNull(jobByIdOption.get());
        assertEquals(savedJob.getId(), jobByIdOption.get().getId());
    }


    @Test
    public void should_DeleteJobByItsIdTest() {
        Job savedJob = jobRepository.save(job);
        assertNotNull(savedJob);
        jobRepository.deleteById(savedJob.getId());
        var jobOption = jobRepository.findById(savedJob.getId());
        assertEquals(true, jobOption.isEmpty());
    }

    @Test
    public void should_ThrowInvalidDataAccessApiUsageException_WhenJobEntityIsNullTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            jobRepository.save(null);
        });
    }

    @Test
    public void should_ThrowInvalidDataAccessApiUsageException_WhenJobIdIsNullTest() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            jobRepository.findById(null);
        });
    }

}
