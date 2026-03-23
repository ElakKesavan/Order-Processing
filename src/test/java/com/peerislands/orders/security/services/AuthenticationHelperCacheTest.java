package com.peerislands.orders.security.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.peerislands.orders.model.Role;
import com.peerislands.orders.model.User;
import com.peerislands.orders.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;

@SpringBootTest
public class AuthenticationHelperCacheTest {

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private CacheManager cacheManager;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private Authentication authentication;

    private User user;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setRole(Role.CUSTOMER);
        user.setPasswordHash("hashedpassword");

        userDetails = UserDetailsImpl.build(user);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        org.springframework.cache.Cache cache = cacheManager.getCache("users");
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    void getAuthenticatedUser_UsesCache() {
        // First call - should hit the database
        User result1 = authenticationHelper.getAuthenticatedUser(authentication);
        assertEquals(user.getId(), result1.getId());
        verify(userRepository, times(1)).findById(1L);

        // Second call - should hit the cache, not the database
        User result2 = authenticationHelper.getAuthenticatedUser(authentication);
        assertEquals(user.getId(), result2.getId());
        // Verify count is still 1, proving the cache was hit
        verify(userRepository, times(1)).findById(1L);
    }
}
