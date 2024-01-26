package services.jobApplication.jobApplicationServiceImpl;

import entity.job.Job;
import entity.jobApplication.JobApplication;
import producers.job.mapper.JobNotificationMapper;
import services.jobApplication.mapper.JobApplicationMapper;
import entity.user.User;
import lombok.extern.slf4j.Slf4j;
import services.jobApplication.models.CreateJobApplicationRequest;
import services.jobApplication.models.UserAppliedJobsResponse;
import producers.job.models.JobApplicationCreatedNotification;
import models.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import producers.job.jobNotification.JobNotificationProducer;
import repository.job.JobRepository;
import repository.jobApplication.JobApplicationRepository;
import repository.user.UserRepository;
import services.jobApplication.jobApplicationService.JobApplicationService;
import services.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class JobApplicationServiceImpl implements JobApplicationService {
    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private JobNotificationProducer jobNotificationProducer;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;


    @Override
    public ServiceResponse<JobApplication> submitJobApplication(CreateJobApplicationRequest createJobApplicationRequest) {

        ServiceResponse<JobApplication> serviceResponse = new ServiceResponse<>();
        try {
            Optional<Job> job = jobRepository.findById(createJobApplicationRequest.getJobId());

            if (job.isEmpty()) {
                serviceResponse.setErrorMessage("No job found for requested job id");
                serviceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
                return serviceResponse;
            }


            User user = userRepository.findByEmailId(createJobApplicationRequest.getUserEmail());
            if (Objects.isNull(user)) {
                serviceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
                serviceResponse.setErrorMessage(String.format("No user found for requested email id %s", createJobApplicationRequest.getUserEmail()));
                return serviceResponse;
            }

            JobApplication jobApplication = services.jobApplication.mapper.JobApplicationMapper.buildJobApplicationEntity(job.get(), user);
            JobApplication savedJobApplication = jobApplicationRepository.save(jobApplication);
            if (Objects.isNull(savedJobApplication)) {
                serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                serviceResponse.setErrorMessage(String.format("Error while saving job application record"));
                return serviceResponse;
            }

            log.info("Job application saved successfully");
            JobApplicationCreatedNotification jobApplicationCreatedNotification = JobNotificationMapper.getJobApplicationCreatedNotification(savedJobApplication);
            jobNotificationProducer.sendJobApplicationCreatedNotification(jobApplicationCreatedNotification);

            serviceResponse.setData(savedJobApplication);
            serviceResponse.setHttpStatus(HttpStatus.OK);

        } catch (RuntimeException ex) {
            serviceResponse.setErrorMessage(ex instanceof DataIntegrityViolationException ?
                    String.format("User with emailId %s has already applied for this job", createJobApplicationRequest.getUserEmail()) :
                    ex.getMessage());
            serviceResponse.setHttpStatus(ex instanceof DataIntegrityViolationException ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return serviceResponse;
    }

    @Override
    public ServiceResponse<List<UserAppliedJobsResponse>> getUserAppliedJobs(String authorizationToken, int pageNo) {

        ServiceResponse<List<UserAppliedJobsResponse>> serviceResponse = new ServiceResponse<>();
        try {
            String emailId = userMapper.extractEmailFromToken(authorizationToken);
            List<JobApplication> jobApplications = jobApplicationRepository.findByUserEmailId(emailId, PageRequest.of(pageNo, 5,
                    Sort.by("createdDate").descending()));

            if (Objects.isNull(jobApplications) || jobApplications.isEmpty()) {
                serviceResponse.setHttpStatus(pageNo > 0 ? HttpStatus.NO_CONTENT : HttpStatus.NOT_FOUND);
                serviceResponse.setErrorMessage(pageNo > 0 ?
                        "No more job application to show " :
                        "No job application found for requested user");
                return serviceResponse;
            }

            List<UserAppliedJobsResponse> response = JobApplicationMapper.buildUserAppliedJobResponse(jobApplications);
            serviceResponse.setHttpStatus(HttpStatus.OK);
            serviceResponse.setData(response);

        } catch (RuntimeException ex) {
            log.info(String.format("No job application found for requested email id ", ex.getMessage()));

            serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            serviceResponse.setErrorMessage(String.format("Internal server error while searching job application : %s", ex.getMessage()));
        }

        return serviceResponse;
    }


}
