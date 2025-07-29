
package com.cozentus.pms.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.CredentialRepository;

@SpringBootTest
public class UserDetailsServiceImplTest {

    @MockBean
    private CredentialRepository credentialRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoadUserByUsername() {
        Credential credential = new Credential();
        credential.setUsername("test");
        credential.setPassword("test");
        credential.setEnabled(true);
        credential.setRole(Roles.RESOURCE); // assuming Role is an Enum with USER as one of its constants

        when(credentialRepository.findByUsernameAndEnabledTrue("test")).thenReturn(Optional.of(credential));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test");

        assertEquals("test", userDetails.getUsername());
        assertEquals("test", userDetails.getPassword());
        assertEquals(true, userDetails.isEnabled());
        assertEquals(List.of(new SimpleGrantedAuthority("ROLE_USER")), userDetails.getAuthorities());
    }

    @Test
    public void testLoadUserByUsernameNotFound() {
        when(credentialRepository.findByUsernameAndEnabledTrue("test")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("test");
        });
    }
}
