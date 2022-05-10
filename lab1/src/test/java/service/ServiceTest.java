package service;

import domain.Grade;
import domain.Homework;
import domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import validation.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {
    private Service service;

    @BeforeEach
    public void setup() {
        Validator<Student> studentValidator = new StudentValidator();
        Validator<Homework> homeworkValidator = new HomeworkValidator();
        Validator<Grade> gradeValidator = new GradeValidator();

        StudentXMLRepository fileRepository1 = new StudentXMLRepository(studentValidator, "students.xml");
        HomeworkXMLRepository fileRepository2 = new HomeworkXMLRepository(homeworkValidator, "homework.xml");
        GradeXMLRepository fileRepository3 = new GradeXMLRepository(gradeValidator, "grades.xml");

        service = new Service(fileRepository1, fileRepository2, fileRepository3);
    }

    public <T> Integer countElements(Iterable<T> iterable) {
        int count = 0;
        for (T element : iterable) {
            count++;
        }

        return count;
    }

    private Homework addDummyHomework() {
        Homework dummyHomework = new Homework(UUID.randomUUID().toString(), "dummy description", 2, 1);
        service.saveHomework(dummyHomework.getID(), dummyHomework.getDescription(), dummyHomework.getDeadline(), dummyHomework.getStartline());

        return dummyHomework;
    }

    private Student addDummyStudent() {
        Student dummyStudent = new Student(UUID.randomUUID().toString(), "dummy name", 531);
        service.saveStudent(dummyStudent.getID(), dummyStudent.getName(), dummyStudent.getGroup());

        return dummyStudent;
    }


    private Integer getHomeworkCount() {
        return countElements(service.findAllHomework());
    }
    private Integer getStudentCount() { return countElements(service.findAllStudents()); }
    private Integer getGradeCount() { return countElements(service.findAllGrades()); }

    @Test
    public void testSaveHomework() {
        Integer sizeBefore = getHomeworkCount();
        addDummyHomework();
        Integer sizeAfter = getHomeworkCount();

        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testSaveStudent() {
        Integer sizeBefore = getStudentCount();
        addDummyStudent();
        Integer sizeAfter = getStudentCount();

        assertEquals(sizeBefore + 1, sizeAfter);
    }

    @Test
    public void testDeleteHomework() {
        Integer sizeBefore = getHomeworkCount();

        Homework dummyHomework = addDummyHomework();
        service.deleteHomework(dummyHomework.getID());

        Integer sizeAfter = getHomeworkCount();

        assertEquals(sizeBefore, sizeAfter);
    }

    private Homework findHomeworkById(String id) {
        Iterable<Homework> homeworks = service.findAllHomework();
        for (Homework homework : homeworks) {
            if (homework.getID().equals(id)) {
                return homework;
            }
        }

        return null;
    }

    @Test
    public void testSaveHomeworkOverwrite() {
        Homework dummyHomework = addDummyHomework();

        service.saveHomework(dummyHomework.getID(), "new description", dummyHomework.getDeadline(), dummyHomework.getStartline());

        assertNotEquals("new description", findHomeworkById(dummyHomework.getID()).getDescription());
    }

    @Test
    public void testAddInvalidHomework() {
        assertThrows(ValidationException.class, () -> service.saveHomework(UUID.randomUUID().toString(), "", 1000, 1200));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 3, 8, 10})
    public void testExtendDeadline(int extendBy) {
        Homework dummyHomework = addDummyHomework();

        int before = dummyHomework.getDeadline();
        service.extendDeadline(dummyHomework.getID(), extendBy);

        assertEquals(before + extendBy, findHomeworkById(dummyHomework.getID()).getDeadline());
    }
}