package controllers.job.jobControllersImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.job.jobControllers.JobController;
import controllers.utils.BaseControllerUtils;
import models.response.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.job.jobService.JobService;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;


@RestController
@RequestMapping("/jobs")
public class JobControllerImpl extends BaseControllerUtils implements JobController {
    @Autowired
    private JobService jobService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResponseEntity<ApplicationResponse> createJob(CreateJobRequest createJobRequest) {

        var serviceResponse = jobService.createJob(createJobRequest);

        return applicationResponse(serviceResponse);

    }


    @Override
    public ResponseEntity<ApplicationResponse> searchJobsByTitle(String title, int pageNo) {

        var serviceResponse = jobService.searchByTitle(title, pageNo);

        return applicationResponse(serviceResponse);
    }

    @Override
    public ResponseEntity<ApplicationResponse> searchJobsByLocation(String location, int pageNo) {

        var serviceResponse = jobService.searchByLocation(location, pageNo);

        return applicationResponse(serviceResponse);
    }

    @Override
    public ResponseEntity<ApplicationResponse> searchJobsBySkillSet(JobBySkillsRequest jobBySkillsRequest, int pageNo) {

        var serviceResponse = jobService.searchJobBySkills(jobBySkillsRequest, pageNo);

        return applicationResponse(serviceResponse);
    }

    @Override
    public ResponseEntity<ApplicationResponse> searchJobsByJobMode(String jobMode, int pageNo) {
        var serviceResponse = jobService.searchJobByJobMode(jobMode, pageNo);

        return applicationResponse(serviceResponse);
    }


    @Override
    public ResponseEntity<ApplicationResponse> deleteJob(int jobId) {
        var serviceResponse = jobService.deleteJobById(jobId);

        return applicationResponse(serviceResponse);
    }

    @Override
    public ResponseEntity<ApplicationResponse> updateJobRecord(int jobId, UpdateJobRequest updateJobRequest) {
        var serviceResponse = jobService.updateJobRecord(updateJobRequest, jobId);

        return applicationResponse(serviceResponse);
    }

}
