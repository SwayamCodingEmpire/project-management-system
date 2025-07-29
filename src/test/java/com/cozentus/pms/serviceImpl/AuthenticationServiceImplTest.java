
package com.cozentus.pms.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.JwtService;

@SpringBootTest
public class AuthenticationServiceImplTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticate() {
        LoginDTO loginDTO = new LoginDTO("test@test.com", "password");
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("token");
//        when(userInfoRepository.findNameByUsername(any(String.class))).thenReturn(Optional.of("Test User"));

        LoginResponseDTO response = authenticationService.authenticate(loginDTO);

        assertEquals("token", response.token());
        assertEquals(Roles.RESOURCE , response.role());
//        assertEquals("Test User", response.name());
    }

    @Test
    public void testAuthenticateWithNullUsernameAndPassword() {
        LoginDTO loginDTO = new LoginDTO(null, null);

        assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.authenticate(loginDTO);
        });
    }

    @Test
    public void testAuthenticateWithInvalidUser() {
        LoginDTO loginDTO = new LoginDTO("test@test.com", "password");
        when(userDetailsService.loadUserByUsername(any(String.class))).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> {
            authenticationService.authenticate(loginDTO);
        });
    }

    @Test
    public void testGetCurrentUserDetails() {
        // TODO: Implement this test
    }
}
