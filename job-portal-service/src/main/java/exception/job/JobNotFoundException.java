package exception.job;

import lombok.Data;

@Data
public class JobNotFoundException extends RuntimeException {
    private String message;
    private int statusCode;

    public JobNotFoundException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
}
