package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TeacherUnitTests {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testTeacherBuilder() {
        // Build a Teacher
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
}
