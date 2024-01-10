package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SessionUnitTests {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testSessionBuilder() {
        Teacher teacher = Teacher.builder()
                .lastName("Doe")
                .firstName("John")
                .build();

        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        Session session = Session.builder()
                .name("Math Class")
                .date(new Date())
                .description("Introduction to Mathematics")
                .teacher(teacher)
                .users(Arrays.asList(user))
                .build();

        // Verify that the Session is built correctly
        assertNotNull(session);
        assertEquals("Math Class", session.getName());
        assertEquals("Introduction to Mathematics", session.getDescription());

        // Verify that the associated Teacher and User are set correctly
        assertNotNull(session.getTeacher());
        assertEquals("Doe", session.getTeacher().getLastName());
        assertEquals("John", session.getTeacher().getFirstName());

        assertNotNull(session.getUsers());
        assertEquals(1, session.getUsers().size());
        assertEquals("john.doe@example.com", session.getUsers().get(0).getEmail());
    }

    @Test
    public void testNameNotBlank() {
        Session session = Session.builder()
                .name("")
                .date(new Date())
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                .build();

        Set<ConstraintViolation<Session>> violations = validator.validate(session);

        assertEquals(1, violations.size());
        assertEquals("must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void testDateNotNull() {
        Session session = Session.builder()
                .name("Valid name for my new Session")
                .date(null)
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                .build();

        Set<ConstraintViolation<Session>> violations = validator.validate(session);

        assertEquals(1, violations.size());
        assertEquals("must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void testDescriptionNotNull() {
        Session session = Session.builder()
                .name("Valid name for my new Session")
                .date(new Date())
                .description(null)
                .build();

        Set<ConstraintViolation<Session>> violations = validator.validate(session);

        assertEquals(1, violations.size());
        assertEquals("must not be null", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidSession() {
        Session session = Session.builder()
                .name("Valid name for my new Session")
                .date(new Date())
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.")
                .build();

        Set<ConstraintViolation<Session>> violations = validator.validate(session);

        assertEquals(0, violations.size());
    }
}
