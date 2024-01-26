package exception.job;

import lombok.Data;

@Data
public class GeneralApplicationException extends RuntimeException {
    private String message;

    private int statusCode;

    public GeneralApplicationException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

}
