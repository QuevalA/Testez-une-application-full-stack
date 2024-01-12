package com.openclassrooms.starterjwt;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.services.SessionService;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class SessionIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private SessionService sessionService;

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
                .andExpect(MockMvcResultMatchers.status().isOk())
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
    public void testGetSessionById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/4")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Session test from UI #1"));
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

        // Perform a mock session update request using the mockMvc
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/session/{id}",
                                sessionToUpdate.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType("application/json")
                        .content(sessionUpdatePayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(sessionToUpdate.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Updated Session Name 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("Updated Session Description 22"))
                .andReturn();

        // Restore the original session entry for cleanup
        sessionRepository.save(sessionToUpdate);
    }

    @Test
    public void testGetAllSessions() throws Exception {
        // Perform a mock request to get all sessions
        mockMvc.perform(MockMvcRequestBuilders.get("/api/session")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeleteSession() throws Exception {
        // Perform a mock request to get all sessions
        MvcResult getAllResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/session")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Retrieve the last session entry's ID
        String lastSessionId = JsonPath.read(getAllResult.getResponse().getContentAsString(), "$[-1].id").toString();

        assertNotNull(lastSessionId);

        // Perform a mock session deletion request using the mockMvc
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/session/{id}", Long.parseLong(lastSessionId))
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Verify that the session has been deleted from the database
        Session deletedSession = sessionRepository.findById(Long.parseLong(lastSessionId)).orElse(null);
        assertNull(deletedSession);
    }
}
