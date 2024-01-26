package services.jobApplication.jobApplicationService;

import entity.jobApplication.JobApplication;
import services.jobApplication.models.CreateJobApplicationRequest;
import services.jobApplication.models.UserAppliedJobsResponse;
import models.response.ServiceResponse;

import java.util.List;

public interface JobApplicationService {

    ServiceResponse<JobApplication> submitJobApplication(CreateJobApplicationRequest createJobApplicationRequest);

    ServiceResponse<List<UserAppliedJobsResponse>> getUserAppliedJobs(String authorizationToken, int pageNo);

}
