package exceptionHandlers.job;

import exception.job.GeneralApplicationException;
import exception.job.JobNotFoundException;
import models.response.ApplicationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class JobExceptionsHandler {

    @ExceptionHandler(value = GeneralApplicationException.class)
    public ResponseEntity<ApplicationResponse> generalApplicationExceptionHandler(GeneralApplicationException ex) {

        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApplicationResponse(ex.getStatusCode(),
                        ex.getMessage()
                ));

    }

    @ExceptionHandler(value = JobNotFoundException.class)
    public ResponseEntity<ApplicationResponse> jobNotFoundExceptionHandler(JobNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                .body(new ApplicationResponse(ex.getStatusCode(),
                        ex.getMessage())
                );

    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApplicationResponse> genericExceptionHandler(RuntimeException ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(new ApplicationResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ex.getMessage())
                );

    }

}
