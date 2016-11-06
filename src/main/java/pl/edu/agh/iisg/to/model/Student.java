package pl.edu.agh.iisg.to.model;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Student {

    public static final String TABLE_NAME = "student";

    private final int id;

    private final String firstName;

    private final String lastName;

    private final int indexNumber;

    private Student(final int id, final String firstName, final String lastName, final int indexNumber) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.indexNumber = indexNumber;
    }

    public static Optional<Student> create(final String firstName, final String lastName, final int indexNumber) {
        String sql = "insert into student (first_name, last_name, index_number) values ('%s', '%s', '%s')";
        String preparedSql = String.format(sql, firstName, lastName, indexNumber);
        try {
            int id = QueryExecutor.createAndObtainId(preparedSql);
            Student student = new Student(id, firstName, lastName, indexNumber);
            return Optional.of(student);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public static Optional<Student> findById(final int id) {
        String sql = "select * from student where id = '%s'";
        String preparedSql = String.format(sql, id);
        try {
            ResultSet set = QueryExecutor.read(preparedSql);
            return Optional.of(new Student(
                    set.getInt("id"),
                    set.getString("first_name"),
                    set.getString("last_name"),
                    set.getInt("index_number")
            ));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public static Optional<Student> findByIndexNumber(final int indexNumber) {
        String sql = "select * from student where index_number = '%s'";
        String preparedSql = String.format(sql, indexNumber);
        try {
            ResultSet set = QueryExecutor.read(preparedSql);
            return Optional.of(new Student(
                    set.getInt("id"),
                    set.getString("first_name"),
                    set.getString("last_name"),
                    set.getInt("index_number")
            ));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public static List<Student> findAllByLastName(final String lastName) {
        String sql = "select * from student where last_name = '%s'";
        String preparedSql = String.format(sql, lastName);

        try {
            List<Student> students = new LinkedList<>();
            ResultSet set = QueryExecutor.read(preparedSql);
            while (set.next()) {
                students.add(new Student(
                        set.getInt("id"),
                        set.getString("first_name"),
                        set.getString("last_name"),
                        set.getInt("index_number")
                ));
            }
            return students;
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public Map<Course, Float> createReport() {
        //TODO [10] Implementacja tworzenia raportu dla studenta w każdym kursie z średnią ocen
        return Collections.emptyMap();
    }

    public int id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public int indexNumber() {
        return indexNumber;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String FIRST_NAME = "first_name";

        public static final String LAST_NAME = "last_name";

        public static final String INDEX_NUMBER = "index_number";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;

        if (id != student.id) return false;
        if (indexNumber != student.indexNumber) return false;
        if (!firstName.equals(student.firstName)) return false;
        return lastName.equals(student.lastName);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + indexNumber;
        return result;
    }
}
