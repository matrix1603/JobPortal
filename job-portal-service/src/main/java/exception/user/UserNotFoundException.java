package exception.user;

import lombok.Data;

@Data
public class UserNotFoundException extends RuntimeException {
    private String message;
    private int statusCode;

    public UserNotFoundException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
