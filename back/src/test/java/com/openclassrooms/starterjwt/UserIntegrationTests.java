package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserIntegrationTests {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testUserPersistence() {
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        User savedUser = userRepository.save(user);

        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Verify that the retrieved User matches the saved User
        assertNotNull(retrievedUser);
        assertEquals("john.doe@example.com", retrievedUser.getEmail());
        assertEquals("Doe", retrievedUser.getLastName());
        assertEquals("John", retrievedUser.getFirstName());
        assertEquals("password", retrievedUser.getPassword());
        assertFalse(retrievedUser.isAdmin());
    }
}
