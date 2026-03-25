package com.peerislands.orders.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.peerislands.orders.model.User;
import com.peerislands.orders.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

@ExtendWith(MockitoExtension.class)
public class AuthenticationHelperTest {

    @Mock private UserRepository userRepository;

    @Mock private Authentication authentication;

    @Mock private UserDetailsImpl userDetails;

    @InjectMocks private AuthenticationHelper authenticationHelper;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void getAuthenticatedUser_Success() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = authenticationHelper.getAuthenticatedUser(authentication);

        assertEquals(user, result);
    }

    @Test
    void getAuthenticatedUser_UserNotFound() {
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(
                IllegalStateException.class,
                () -> {
                    authenticationHelper.getAuthenticatedUser(authentication);
                });
    }

    @Test
    void testThreadLocalUserContext() {
        authenticationHelper.setCurrentUser(user);
        User result = authenticationHelper.getCurrentUser();
        assertEquals(user, result);

        authenticationHelper.clear();
        assertThrows(IllegalStateException.class, () -> authenticationHelper.getCurrentUser());
    }

    @Test
    void getCurrentUser_WhenNotSet_ThrowsException() {
        authenticationHelper.clear();
        assertThrows(IllegalStateException.class, () -> authenticationHelper.getCurrentUser());
    }
}
