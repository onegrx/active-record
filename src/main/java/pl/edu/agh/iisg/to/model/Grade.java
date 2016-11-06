package pl.edu.agh.iisg.to.model;

import pl.edu.agh.iisg.to.executor.QueryExecutor;

import java.sql.SQLException;

public class Grade {

    public static final String TABLE_NAME = "grade";

    private final int id;

    private final float grade;

    private Grade(final int id, final float grade) {
        this.id = id;
        this.grade = grade;
    }

    public static boolean markStudent(final Student student, final Course course, final float grade) {

        String sql = "insert into grade (grade, student_id, course_id) values ('%s', '%s', '%s')";
        String preparedSql = String.format(sql, grade, student.id(), course.id());
        try {
            QueryExecutor.createAndObtainId(preparedSql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public int id() {
        return id;
    }

    public float grade() {
        return grade;
    }

    public static class Columns {

        public static final String ID = "id";

        public static final String GRADE = "grade";

        public static final String STUDENT_ID = "student_id";

        public static final String COURSE_ID = "course_id";

    }

}
