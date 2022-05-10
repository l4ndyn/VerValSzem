package service;

import domain.Grade;
import domain.Homework;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import repository.GradeXMLRepository;
import repository.HomeworkXMLRepository;
import repository.StudentXMLRepository;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ServiceMockTest {
    private Service service;

    @Mock
    private StudentXMLRepository studentRepository;
    @Mock
    private HomeworkXMLRepository homeworkRepository;
    @Mock
    private GradeXMLRepository gradeRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        service = new Service(studentRepository, homeworkRepository, gradeRepository);
    }

    private Homework addDummyHomework() {
        Homework dummyHomework = new Homework(UUID.randomUUID().toString(), "dummy description", 2, 1);
        service.saveHomework(dummyHomework.getID(), dummyHomework.getDescription(), dummyHomework.getDeadline(), dummyHomework.getStartline());

        return dummyHomework;
    }

    @Test
    public void testSaveHomework() {
        Homework dummyHomework = addDummyHomework();
        Mockito.verify(homeworkRepository).save(dummyHomework);
    }

    @Test
    public void testDeleteInvalidHomework() {
        when(homeworkRepository.delete(anyString())).thenReturn(null);

        Homework dummyHomework = addDummyHomework();
        assertEquals(0, service.deleteHomework(dummyHomework.getID()));
    }

    @Test
    public void testIsHomeworkUpdatedWhenDeadlineExtended() {
        Homework dummyHomework = addDummyHomework();

        when(homeworkRepository.findOne(anyString())).thenReturn(dummyHomework);
        service.extendDeadline(dummyHomework.getID(), 1);

        Mockito.verify(homeworkRepository).update(any());
    }
}
