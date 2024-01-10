package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testFindById() {
        // Mocking the UserRepository to return a user when findById is called
        User mockUser = new User(2L, "mau-lahaie@email.fr", "Lahaie", "Maurice", "password", false,
                LocalDateTime.parse("2023-12-21T10:45:53"), LocalDateTime.parse("2023-12-21T10:45:53"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockUser));

        User result = userService.findById(2L);

        assertEquals(mockUser, result);
    }

    @Test
    public void testDeleteUser() {
        // Mocking the UserRepository to return a user when findById is called
        User mockUser = new User(2L, "mau-lahaie@email.fr", "Lahaie", "Maurice", "password", false,
                LocalDateTime.parse("2023-12-21T10:45:53"), LocalDateTime.parse("2023-12-21T10:45:53"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(mockUser));

        userService.delete(2L);

        // Verifying that the deleteById method is called with the correct userId
        verify(userRepository).deleteById(2L);
    }
}
