package controllers.utils;

import models.response.ApplicationResponse;
import models.response.ServiceResponse;
import org.springframework.http.ResponseEntity;

public class BaseControllerUtils {

    public static ResponseEntity<ApplicationResponse> applicationResponse(ServiceResponse serviceResponse) {

        return new ResponseEntity(serviceResponse.getHttpStatus().is2xxSuccessful() ?
                new ApplicationResponse(serviceResponse.getHttpStatus().value(), serviceResponse.getData()) :
                new ApplicationResponse(serviceResponse.getErrorMessage(), serviceResponse.getHttpStatus().value()), serviceResponse.getHttpStatus());
    }
}
