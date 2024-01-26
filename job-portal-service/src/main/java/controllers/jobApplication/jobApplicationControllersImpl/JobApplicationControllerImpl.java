package controllers.jobApplication.jobApplicationControllersImpl;

import controllers.jobApplication.jobApplicationControllers.JobApplicationController;
import controllers.utils.BaseControllerUtils;
import models.response.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.jobApplication.jobApplicationService.JobApplicationService;
import services.jobApplication.models.CreateJobApplicationRequest;

@RestController
@RequestMapping("/application")
public class JobApplicationControllerImpl extends BaseControllerUtils implements JobApplicationController {

    @Autowired
    private JobApplicationService jobApplicationService;

    @Override
    public ResponseEntity<ApplicationResponse> createJobApplication(CreateJobApplicationRequest createJobApplicationRequest) {
        var serviceResponse = jobApplicationService.submitJobApplication(createJobApplicationRequest);

        return applicationResponse(serviceResponse);
    }

    @Override
    public ResponseEntity<ApplicationResponse> getUserAppliedJobs(String authorizationToken, int pageNo) {

        var serviceResponse = jobApplicationService.getUserAppliedJobs(authorizationToken, pageNo);

        return applicationResponse(serviceResponse);
    }

}
