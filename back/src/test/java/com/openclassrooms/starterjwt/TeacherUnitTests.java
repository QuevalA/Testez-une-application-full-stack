package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.services.TeacherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TeacherUnitTests {

    @Mock
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherService teacherService;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testTeacherBuilder() {
        Teacher teacher = Teacher.builder()
                .lastName("Doe")
                .firstName("John")
                .build();

        // Verify that the Teacher is built correctly
        assertNotNull(teacher);
        assertEquals("Doe", teacher.getLastName());
        assertEquals("John", teacher.getFirstName());
    }

    @Test
    public void testLastNameNotBlank() {
        Teacher teacher = Teacher.builder()
                .lastName("")
                .firstName("John")
                .build();

        Set<ConstraintViolation<Teacher>> violations = validator.validate(teacher);

        assertEquals(1, violations.size());
        assertEquals("must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void testFirstNameNotBlank() {
        Teacher teacher = Teacher.builder()
                .lastName("Doe")
                .firstName("")
                .build();

        Set<ConstraintViolation<Teacher>> violations = validator.validate(teacher);

        assertEquals(1, violations.size());
        assertEquals("must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidTeacher() {
        Teacher teacher = Teacher.builder()
                .lastName("Doe")
                .firstName("John")
                .build();

        Set<ConstraintViolation<Teacher>> violations = validator.validate(teacher);

        assertEquals(0, violations.size());
    }

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

    @Test
    public void testTeacherToString() {
        long uniqueId = System.currentTimeMillis();

        // Create a sample Teacher instance with a unique ID
        Teacher teacher = new Teacher();
        teacher.setId(uniqueId);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");

        String teacherString = teacher.toString();

        assertNotNull(teacherString, "toString() should not be null");

        // Assert that the toString() result contains the expected values
        assertTrue(teacherString.contains("id=" + uniqueId), "toString() should contain the unique ID");
        assertTrue(teacherString.contains("lastName=Doe"), "toString() should contain the last name");
        assertTrue(teacherString.contains("firstName=John"), "toString() should contain the first name");
    }

    @Test
    public void testMapTeacherDtoToEntity() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Doe");
        teacherDto.setFirstName("John");
        teacherDto.setCreatedAt(LocalDateTime.now());
        teacherDto.setUpdatedAt(LocalDateTime.now());

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertNotNull(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
    }

    @Test
    public void testMapTeacherDtoListToEntityList() {
        List<TeacherDto> teacherDtoList = Arrays.asList(
                new TeacherDto(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()),
                new TeacherDto(2L, "Smith", "Jane", LocalDateTime.now(), LocalDateTime.now())
        );

        List<Teacher> teacherList = teacherMapper.toEntity(teacherDtoList);

        assertNotNull(teacherList);
        assertEquals(teacherDtoList.size(), teacherList.size());

        assertEquals(teacherDtoList.get(0).getId(), teacherList.get(0).getId());
        assertEquals(teacherDtoList.get(0).getLastName(), teacherList.get(0).getLastName());
        assertEquals(teacherDtoList.get(0).getFirstName(), teacherList.get(0).getFirstName());
    }

    @Test
    public void testTeacherEqualsAndHashCode() {
        Teacher originalTeacher = new Teacher(1L, "LastName1", "FirstName1", null, null);

        // Mock the behavior of the teacherRepository
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(originalTeacher));

        Teacher duplicateTeacher = new Teacher(
                originalTeacher.getId(),
                originalTeacher.getLastName(),
                originalTeacher.getFirstName(),
                originalTeacher.getCreatedAt(),
                originalTeacher.getUpdatedAt()
        );

        // Test equals method
        assertTrue(originalTeacher.equals(duplicateTeacher), "The equals method should return true for equal objects.");
        assertTrue(duplicateTeacher.equals(originalTeacher), "The equals method should be symmetric.");

        assertEquals(originalTeacher.hashCode(), duplicateTeacher.hashCode(), "The hashCode values should be equal for equal hash code fields.");

        Teacher differentTeacher = new Teacher(2L, "LastName2", "FirstName2", null, null);

        // Test equals method for non-equal objects
        assertFalse(originalTeacher.equals(differentTeacher), "The equals method should return false for non-equal objects.");
        assertFalse(differentTeacher.equals(originalTeacher), "The equals method should be symmetric for non-equal objects.");

        assertNotEquals(originalTeacher.hashCode(), differentTeacher.hashCode(), "The hashCode values should be different for non-equal objects.");
    }
}
