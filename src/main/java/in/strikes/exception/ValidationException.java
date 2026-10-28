package in.strikes.exception;

/** Thrown when input data breaks a business rule. */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }
}
