package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserUnitTests {
    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testUserBuilder() {
        // Build a User
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        // Verify that the User is built correctly
        assertNotNull(user);
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("password", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    public void testValidEmailFormat() {
        User user = User.builder()
                .email("invalid_email_format")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidUser() {
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }
}
