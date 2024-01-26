package controllers.jobApplication.jobApplicationControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import services.jobApplication.models.CreateJobApplicationRequest;
import models.response.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Job Application Rest Api", description = "API's for job application related operations")
public interface JobApplicationController {


    @Operation(method = "POST", summary = "Creates new job application", description = "This api creates new job application in DB")
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Job Application Created Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request for create job application",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    @PostMapping("/create")
    public ResponseEntity<ApplicationResponse> createJobApplication(@RequestBody @Valid CreateJobApplicationRequest createJobApplicationRequest);


    @Operation(method = "GET", summary = "Get the job applications of user", description = "This api gets all the jobs applications for which user applied")
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Fetched user's jobs applications successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error while fetching user's job applications",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    @GetMapping("/applied")
    public ResponseEntity<ApplicationResponse> getUserAppliedJobs(@RequestHeader(name = "Authorization") String authorizationToken, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo);
}
