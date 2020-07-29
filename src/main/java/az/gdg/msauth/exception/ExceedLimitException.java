package az.gdg.msauth.exception;

public class ExceedLimitException extends RuntimeException {

    public ExceedLimitException(String message) {
        super(message);
    }
}
