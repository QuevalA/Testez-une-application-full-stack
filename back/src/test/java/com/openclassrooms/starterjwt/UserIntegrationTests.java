package com.openclassrooms.starterjwt;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class UserIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private String authToken;

    @BeforeEach
    public void setUp() throws Exception {
        performLogin();
        authToken = JsonPath.read(performLogin().andReturn().getResponse().getContentAsString(), "$.token");
    }

    @Test
    public void testGetUserById() throws Exception {
        // Retrieve the user to be queried
        User userToQuery = userRepository.findById(3L).orElse(null);
        assertNotNull(userToQuery);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/user/{id}", userToQuery.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse the response body and verify user details
        String responseBody = result.getResponse().getContentAsString();
        assertEquals(userToQuery.getId(), Long.valueOf(JsonPath.read(responseBody, "$.id").toString()));
        assertEquals(userToQuery.getEmail(), JsonPath.read(responseBody, "$.email"));
        assertEquals(userToQuery.getLastName(), JsonPath.read(responseBody, "$.lastName"));
        assertEquals(userToQuery.getFirstName(), JsonPath.read(responseBody, "$.firstName"));
        assertEquals(userToQuery.isAdmin(), JsonPath.read(responseBody, "$.admin"));
    }
}
