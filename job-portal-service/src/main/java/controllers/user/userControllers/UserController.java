package controllers.user.userControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import models.response.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "User Rest Api", description = "API's for user operations related to job")
public interface UserController {

    @GetMapping("/register")
    @Operation(method = "GET", summary = "Creates user record", description = "Creates user record in DB by extracting information from authorization token")
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200", description = "User Registered successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class))}),
            @ApiResponse(responseCode = "400", description = "User already registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationResponse.class)
                    ))})
    public ResponseEntity<ApplicationResponse> registerUser(@RequestHeader(name = "Authorization") String authorizationToken);

}
