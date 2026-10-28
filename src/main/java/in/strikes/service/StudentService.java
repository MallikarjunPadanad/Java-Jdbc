package in.strikes.service;

import in.strikes.exception.StudentNotFoundException;
import in.strikes.exception.ValidationException;
import in.strikes.model.Student;
import in.strikes.repository.StudentRepository;

import java.util.List;

public class StudentService {

    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 120;
    private static final String EMAIL_REGEX = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        validate(student);

        Long id = studentRepository.create(student);
        student.setId(id);
        return student;
    }

    public void updateStudent(Long id, Student student) {
        validateId(id);
        validate(student);

        if (!studentRepository.update(id, student)) {
            throw new StudentNotFoundException(id);
        }
    }

    public void deleteStudent(Long id) {
        validateId(id);

        if (!studentRepository.delete(id)) {
            throw new StudentNotFoundException(id);
        }
    }

    public Student getStudentById(Long id) {
        validateId(id);

        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id must be a positive number");
        }
    }

    private void validate(Student student) {
        if (student == null) {
            throw new ValidationException("Student must not be null");
        }
        if (student.getName() == null || student.getName().isBlank()) {
            throw new ValidationException("Name is required");
        }
        if (student.getEmail() == null || !student.getEmail().matches(EMAIL_REGEX)) {
            throw new ValidationException("A valid email is required");
        }
        if (student.getAge() < MIN_AGE || student.getAge() > MAX_AGE) {
            throw new ValidationException("Age must be between " + MIN_AGE + " and " + MAX_AGE);
        }
    }
}
