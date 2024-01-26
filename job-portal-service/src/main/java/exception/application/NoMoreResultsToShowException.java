package exception.application;

public class NoMoreResultsToShowException extends RuntimeException {

    private String message;
    private int statusCode;

    public NoMoreResultsToShowException(String message, int statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

}
