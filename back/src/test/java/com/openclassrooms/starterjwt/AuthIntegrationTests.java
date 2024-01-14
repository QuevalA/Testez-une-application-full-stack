package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.SignupRequest;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class AuthIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private List<User> usersCreatedDuringTest = new ArrayList<>();

    @Test
    public void testLogin() throws Exception {
        performLogin()
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void testRegistration() throws Exception {
        String registrationPayload = "{\"email\":\"new-user989@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"us!er-password456\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType("application/json")
                        .content(registrationPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User registered successfully!"));

        // Retrieve the user created during the test and store it for cleanup
        User createdUser = userRepository.findByEmail("new-user989@example.com").orElse(null);
        if (createdUser != null) {
            usersCreatedDuringTest.add(createdUser);
        }
    }

    @Test
    public void testSignupRequestEqualsAndHashCode() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("user@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("user@example.com");
        request2.setFirstName("John");
        request2.setLastName("Doe");
        request2.setPassword("password123");

        assertEquals(request1, request2, "equals method should return true for equal objects");
        assertEquals(request1.hashCode(), request2.hashCode(), "hashCode values should be equal for equal objects");
    }

    @Test
    public void testSignupRequestNotEquals() {
        SignupRequest request1 = new SignupRequest();
        request1.setEmail("user1@example.com");
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setPassword("password123");

        SignupRequest request2 = new SignupRequest();
        request2.setEmail("user2@example.com");
        request2.setFirstName("Jane");
        request2.setLastName("Smith");
        request2.setPassword("password456");

        assertNotEquals(request1, request2, "equals method should return false for non-equal objects");
    }

    @Test
    public void testSignupRequestToString() {
        SignupRequest request = new SignupRequest();
        request.setEmail("user@example.com");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setPassword("password123");

        String expectedToString = "SignupRequest(email=user@example.com, firstName=John, lastName=Doe, password=password123)";
        assertEquals(expectedToString, request.toString(), "toString method should return the expected string");
    }

    @AfterEach
    public void cleanup() {
        // Delete only the users created during the test using UserService
        for (User user : usersCreatedDuringTest) {
            userService.delete(user.getId());
        }
        // Clear the list for the next test run
        usersCreatedDuringTest.clear();
    }
}
