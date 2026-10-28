package in.strikes.exception;

/** Thrown when an email address is already used by another student. */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }
}
