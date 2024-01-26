package controllers.job.jobControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import models.response.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.job.models.CreateJobRequest;
import services.job.models.JobBySkillsRequest;
import services.job.models.UpdateJobRequest;

@Tag(name = "Jobs Rest Api", description = "API's for job related operations")
public interface JobController {

    @Operation(method = "POST", summary = "Saves the job in database", description = "This api saves the job records created by company's in DB")
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "Job Created Successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request for create job",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    @PostMapping("/createjob")
    public ResponseEntity<ApplicationResponse> createJob(@RequestBody @Valid CreateJobRequest request);


    @Operation(method = "GET", summary = "Returns the job records whose title matches/like title in request", description = "Search the job for requested title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job found for requested title",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Job not found for requested title",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    @GetMapping("/search")
    public ResponseEntity<ApplicationResponse> searchJobsByTitle(@NotBlank @RequestParam("title") String title, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo);

    @Operation(method = "DELETE", summary = "Deletes the job for requested job ID", description = "Accepts the job ID and deletes that job")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job record deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Job not found for requested job id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    @DeleteMapping("/delete")
    public ResponseEntity<ApplicationResponse> deleteJob(@RequestParam("jobId") int jobId);

    @PutMapping("/update")
    @Operation(method = "PUT", summary = "Updates the job record", description = "Updates the existing job records with new properties")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job record updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Job not found for requested job id",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    public ResponseEntity<ApplicationResponse> updateJobRecord(@RequestParam(value = "jobId") int jobId, @Valid @RequestBody UpdateJobRequest updateJobRequest);


    @GetMapping("/searchByLocation")
    @Operation(method = "GET", summary = "Search jobs by location", description = "Searches the job by location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job record fetched for location",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No job found for the requested location",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    public ResponseEntity<ApplicationResponse> searchJobsByLocation(@RequestParam("location") String location, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo);


    @GetMapping(value = "/searchBySkills")
    @Operation(method = "GET", summary = "Search jobs by skill set", description = "Searches the job by skill set")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job record fetched for skill set",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "404", description = "No job found for the requested skill set",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    public ResponseEntity<ApplicationResponse> searchJobsBySkillSet(@RequestBody @Valid JobBySkillsRequest jobBySkillsRequest, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo);


    @GetMapping("/searchByMode")
    @Operation(method = "GET", summary = "Search jobs by job mode", description = "Searches the job by job mode")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Job record fetched for job mode",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "403", description = "Invalid job mode passed in request, only allowed REMOTE,HYBRID or OFFICE",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    public ResponseEntity<ApplicationResponse> searchJobsByJobMode(@RequestParam(value = "jobMode", required = true) String jobMode, @RequestParam(value = "pageNo", required = false, defaultValue = "0") int pageNo);
}
