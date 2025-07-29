
package com.cozentus.pms.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.entites.Client;

@SpringBootTest
public class ClientRepositoryTest {

    @MockBean
    private ClientRepository clientRepository;

    private ClientDTO client1;
    private ClientDTO client2;

    @BeforeEach
    public void setUp() {
        client1 = new ClientDTO(1, "Client1", "Entity1", "Business1");
        client2 = new ClientDTO(2, "Client2", "Entity2", "Business2");
    }

    @Test
    public void testFindAllByEnabledTrue() {
        // Arrange
        when(clientRepository.findAllByEnabledTrue()).thenReturn(Arrays.asList(client1, client2));

        // Act
        List<ClientDTO> result = clientRepository.findAllByEnabledTrue();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(client1));
        assertTrue(result.contains(client2));
    }

    @Test
    public void testFindAllByEnabledTrue_NoResults() {
        // Arrange
        when(clientRepository.findAllByEnabledTrue()).thenReturn(Arrays.asList());

        // Act
        List<ClientDTO> result = clientRepository.findAllByEnabledTrue();

        // Assert
        assertTrue(result.isEmpty());
    }
}
