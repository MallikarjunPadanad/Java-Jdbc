package in.strikes.exception;

/** Thrown when a database operation fails. */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
