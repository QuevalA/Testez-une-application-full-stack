package com.openclassrooms.starterjwt;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
public class TeacherIntegrationTests extends BaseIntegrationTests {

    private String authToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

    @BeforeEach
    public void setUp() throws Exception {
        performLogin();
        authToken = JsonPath.read(performLogin().andReturn().getResponse().getContentAsString(), "$.token");
    }

    @Test
    public void testGetAllTeachers() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);
    }

    @Test
    public void testGetTeacherById() throws Exception {
        // Retrieve the teacher to be queried
        Teacher teacherToQuery = teacherRepository.findById(2L).orElse(null);
        assertNotNull(teacherToQuery);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/teacher/{id}", teacherToQuery.getId())
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        // Parse the response body and verify teacher details
        String responseBody = result.getResponse().getContentAsString();
        assertNotNull(responseBody);

        assertEquals(teacherToQuery.getId(), Long.valueOf(JsonPath.read(responseBody, "$.id").toString()));
        assertEquals(teacherToQuery.getLastName(), new String(JsonPath.read(responseBody, "$.lastName").toString().getBytes("ISO-8859-1"), "UTF-8"));
        assertEquals(teacherToQuery.getFirstName(), new String(JsonPath.read(responseBody, "$.firstName").toString().getBytes("ISO-8859-1"), "UTF-8"));
    }
}
