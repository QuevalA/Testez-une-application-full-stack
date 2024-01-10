package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.SessionService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SessionServiceUnitTests {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void testFindAll() {
        // Arrange
        Session session1 = new Session();
        session1.setId(1L);
        session1.setName("Session 1");

        Session session2 = new Session();
        session2.setId(2L);
        session2.setName("Session 2");

        List<Session> sessions = Arrays.asList(session1, session2);

        when(sessionRepository.findAll()).thenReturn(sessions);

        // Act
        List<Session> result = sessionService.findAll();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Session 1", result.get(0).getName());
        assertEquals("Session 2", result.get(1).getName());
    }

    @Test
    void testGetByIdExistingSession() {
        // Arrange
        Session session = new Session();
        session.setId(1L);
        session.setName("Existing Session");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // Act
        Session result = sessionService.getById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Existing Session", result.getName());
    }

    @Test
    void testCreateSession() {
        // Arrange
        Session sessionToCreate = new Session();
        sessionToCreate.setId(1L);
        sessionToCreate.setName("New Session");

        when(sessionRepository.save(sessionToCreate)).thenReturn(sessionToCreate);

        // Act
        Session createdSession = sessionService.create(sessionToCreate);

        // Assert
        assertNotNull(createdSession);
        assertEquals("New Session", createdSession.getName());
    }

    @Test
    void testUpdateSession() {
        // Arrange
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

        // Act
        Session result = sessionService.update(1L, updatedSession);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Session", result.getName());
    }

    @Test
    void testDeleteSession() {
        // Arrange
        Session existingSession = new Session();
        existingSession.setId(1L);
        existingSession.setName("Existing Session");
        existingSession.setCreatedAt(LocalDateTime.now().minusDays(1));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existingSession));

        // Act
        sessionService.delete(1L);

        // Assert
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void testParticipateInSession() {
        // Arrange
        Session session = new Session();
        session.setId(1L);
        session.setName("Sample Session");
        session.setUsers(new ArrayList<>());

        User user = new User();
        user.setId(2L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // Act
        sessionService.participate(1L, 2L);

        // Assert
        assertTrue(session.getUsers().contains(user));
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testParticipateInSessionUserNotFound() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> sessionService.participate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void testParticipateInSessionAlreadyParticipating() {
        // Arrange
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

        // Act and Assert
        assertThrows(BadRequestException.class, () -> sessionService.participate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void testNoLongerParticipateInSession() {
        // Arrange
        Session session = new Session();
        session.setId(1L);
        session.setName("Sample Session");
        User user = new User();
        user.setId(2L);
        session.setUsers(Collections.singletonList(user));

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        // Act
        sessionService.noLongerParticipate(1L, 2L);

        // Assert
        assertTrue(session.getUsers().isEmpty());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void testNoLongerParticipateInSessionNotParticipating() {
        // Arrange
        Session session = new Session();
        session.setId(1L);
        session.setUsers(new ArrayList<>());

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        // Act and Assert
        assertThrows(BadRequestException.class, () -> sessionService.noLongerParticipate(1L, 2L));
        verify(sessionRepository, never()).save(any());
    }
}
