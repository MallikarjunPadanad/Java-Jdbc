package in.strikes.exception;

/** Thrown when a student with the given id does not exist. */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student not found with id " + id);
    }
}
