package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.mapper.SessionMapper;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
public class SessionUnitTests {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Autowired
    private SessionMapper sessionMapper;

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

    @Test
    void testFindAll() {
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Session 1");

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Session 2");

        List<Session> sessions = Arrays.asList(session1, session2);

        when(sessionRepository.findAll()).thenReturn(sessions);

        List<Session> result = sessionService.findAll();

        assertEquals(2, result.size());
        assertEquals("Session 1", result.get(0).getName());
        assertEquals("Session 2", result.get(1).getName());
    }

    @Test
    void testGetByIdExistingSession() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Existing Session");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        Session result = sessionService.getById(1L);

        assertNotNull(result);
        assertEquals("Existing Session", result.getName());
    }

    @Test
    void testCreateSession() {
        Session sessionToCreate = new Session();
        sessionToCreate.setId(1L);
        sessionToCreate.setName("New Session");

        when(sessionRepository.save(sessionToCreate)).thenReturn(sessionToCreate);

        Session createdSession = sessionService.create(sessionToCreate);

        assertNotNull(createdSession);
        assertEquals("New Session", createdSession.getName());
    }

    @Test
    void testUpdateSession() {
        Session existingSession = new Session();
        existingSession.setId(1L);
        existingSession.setName("Existing Session");
        existingSession.setCreatedAt(LocalDateTime.now().minusDays(1));

        Session updatedSession = new Session();
        updatedSession.setId(1L);
        updatedSession.setName("Updated Session");
        updatedSession.setCreatedAt(existingSession.getCreatedAt());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existingSession));
        when(sessionRepository.save(updatedSession)).thenReturn(updatedSession);

        Session result = sessionService.update(1L, updatedSession);

        assertNotNull(result);
        assertEquals("Updated Session", result.getName());
    }

    @Test
    void testDeleteSession() {
        Session existingSession = new Session();
        existingSession.setId(1L);
        existingSession.setName("Existing Session");
        existingSession.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existingSession));

        sessionService.delete(1L);

        verify(sessionRepository).deleteById(1L);
    }

    @Test
    public void testToEntityListSessionDto() {
        SessionDto sessionDto1 = new SessionDto();
        sessionDto1.setId(1L);
        sessionDto1.setName("Session1");
        sessionDto1.setDate(new Date());
        sessionDto1.setTeacher_id(1L);
        sessionDto1.setDescription("Description1");
        sessionDto1.setCreatedAt(LocalDateTime.now());
        sessionDto1.setUpdatedAt(LocalDateTime.now());

        SessionDto sessionDto2 = new SessionDto();
        sessionDto2.setId(2L);
        sessionDto2.setName("Session2");
        sessionDto2.setDate(new Date());
        sessionDto2.setTeacher_id(2L);
        sessionDto2.setDescription("Description2");
        sessionDto2.setCreatedAt(LocalDateTime.now());
        sessionDto2.setUpdatedAt(LocalDateTime.now());

        List<SessionDto> sessionDtoList = Arrays.asList(sessionDto1, sessionDto2);

        List<Session> sessionList = sessionMapper.toEntity(sessionDtoList);

        assertNotNull(sessionList);
        assertEquals(sessionDtoList.size(), sessionList.size());
    }

    @Test
    void testParticipateInSession() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Sample Session");
        session.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(2L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        sessionService.participate(1L, 2L);

        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateInSessionUserNotFound() {
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void testParticipateInSessionAlreadyParticipating() {
        Session session = new Session();
        session.setId(1L);

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("test@example.com");
        existingUser.setLastName("Doe");
        existingUser.setFirstName("John");
        existingUser.setPassword("password");
        existingUser.setAdmin(false);

        session.setUsers(Collections.singletonList(existingUser));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));

        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void testNoLongerParticipateInSession() {
        Session session = new Session();
        session.setId(1L);
        session.setName("Sample Session");
        User user = new User();
        user.setId(2L);
        session.setUsers(Collections.singletonList(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        sessionService.noLongerParticipate(1L, 2L);

        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateInSessionNotParticipating() {
        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }
}
