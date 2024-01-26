package services.job.jobServiceImpl;

import entity.job.Job;
import entity.job.Skills;
import exception.job.JobNotFoundException;
import lombok.extern.slf4j.Slf4j;
import models.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import producers.job.jobNotification.JobNotificationProducer;
import producers.job.mapper.JobNotificationMapper;
import producers.job.models.JobCreatedNotification;
import repository.job.JobRepository;
import repository.job.SkillRepository;
import repository.job.specification.JobSpecification;
import repository.jobApplication.JobApplicationRepository;
import services.job.extension.JobSkillsExtension;
import services.job.jobService.JobService;
import services.job.mapper.JobMapper;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static services.job.mapper.JobMapper.updateExistingJobRecord;


@Slf4j
@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobNotificationProducer jobNotificationProducer;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Override
    public ServiceResponse<Job> createJob(CreateJobRequest createJobRequest) {
        log.info("Request received for create job");
        ServiceResponse<Job> serviceResponse = new ServiceResponse<>();
        try {
            List<Skills> skillsFromDb = skillRepository.findBySkillIn(createJobRequest.getSkills());
            List<Skills> jobSkills = JobSkillsExtension.getSkillEntityList(createJobRequest.getSkills(), skillsFromDb);
            Job jobEntity = JobMapper.buildJobEntity(createJobRequest, jobSkills);

            Job savedJob = jobRepository.save(jobEntity);

            if (Objects.isNull(savedJob)) {
                serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                serviceResponse.setErrorMessage("Internal server error while saving job record");
                return serviceResponse;
            }

            log.info("New job created successfully ! ");
            JobCreatedNotification jobCreatedNotification = JobNotificationMapper.getJobCreatedNotification(savedJob);
            jobNotificationProducer.sendJobCreatedNotification(jobCreatedNotification);
            serviceResponse.setData(savedJob);
            serviceResponse.setHttpStatus(HttpStatus.OK);

        } catch (RuntimeException ex) {
            log.info("Error while creating new job, Error message is : " + ex.getMessage());

            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            serviceResponse.setErrorMessage(String.format("Error creating job %s, because of %s", createJobRequest.getJobTitle(), ex.getMessage()));
        }
        return serviceResponse;
    }

    @Override
    @Cacheable(value = "jobCache", key = "{#title,#pageNo}", unless = "#result==null")
    public ServiceResponse<List<Job>> searchByTitle(String title, int pageNo) {
        log.info("Request received for search job by title");

        ServiceResponse<List<Job>> serviceResponse = new ServiceResponse<>();
        try {
            var titleFilter = Specification.where(JobSpecification.titleLike(title));
            var jobs = jobRepository.findAll(titleFilter, PageRequest.of(pageNo, 5,
                    Sort.by("createdDate").descending())).stream().toList();

            if ((Objects.isNull(jobs) || jobs.isEmpty())) {
                serviceResponse.setHttpStatus(pageNo > 0 ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage(pageNo > 0 ?
                        "No more results to show for requested job title" :
                        "No job found for requested job title");
                return serviceResponse;
            }

            serviceResponse.setData(jobs);
            serviceResponse.setHttpStatus(HttpStatus.OK);
            log.info("Fetched the jobs successfully based on title");

        } catch (RuntimeException ex) {
            log.info(String.format("Error while searching job, Error message is : %s", ex.getMessage()));
            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            serviceResponse.setErrorMessage(String.format("Error searching job for title %s, Error Message :  %s", title, ex.getMessage()));

        }
        return serviceResponse;
    }

    @Override
    @Cacheable(value = "jobCache", key = "{#location,#pageNo}", unless = "#result==null")
    public ServiceResponse<List<Job>> searchByLocation(String location, int pageNo) {
        log.info("Request received to search job by location");
        ServiceResponse<List<Job>> serviceResponse = new ServiceResponse<>();
        try {
            var locationFilter = Specification.where(JobSpecification.locationLike(location));
            var jobs = jobRepository.findAll(locationFilter,
                    PageRequest.of(pageNo, 5,
                            Sort.by("createdDate").descending())).stream().toList();

            if ((Objects.isNull(jobs) || jobs.isEmpty())) {
                serviceResponse.setHttpStatus(pageNo > 0 ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage(pageNo > 0 ?
                        "No more results to show for requested job location" :
                        "No job found for requested job location");
                return serviceResponse;
            }

            serviceResponse.setData(jobs);
            serviceResponse.setHttpStatus(HttpStatus.OK);
            log.info("Fetched the jobs successfully based on requested location");

        } catch (RuntimeException ex) {
            log.info(String.format("Error while searching job, Error message is : %s", ex.getMessage()));
            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            serviceResponse.setErrorMessage(String.format("Error searching job for location %s, Error Message :  %s", location, ex.getMessage()));

        }
        return serviceResponse;
    }


    @Override
    @CacheEvict(cacheNames = "jobCache", allEntries = true) // CLEARING CACHE WHEN ANY JOB RECORD IS DELETED
    public ServiceResponse<String> deleteJobById(int jobId) {
        log.info("Request received to delete job by Job Id");
        ServiceResponse<String> serviceResponse = new ServiceResponse<>();

        try {
            if (jobRepository.findById(jobId).isPresent()) {
                jobApplicationRepository.deleteByJobId(jobId); // DELETING THE JOB APPLICATION RECORD FOR JOB TO BE DELETED
                jobRepository.deleteById(jobId);
                log.info(String.format("Successfully deleted the job for job ID: %d ", jobId));
                serviceResponse.setHttpStatus(HttpStatus.OK);
                serviceResponse.setData("Job record deleted successfully");

            } else {
                log.info(String.format("No job with job ID : %d found in DB", jobId));
                serviceResponse.setHttpStatus(HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage("Job not found for requested job id");
            }

        } catch (RuntimeException ex) {
            log.info(String.format("Error while deleting job by job id, Error message is : %s", ex.getMessage()));
            serviceResponse.setErrorMessage(String.format("Error while deleting job for job id %d, Error message : %s", jobId, ex.getMessage()));
            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return serviceResponse;
    }

    @Override
    @CacheEvict(cacheNames = "jobCache", allEntries = true) // CLEARING CACHE WHEN ANY JOB RECORD IS UPDATED
    public ServiceResponse<Job> updateJobRecord(UpdateJobRequest updateJobRequest, int jobId) {
        log.info("Request received for update job record");
        ServiceResponse<Job> serviceResponse = new ServiceResponse<>();
        try {
            var existingJob = jobRepository.findById(jobId).orElseThrow(() -> new JobNotFoundException("No job found for requested job id ", HttpStatus.NOT_FOUND.value()));

            List<Skills> skillsFromDb = skillRepository.findBySkillIn(updateJobRequest.getSkills());
            List<Skills> jobSkills = JobSkillsExtension.getSkillEntityList(updateJobRequest.getSkills(), skillsFromDb);
            var updatedJobRecord = updateExistingJobRecord(updateJobRequest, existingJob, jobSkills);
            log.info("Successfully updated the existing job record ! ");
            var record = jobRepository.save(updatedJobRecord);
            serviceResponse.setData(record);
            serviceResponse.setHttpStatus(HttpStatus.OK);

        } catch (RuntimeException ex) {
            log.info(String.format("Error while updating job record, Error message is : %s", ex.getMessage()));
            serviceResponse.setHttpStatus(ex instanceof JobNotFoundException ?
                    HttpStatus.NOT_FOUND :
                    HttpStatus.INTERNAL_SERVER_ERROR);

            serviceResponse.setErrorMessage(ex.getMessage());
        }

        return serviceResponse;
    }

    @Override
    @Cacheable(value = "jobCache", key = "{#searchBySkillsRequest,#pageNo}", unless = "#result == null")
    public ServiceResponse<List<Job>> searchJobBySkills(JobBySkillsRequest searchBySkillsRequest, int pageNo) {
        log.info("Request received to search job by skills");
        ServiceResponse<List<Job>> serviceResponse = new ServiceResponse<>();
        try {
            var jobs = jobRepository.findBySkillsSkillIn(searchBySkillsRequest.getSkills().stream().
                            map(String::toLowerCase).collect(Collectors.toList()),
                    PageRequest.of(pageNo, 5,
                            Sort.by("createdDate").descending()));

            if ((Objects.isNull(jobs) || jobs.isEmpty())) {
                serviceResponse.setHttpStatus(pageNo > 0 ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage(pageNo > 0 ?
                        "No more results to show for requested job skill set" :
                        "No job found for requested job skill set");
                return serviceResponse;
            }
            serviceResponse.setData(jobs);
            serviceResponse.setHttpStatus(HttpStatus.OK);

        } catch (RuntimeException ex) {
            log.info("Error while searching job by skill set");
            serviceResponse.setErrorMessage(ex.getMessage());
            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return serviceResponse;
    }

    @Override
    @Cacheable(value = "jobCache", key = "{#jobMode,#pageNo}", unless = "#result==null")
    public ServiceResponse<List<Job>> searchJobByJobMode(String jobMode, int pageNo) {
        log.info("Request received to search job for job mode: {}", jobMode);
        ServiceResponse<List<Job>> serviceResponse = new ServiceResponse<>();
        try {
            var jobs = jobRepository.findByJobMode(jobMode,
                    PageRequest.of(pageNo, 5, Sort.by("createdDate")
                            .descending()));

            if ((Objects.isNull(jobs) || jobs.isEmpty())) {
                serviceResponse.setHttpStatus(pageNo > 0 ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage(pageNo > 0 ?
                        "No more results to show for requested job skill set" :
                        "No job found for requested job skill set");
                return serviceResponse;
            }
            serviceResponse.setData(jobs);
            serviceResponse.setHttpStatus(HttpStatus.OK);

        } catch (RuntimeException ex) {
            log.info("Error while searching job by requested job mode");
            serviceResponse.setErrorMessage(ex.getMessage());
            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return serviceResponse;
    }


}
