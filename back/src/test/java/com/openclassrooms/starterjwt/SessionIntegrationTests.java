package com.openclassrooms.starterjwt;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class SessionIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    private String authToken;

    private List<Session> sessionsCreatedDuringTest = new ArrayList<>();
    
    @BeforeEach
    public void setUp() throws Exception {
        performLogin();
        authToken = JsonPath.read(performLogin().andReturn().getResponse().getContentAsString(), "$.token");
    }

    @Test
    public void testCreateSession() throws Exception {
        String sessionCreationPayload = "{\"name\":\"Session created during int. test\",\"date\":\"2024-02-06\",\"teacher_id\":1,\"description\":\"Lorem ipsum dolor sit amet.\"}";

        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionCreationPayload))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Session created during int. test"))
                .andReturn();

        String createdSessionId = JsonPath.read(creationResult.getResponse().getContentAsString(), "$.id").toString();

        Session createdSession = sessionRepository.findById(Long.valueOf(createdSessionId)).orElse(null);

        if (createdSession != null) {
            sessionsCreatedDuringTest.add(createdSession);
        }
    }

    @Test
    public void testCreateSessionWithInvalidData() throws Exception {
        String sessionCreationPayload = "{}";

                        mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionCreationPayload))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetSessionById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/4")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Session test from UI #1"));
    }

    @Test
    public void testRetrieveSessionWithInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/{id}", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateSession() throws Exception {
        // Retrieve the session to be updated
        Session sessionToUpdate = sessionRepository.findById(3L).orElse(null);
        assertNotNull(sessionToUpdate);

        // Prepare the update payload
        String sessionUpdatePayload = "{" +
                "\"name\":\"Updated Session Name 2\"," +
                "\"date\":\"2024-01-26\"," +
                "\"teacher_id\":2," +
                "\"description\":\"Updated Session Description 22\"," +
                "\"users\":[]" +
                "}";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}",
                                sessionToUpdate.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionUpdatePayload))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(sessionToUpdate.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Session Name 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("Updated Session Description 22"))
                .andReturn();

        // Restore the original session entry for cleanup
        sessionRepository.save(sessionToUpdate);
    }

    @Test
    public void testUpdateSessionWithInvalidData() throws Exception {
        // Retrieve the session to be updated
        Session sessionToUpdate = sessionRepository.findById(3L).orElse(null);
        assertNotNull(sessionToUpdate);

        String invalidSessionUpdatePayload = "{}";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}", sessionToUpdate.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(invalidSessionUpdatePayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllSessions() throws Exception {
        // Perform a mock request to get all sessions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSession() throws Exception {
        MvcResult getAllResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/session")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andReturn();

        // Retrieve the last session entry's ID
        String lastSessionId = JsonPath.read(getAllResult.getResponse().getContentAsString(), "$[-1].id").toString();

        assertNotNull(lastSessionId);

        // Perform a mock session deletion request using the mockMvc
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", Long.parseLong(lastSessionId))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Verify that the session has been deleted from the database
        Session deletedSession = sessionRepository.findById(Long.parseLong(lastSessionId)).orElse(null);
        assertNull(deletedSession);
    }

    @Test
    public void testDeleteSessionWithInvalidId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", Long.MAX_VALUE)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testAddUserToSessionParticipants() throws Exception {
        // Create a new session for testing
        String sessionCreationPayload = "{\"name\":\"Session for adding user\",\"date\":\"2024-02-10\",\"teacher_id\":1,\"description\":\"Test session for adding user.\"}";
        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionCreationPayload))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andReturn();

        String createdSessionId = JsonPath.read(creationResult.getResponse().getContentAsString(), "$.id").toString();

        // Add a user to the session participants
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{sessionId}/participate/{userId}", createdSessionId, 2)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Retrieve the updated session
        Session updatedSession = sessionRepository.findById(Long.valueOf(createdSessionId)).orElse(null);
        assertNotNull(updatedSession);

        // Verify that the user has been added to the participants list
        assertTrue(updatedSession.getUsers().stream().anyMatch(user -> user.getId() == 2));

        // Cleanup: Delete the created session
        sessionRepository.deleteById(Long.valueOf(createdSessionId));
    }

    @Test
    public void testRemoveUserFromSessionParticipants() throws Exception {
        // Create a new session for testing with a user in the participants list
        String sessionCreationPayload = "{\"name\":\"Session for removing user\",\"date\":\"2024-02-10\",\"teacher_id\":1,\"description\":\"Test session for removing user.\"}";
        MvcResult creationResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/session")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionCreationPayload))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andReturn();

        String createdSessionId = JsonPath.read(creationResult.getResponse().getContentAsString(), "$.id").toString();

        // Add a user to the session participants for testing removal
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/{sessionId}/participate/{userId}", createdSessionId, 2)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        // Remove the user from the session participants
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{sessionId}/participate/{userId}", createdSessionId, 2)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        Session updatedSession = sessionRepository.findById(Long.valueOf(createdSessionId)).orElse(null);
        assertNotNull(updatedSession);

        assertFalse(updatedSession.getUsers().stream().anyMatch(user -> user.getId() == 2));

        // Cleanup
        sessionRepository.deleteById(Long.valueOf(createdSessionId));
    }

    @Test
    public void testSessionEqualsAndHashCode() {
        Session originalSession = sessionRepository.findById(3L).orElse(null);
        assertNotNull(originalSession);

        Session duplicateSession = new Session(
                        originalSession.getId(),
                        originalSession.getName(),
                        originalSession.getDate(),
                originalSession.getDescription(),
                originalSession.getTeacher(),
                        originalSession.getUsers(),
                        originalSession.getCreatedAt(),
                        originalSession.getUpdatedAt()
                );

        // Test equals method
        assertTrue(originalSession.equals(duplicateSession), "The equals method should return true for equal objects.");
        assertTrue(duplicateSession.equals(originalSession), "The equals method should be symmetric.");

        assertEquals(originalSession.hashCode(), duplicateSession.hashCode(), "The hashCode values should be equal for equal hash code fields.");

        Session differentSession = sessionRepository.findById(4L).orElse(null);

        // Test equals method for non-equal objects
        assertFalse(originalSession.equals(differentSession), "The equals method should return false for non-equal objects.");
        assertFalse(differentSession.equals(originalSession), "The equals method should be symmetric for non-equal objects.");

        // Test hashCode method for non-equal objects
        assertNotEquals(originalSession.hashCode(), differentSession.hashCode(), "The hashCode values should be different for non-equal objects.");
    }
}
