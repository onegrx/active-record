package pl.edu.agh.iisg.to;

import org.junit.*;
import pl.edu.agh.iisg.to.connection.ConnectionProvider;
import pl.edu.agh.iisg.to.executor.QueryExecutor;
import pl.edu.agh.iisg.to.model.Course;
import pl.edu.agh.iisg.to.model.Grade;
import pl.edu.agh.iisg.to.model.Student;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ActiveRecordTest {

    @BeforeClass
    public static void init() {
        ConnectionProvider.init("jdbc:sqlite:active_record_test.db");
    }

    @Before
    public void setUp() throws SQLException {
        QueryExecutor.delete("DELETE FROM STUDENT_COURSE");
        QueryExecutor.delete("DELETE FROM STUDENT");
        QueryExecutor.delete("DELETE FROM COURSE");
        QueryExecutor.delete("DELETE FROM GRADE");
    }

    @AfterClass
    public static void cleanUp() throws SQLException {
        ConnectionProvider.close();
    }

    @Test //[1]
    public void createStudentTest() {
        Optional<Student> first = Student.create("Adam", "Kowalski", 100122);
        checkStudent(first);
        Optional<Student> second = Student.create("Jan", "Nowak", 100123);
        checkStudent(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
        Optional<Student> third = Student.create("Kasia", "Kowalska", 100123);
        Assert.assertTrue(!third.isPresent());
    }

    @Test //[2]
    public void findStudentTest() {
        Optional<Student> first = Student.create("Kasia", "Kowalska", 200124);
        checkStudent(first);
        Optional<Student> second = Student.findById(first.get().id());
        Assert.assertEquals(first.get(), second.get());
        Optional<Student> third = Student.findById(Integer.MAX_VALUE);
        Assert.assertTrue(!third.isPresent());
    }

    @Test //[3]
    public void findStudentIndexTest() {
        Optional<Student> first = Student.create("Kasia", "Kowalska", 300124);
        checkStudent(first);
        Optional<Student> second = Student.findByIndexNumber(first.get().indexNumber());
        Assert.assertEquals(first.get(), second.get());
    }

    @Test //[4]
    public void findStudentsTest() {
        Optional<Student> first = Student.create("Adam", "Paciaciak", 400125);
        checkStudent(first);
        Optional<Student> second = Student.create("Jan", "Paciaciak", 400126);
        checkStudent(second);
        List<Student> students = Student.findAllByLastName("Paciaciak");
        Assert.assertEquals(2, students.size());
        Assert.assertTrue(students.contains(first.get()));
        Assert.assertTrue(students.contains(second.get()));
    }

    @Test //[5]
    public void createCourseTest() {
        Optional<Course> first = Course.create("TO");
        checkCourse(first);
        Optional<Course> second = Course.create("TO2");
        checkCourse(second);
        Assert.assertNotEquals(first.get().id(), second.get().id());
        Optional<Course> third = Course.create("TO2");
        Assert.assertTrue(!third.isPresent());
    }

    @Test //[6]
    public void findCourseTest() {
        Optional<Course> first = Course.create("TK");
        checkCourse(first);
        Optional<Course> second = Course.findById(first.get().id());
        Assert.assertEquals(first.get(), second.get());
    }

    @Test //[7]
    public void enrollStudentTest() {
        Optional<Student> first = Student.create("Kasia", "Kowalska", 700124);
        checkStudent(first);
        Optional<Course> second = Course.create("MOWNIT");
        checkCourse(second);
        Assert.assertTrue(second.get().enrollStudent(first.get()));
        Assert.assertFalse(second.get().enrollStudent(first.get()));
    }

    @Test //[8]
    public void courseStudentListTest() {
        Optional<Student> first = Student.create("Adam", "Paciaciak", 800125);
        checkStudent(first);
        Optional<Student> second = Student.create("Jan", "Paciaciak", 800126);
        checkStudent(second);
        Optional<Course> third = Course.create("WDI");
        checkCourse(third);
        Assert.assertTrue(third.get().enrollStudent(first.get()));
        Assert.assertTrue(third.get().enrollStudent(second.get()));
        List<Student> students = third.get().studentList();
        Assert.assertEquals(2, students.size());
        Assert.assertTrue(students.contains(first.get()));
        Assert.assertTrue(students.contains(second.get()));
    }

    @Test //[9]
    public void markStudentTest() {
        Optional<Student> first = Student.create("Kasia", "Kowalska", 900124);
        checkStudent(first);
        Optional<Course> second = Course.create("MOWNIT 2");
        checkCourse(second);
        Assert.assertTrue(Grade.markStudent(first.get(), second.get(), 5.0f));
    }

    @Test //[10]
    public void createReportTest() {
        Optional<Student> first = Student.create("Kasia", "Kowalska", 1000124);
        checkStudent(first);
        Optional<Course> second = Course.create("Bazy");
        checkCourse(second);
        Assert.assertTrue(Grade.markStudent(first.get(), second.get(), 5.0f));
        Assert.assertTrue(Grade.markStudent(first.get(), second.get(), 4.0f));
        Optional<Course> third = Course.create("Bazy 2");
        checkCourse(third);
        Assert.assertTrue(Grade.markStudent(first.get(), third.get(), 5.0f));
        Assert.assertTrue(Grade.markStudent(first.get(), third.get(), 3.0f));
        Map<Course, Float> report = first.get().createReport();
        Assert.assertTrue(Float.compare(4.5f, report.get(second.get())) == 0);
        Assert.assertTrue(Float.compare(4.0f, report.get(third.get())) == 0);
    }

    private void checkStudent(final Optional<Student> student) {
        Assert.assertTrue(student.isPresent());
        student.ifPresent(s -> {
            Assert.assertTrue(s.id() > 0);
            Assert.assertNotNull(s.firstName());
            Assert.assertNotNull(s.lastName());
            Assert.assertTrue(s.indexNumber() > 0);
        });
    }

    private void checkCourse(final Optional<Course> course) {
        Assert.assertTrue(course.isPresent());
        course.ifPresent(c -> {
            Assert.assertTrue(c.id() > 0);
            Assert.assertNotNull(c.name());
        });
    }

}
