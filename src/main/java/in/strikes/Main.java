package in.strikes;

import in.strikes.exception.DataAccessException;
import in.strikes.exception.DuplicateEmailException;
import in.strikes.exception.StudentNotFoundException;
import in.strikes.exception.ValidationException;
import in.strikes.model.Student;
import in.strikes.repository.StudentRepository;
import in.strikes.service.StudentService;

public class Main {

    public static void main(String[] args) {
        StudentService service = new StudentService(new StudentRepository());

        try {
            System.out.println("Students before:");
            service.getAllStudents().forEach(System.out::println);

            // The demo only touches the record it creates itself.
            String email = "demo." + System.currentTimeMillis() + "@mail.com";
            Student created = service.createStudent(new Student("Demo User", email, 21));
            System.out.println("Created: " + created);

            Long id = created.getId();
            System.out.println("Fetched: " + service.getStudentById(id));

            service.updateStudent(id, new Student("Demo User Updated", email, 22));
            System.out.println("Updated: " + service.getStudentById(id));

//            service.deleteStudent(id);
//            System.out.println("Deleted student " + id);

            System.out.println("Students after:");
            service.getAllStudents().forEach(System.out::println);

        } catch (ValidationException | DuplicateEmailException | StudentNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
