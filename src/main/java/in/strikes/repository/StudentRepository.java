package in.strikes.repository;

import in.strikes.config.DatabaseConnection;
import in.strikes.exception.DataAccessException;
import in.strikes.exception.DuplicateEmailException;
import in.strikes.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepository {

    private static final String INSERT_SQL =
            "INSERT INTO students (name, email, age) VALUES (?, ?, ?) RETURNING id";

    private static final String UPDATE_SQL =
            "UPDATE students SET name = ?, email = ?, age = ? WHERE id = ?";

    private static final String DELETE_SQL =
            "DELETE FROM students WHERE id = ?";

    private static final String SELECT_BY_ID_SQL =
            "SELECT id, name, email, age FROM students WHERE id = ?";

    private static final String UNIQUE_VIOLATION = "23505"; // PostgreSQL SQLState

    private static final String SELECT_ALL_SQL =
            "SELECT id, name, email, age FROM students ORDER BY id";

    /** Inserts the student and returns the generated id. */
    public Long create(Student student) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getLong("id");
            }

        } catch (SQLException e) {
            throw translate("Failed to create student", e, student);
        }
    }

    public boolean update(Long id, Student student) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, student.getName());
            ps.setString(2, student.getEmail());
            ps.setInt(3, student.getAge());
            ps.setLong(4, id);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw translate("Failed to update student with id " + id, e, student);
        }
    }

    public boolean delete(Long id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_SQL)) {

            ps.setLong(1, id);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete student with id " + id, e);
        }
    }

    public Optional<Student> findById(Long id) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find student with id " + id, e);
        }
    }

    public List<Student> findAll() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet rs = ps.executeQuery()) {

            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(mapRow(rs));
            }
            return students;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch students", e);
        }
    }

    private RuntimeException translate(String message, SQLException e, Student student) {
        if (UNIQUE_VIOLATION.equals(e.getSQLState())) {
            return new DuplicateEmailException(student.getEmail());
        }
        return new DataAccessException(message, e);
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getInt("age"));
    }
}
