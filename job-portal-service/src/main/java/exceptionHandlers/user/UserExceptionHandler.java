package exceptionHandlers.user;

import exception.user.UserAlreadyRegisteredException;
import exception.user.UserNotFoundException;
import models.response.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(value = UserAlreadyRegisteredException.class)
    public ResponseEntity<ApplicationResponse> userAlreadyRegisteredExceptionHandler(UserAlreadyRegisteredException ex) {

        return ResponseEntity.status(ex.getStatusCode())
                .body(new ApplicationResponse(ex.getStatusCode(),
                        null,
                        ex.getMessage()
                ));

    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ApplicationResponse> userNotFoundExceptionHandler(UserNotFoundException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(new ApplicationResponse(
                ex.getStatusCode(),
                null,
                ex.getMessage()
        ));
    }
}
