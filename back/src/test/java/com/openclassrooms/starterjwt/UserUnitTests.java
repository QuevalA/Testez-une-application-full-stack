package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.mapper.UserMapper;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.services.UserService;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserUnitTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @Test
    void testUserBuilder() {
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        // Verify that the User is built correctly
        assertNotNull(user);
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("Doe", user.getLastName());
        assertEquals("John", user.getFirstName());
        assertEquals("password", user.getPassword());
        assertFalse(user.isAdmin());
    }

    @Test
    public void testValidEmailFormat() {
        User user = User.builder()
                .email("invalid_email_format")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertEquals("must be a well-formed email address", violations.iterator().next().getMessage());
    }

    @Test
    public void testValidUser() {
        User user = User.builder()
                .email("john.doe@example.com")
                .lastName("Doe")
                .firstName("John")
                .password("password")
                .admin(false)
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
    }

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

    @Test
    public void testUserToString() {
        // Generate a unique ID and email for testing
        long uniqueId = System.currentTimeMillis();
        String uniqueEmail = "test" + uniqueId + "@example.com";

        User user = new User(uniqueId, uniqueEmail, "Doe", "John", "password123", true, LocalDateTime.now(), LocalDateTime.now());

        String toStringResult = user.toString();

        // Verify that the generated string matches expectations
        String expectedToString = "User(id=" + uniqueId + ", email=" + uniqueEmail + ", lastName=Doe, firstName=John, password=password123, admin=true, createdAt=" + user.getCreatedAt() + ", updatedAt=" + user.getUpdatedAt() + ")";
        assertEquals(expectedToString, toStringResult);
    }

    @Test
    public void testUserEqualsAndHashCode() {
        // Mocking the UserRepository to return a user when findById is called
        User originalUser = new User(3L, "original@example.com", "Original", "User", "password", false,
                LocalDateTime.parse("2023-12-21T10:45:53"), LocalDateTime.parse("2023-12-21T10:45:53"));

        when(userRepository.findById(3L)).thenReturn(Optional.ofNullable(originalUser));

        User duplicateUser = new User(
                originalUser.getId(),
                originalUser.getEmail(),
                originalUser.getLastName(),
                originalUser.getFirstName(),
                originalUser.getPassword(),
                originalUser.isAdmin(),
                originalUser.getCreatedAt(),
                originalUser.getUpdatedAt()
        );

        assertTrue(originalUser.equals(duplicateUser), "The equals method should return true for equal objects.");
        assertTrue(duplicateUser.equals(originalUser), "The equals method should be symmetric.");

        assertEquals(originalUser.hashCode(), duplicateUser.hashCode(), "The hashCode values should be equal for equal hash code fields.");

        User differentUser = new User(4L, "different@example.com", "Different", "User", "password", false,
                LocalDateTime.parse("2023-12-21T10:45:53"), LocalDateTime.parse("2023-12-21T10:45:53"));

        when(userRepository.findById(4L)).thenReturn(Optional.ofNullable(differentUser));

        // Test equals method for non-equal objects
        assertFalse(originalUser.equals(differentUser), "The equals method should return false for non-equal objects.");
        assertFalse(differentUser.equals(originalUser), "The equals method should be symmetric for non-equal objects.");

        assertNotEquals(originalUser.hashCode(), differentUser.hashCode(), "The hashCode values should be different for non-equal objects.");
    }

    @Test
    public void testUserBuilderToString() {
        // Generate a unique ID and email for testing
        long uniqueId = System.currentTimeMillis();
        String uniqueEmail = "test" + uniqueId + "@example.com";

        User.UserBuilder userBuilder = User.builder()
                .id(uniqueId)
                .email(uniqueEmail)
                .lastName("Doe")
                .firstName("John")
                .password("password123")
                .admin(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now());

        String toStringResult = userBuilder.toString();

        // Assert that the toString() result is not null or empty
        assertNotNull(toStringResult, "toString() result should not be null");
        assertFalse(toStringResult.isEmpty(), "toString() result should not be empty");

        assertTrue(toStringResult.contains("id=" + uniqueId), "toString() should contain id");
        assertTrue(toStringResult.contains("email=" + uniqueEmail), "toString() should contain email");
    }

    @Test
    public void testToEntityUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setLastName("Doe");
        userDto.setFirstName("John");
        userDto.setPassword("password123");
        userDto.setAdmin(false);
        userDto.setCreatedAt(LocalDateTime.now());
        userDto.setUpdatedAt(LocalDateTime.now());

        User user = userMapper.toEntity(userDto);

        assertNotNull(user);
        assertEquals(userDto.getId(), user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.isAdmin(), user.isAdmin());
        assertEquals(userDto.getCreatedAt(), user.getCreatedAt());
        assertEquals(userDto.getUpdatedAt(), user.getUpdatedAt());
    }

    @Test
    public void testToEntityListUserDto() {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail("test1@example.com");
        userDto1.setLastName("Doe");
        userDto1.setFirstName("John");
        userDto1.setPassword("password123");
        userDto1.setAdmin(false);
        userDto1.setCreatedAt(LocalDateTime.now());
        userDto1.setUpdatedAt(LocalDateTime.now());

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("test2@example.com");
        userDto2.setLastName("Doe");
        userDto2.setFirstName("Jane");
        userDto2.setPassword("password123");
        userDto2.setAdmin(false);
        userDto2.setCreatedAt(LocalDateTime.now());
        userDto2.setUpdatedAt(LocalDateTime.now());

        List<UserDto> userDtoList = Arrays.asList(userDto1, userDto2);

        List<User> userList = userMapper.toEntity(userDtoList);

        assertNotNull(userList);
        assertEquals(userDtoList.size(), userList.size());

        assertEquals(userDto1.getId(), userList.get(0).getId());
        assertEquals(userDto1.getEmail(), userList.get(0).getEmail());
    }

    @Test
    public void testToDtoListUser() {
        User user1 = new User(1L, "test1@example.com", "Doe", "John", "password123", false, LocalDateTime.now(), LocalDateTime.now());
        User user2 = new User(2L, "test2@example.com", "Doe", "Jane", "password123", false, LocalDateTime.now(), LocalDateTime.now());

        List<User> userList = Arrays.asList(user1, user2);

        List<UserDto> userDtoList = userMapper.toDto(userList);

        assertNotNull(userDtoList);
        assertEquals(userList.size(), userDtoList.size());

        assertEquals(user1.getId(), userDtoList.get(0).getId());
        assertEquals(user1.getEmail(), userDtoList.get(0).getEmail());
    }

    @Test
    public void testUserDetailsImplEquals() {
        UserDetailsImpl user1 = UserDetailsImpl.builder().id(1L).username("test@example.com").build();
        UserDetailsImpl user2 = UserDetailsImpl.builder().id(1L).username("test@example.com").build();
        UserDetailsImpl user3 = UserDetailsImpl.builder().id(2L).username("another@example.com").build();

        // Test equals method for equal objects
        assertTrue(user1.equals(user2), "The equals method should return true for equal objects.");
        assertTrue(user2.equals(user1), "The equals method should be symmetric.");

        // Test equals method for non-equal objects
        assertFalse(user1.equals(user3), "The equals method should return false for non-equal objects.");
        assertFalse(user3.equals(user1), "The equals method should be symmetric for non-equal objects.");
    }

    @Test
    public void testUserDetailsImplGetAdmin() {
        UserDetailsImpl user = UserDetailsImpl.builder().admin(true).build();

        assertTrue(user.getAdmin(), "getAdmin() should return true for admin user.");

        user = UserDetailsImpl.builder().admin(false).build();
        assertFalse(user.getAdmin(), "getAdmin() should return false for non-admin user.");
    }
}
