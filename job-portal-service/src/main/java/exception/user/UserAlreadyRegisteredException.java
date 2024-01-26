package exception.user;

import lombok.Data;

@Data
public class UserAlreadyRegisteredException extends RuntimeException {

    private String message;
    private int statusCode;

    public UserAlreadyRegisteredException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
