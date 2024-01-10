package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TeacherServiceUnitTests {
    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @Test
    void testFindAll() {
        Teacher teacher1 = new Teacher(1L, "LastName1", "FirstName1", null, null);
        Teacher teacher2 = new Teacher(2L, "LastName2", "FirstName2", null, null);

        List<Teacher> expectedTeachers = Arrays.asList(teacher1, teacher2);

        // Mock the behavior of the teacherRepository
        when(teacherRepository.findAll()).thenReturn(expectedTeachers);

        List<Teacher> actualTeachers = teacherService.findAll();

        assertThat(actualTeachers).isEqualTo(expectedTeachers);
    }

    @Test
    void testFindById() {
        Long teacherId = 1L;
        Teacher expectedTeacher = new Teacher(teacherId, "LastName", "FirstName", null, null);

        // Mock the behavior of the teacherRepository
        when(teacherRepository.findById(anyLong())).thenReturn(Optional.of(expectedTeacher));

        Teacher actualTeacher = teacherService.findById(teacherId);

        assertThat(actualTeacher).isEqualTo(expectedTeacher);
    }

    @Test
    void testFindByIdNotFound() {
        Long nonExistingTeacherId = 999L;

        // Mock the behavior of the teacherRepository for a non-existing teacher
        when(teacherRepository.findById(anyLong())).thenReturn(Optional.empty());

        Teacher actualTeacher = teacherService.findById(nonExistingTeacherId);

        assertThat(actualTeacher).isNull();
    }
}
