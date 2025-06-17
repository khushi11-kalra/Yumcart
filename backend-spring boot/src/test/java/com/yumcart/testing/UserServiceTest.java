package com.yumcart.testing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.yumcart.config.JwtProvider;
import com.yumcart.model.User;
import com.yumcart.Exception.UserException;
import com.yumcart.repository.UserRepository;
import com.yumcart.service.UserServiceImplementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy  // Use @Spy instead of @Mock for final/static class
    private JwtProvider jwtProvider;

    @InjectMocks
    private UserServiceImplementation userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("password123");
    }

    @Test
    void testFindUserProfileByJwt_Success() throws UserException {
        // Mock behavior for a valid token
        doReturn(mockUser.getEmail()).when(jwtProvider).getEmailFromJwtToken(anyString());
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(mockUser);

        // Call the method
        User foundUser = userService.findUserProfileByJwt("mockJwtToken");

        // Validate results
        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getFullName());
        assertEquals("john.doe@example.com", foundUser.getEmail());
    }

    @Test
    void testFindUserProfileByJwt_UserNotFound() {
        // Mock behavior for an invalid email
        doReturn("unknown@example.com").when(jwtProvider).getEmailFromJwtToken(anyString());
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(null);

        // Assert exception is thrown
        UserException exception = assertThrows(UserException.class, () -> {
            userService.findUserProfileByJwt("mockJwtToken");
        });

        assertEquals("User does not exist with email unknown@example.com", exception.getMessage());
    }
}
