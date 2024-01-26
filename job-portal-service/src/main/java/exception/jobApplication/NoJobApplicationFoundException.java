package exception.jobApplication;

public class NoJobApplicationFoundException extends RuntimeException {
    private String message;
    private int statusCode;

    public NoJobApplicationFoundException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }
    
}
