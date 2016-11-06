package pl.edu.agh.iisg.to.model;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Course {

    public static final String TABLE_NAME = "course";

    private final int id;

    private final String name;

    private Course(final int id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static Optional<Course> create(final String name) {
        String sql = "insert into course (name) values ('%s')";
        String preparedSql = String.format(sql, name);
        try {
            int id = QueryExecutor.createAndObtainId(preparedSql);
            Course course = new Course(id, name);
            return Optional.of(course);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public static Optional<Course> findById(final int id) {
        String sql = "select * from course where id = '%s'";
        String preparedSql = String.format(sql, id);
        try {
            ResultSet set = QueryExecutor.read(preparedSql);
            return Optional.of(new Course(
                    set.getInt("id"),
                    set.getString("name")
            ));
        } catch (SQLException e) {
            return Optional.empty();
        }
    }

    public boolean enrollStudent(final Student student) {
        String sql = "insert into student_course (student_id, course_id) values ('%s', '%s')";
        String preparedSql = String.format(sql, student.id(), id());
        try {
            QueryExecutor.createAndObtainId(preparedSql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Student> studentList() {
        String sql = "select student_id from student_course where course_id = '%s'";
        String preparedSql = String.format(sql, id());
        try {
            ResultSet set = QueryExecutor.read(preparedSql);
            List<Integer> studentIds = new LinkedList<>();
            while (set.next()) {
                studentIds.add(set.getInt("student_id"));
            }
            List<Student> students = new LinkedList<>();
            studentIds.forEach(id -> {
                Optional<Student> student = Student.findById(id);
                if(student.isPresent()) {
                    students.add(student.get());
                }
            });
            return students;
        } catch (SQLException e) {
            return Collections.emptyList();
        }

//        String sql2 = "select s.id, s.first_name, s.last_name, s.index_number from student as s " +
//                "inner join student_course as sc where sc = '%s'";

    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String NAME = "name";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Course course = (Course) o;

        if (id != course.id) return false;
        return name.equals(course.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }
}
