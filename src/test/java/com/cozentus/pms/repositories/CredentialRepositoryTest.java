
package com.cozentus.pms.repositories;

import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.helpers.Roles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CredentialRepositoryTest {

    @MockBean
    private CredentialRepository credentialRepository;

    @Test
    void testUpdateRoleByUserId() {
        when(credentialRepository.updateRoleByUserId(any(Integer.class), any(Roles.class))).thenReturn(1);

        int result = credentialRepository.updateRoleByUserId(1, Roles.PROJECT_MANAGER);

        assertEquals(1, result);
        verify(credentialRepository, times(1)).updateRoleByUserId(any(Integer.class), any(Roles.class));
    }

    @Test
    void testFindByUsernameAndEnabledTrue() {
        when(credentialRepository.findByUsernameAndEnabledTrue(any(String.class)))
                .thenReturn(Optional.of(new Credential()));

        Optional<Credential> result = credentialRepository.findByUsernameAndEnabledTrue("username");

        assertEquals(true, result.isPresent());
        verify(credentialRepository, times(1)).findByUsernameAndEnabledTrue(any(String.class));
    }

    @Test
    void testFindByUsername() {
        when(credentialRepository.findByUsername(any(String.class)))
                .thenReturn(Optional.of(new Credential()));

        Optional<Credential> result = credentialRepository.findByUsername("username");

        assertEquals(true, result.isPresent());
        verify(credentialRepository, times(1)).findByUsername(any(String.class));
    }
}
