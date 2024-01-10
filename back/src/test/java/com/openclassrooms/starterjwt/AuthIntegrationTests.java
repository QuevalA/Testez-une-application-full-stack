package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class AuthIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private Environment environment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private List<User> usersCreatedDuringTest = new ArrayList<>();

    @Test
    public void testLogin() throws Exception {
        // Load credentials from properties
        String email = environment.getProperty("TEST_ADMIN_USER_EMAIL");
        String password = environment.getProperty("TEST_ADMIN_USER_PASSWORD");

        // Prepare login request payload
        String loginPayload = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        // Set the authentication in the SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Perform a mock login request using the mockMvc
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType("application/json")
                        .content(loginPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());
    }

    @Test
    public void testRegistration() throws Exception {
        // Prepare registration request payload
        String registrationPayload = "{\"email\":\"new-user989@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\",\"password\":\"us!er-password456\"}";

        // Perform a mock registration request using the mockMvc
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
