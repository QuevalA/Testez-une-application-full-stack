package com.openclassrooms.starterjwt;

import com.jayway.jsonpath.JsonPath;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
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
        Teacher teacherToQuery = teacherRepository.findById(2L).orElse(null);
        assertNotNull(teacherToQuery);

        assertNotNull(teacherToQuery.toString(), "toString() should not be null");

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

    /*@Test
    public void testTeacherToString() {
        long uniqueId = System.currentTimeMillis();

        // Create a sample Teacher instance with a unique ID
        Teacher teacher = new Teacher();
        teacher.setId(uniqueId);
        teacher.setLastName("Doe");
        teacher.setFirstName("John");

        String teacherString = teacher.toString();

        assertNotNull(teacherString, "toString() should not be null");

        // Assert that the toString() result contains the expected values
        assertTrue(teacherString.contains("id=" + uniqueId), "toString() should contain the unique ID");
        assertTrue(teacherString.contains("lastName=Doe"), "toString() should contain the last name");
        assertTrue(teacherString.contains("firstName=John"), "toString() should contain the first name");
    }

    @Test
    public void testTeacherEqualsAndHashCode() {
        // Request Teacher id:1 for testing purpose. Make sure it exists in DB, or adapt id value
        Teacher originalTeacher = teacherRepository.findById(1L).orElse(null);
        assertNotNull(originalTeacher);

        Teacher duplicateTeacher = new Teacher(
                originalTeacher.getId(),
                originalTeacher.getLastName(),
                originalTeacher.getFirstName(),
                originalTeacher.getCreatedAt(),
                originalTeacher.getUpdatedAt()
        );

        // Test equals method
        assertTrue(originalTeacher.equals(duplicateTeacher), "The equals method should return true for equal objects.");
        assertTrue(duplicateTeacher.equals(originalTeacher), "The equals method should be symmetric.");

        assertEquals(originalTeacher.hashCode(), duplicateTeacher.hashCode(), "The hashCode values should be equal for equal hash code fields.");

        Teacher differentTeacher = teacherRepository.findById(2L).orElse(null);

        // Test equals method for non-equal objects
        assertFalse(originalTeacher.equals(differentTeacher), "The equals method should return false for non-equal objects.");
        assertFalse(differentTeacher.equals(originalTeacher), "The equals method should be symmetric for non-equal objects.");

        // Test hashCode method for non-equal objects
        assertNotEquals(originalTeacher.hashCode(), differentTeacher.hashCode(), "The hashCode values should be different for non-equal objects.");
    }

    @Test
    public void testMapTeacherDtoToEntity() {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setLastName("Doe");
        teacherDto.setFirstName("John");
        teacherDto.setCreatedAt(LocalDateTime.now());
        teacherDto.setUpdatedAt(LocalDateTime.now());

        Teacher teacher = teacherMapper.toEntity(teacherDto);

        assertNotNull(teacher);
        assertEquals(teacherDto.getId(), teacher.getId());
        assertEquals(teacherDto.getLastName(), teacher.getLastName());
        assertEquals(teacherDto.getFirstName(), teacher.getFirstName());
    }

    @Test
    public void testMapTeacherDtoListToEntityList() {
        List<TeacherDto> teacherDtoList = Arrays.asList(
                new TeacherDto(1L, "Doe", "John", LocalDateTime.now(), LocalDateTime.now()),
                new TeacherDto(2L, "Smith", "Jane", LocalDateTime.now(), LocalDateTime.now())
        );

        List<Teacher> teacherList = teacherMapper.toEntity(teacherDtoList);

        assertNotNull(teacherList);
        assertEquals(teacherDtoList.size(), teacherList.size());

        assertEquals(teacherDtoList.get(0).getId(), teacherList.get(0).getId());
        assertEquals(teacherDtoList.get(0).getLastName(), teacherList.get(0).getLastName());
        assertEquals(teacherDtoList.get(0).getFirstName(), teacherList.get(0).getFirstName());
    }*/
}
