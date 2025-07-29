
package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ClientServiceImplTest {

    @MockBean
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllClients() {
        // Arrange
        ClientDTO client1 = new ClientDTO(1, "Client1", "Entity1", "Unit1");
        ClientDTO client2 = new ClientDTO(2, "Client2", "Entity2", "Unit2");
        List<ClientDTO> expectedClients = Arrays.asList(client1, client2);

        when(clientRepository.findAllByEnabledTrue()).thenReturn(expectedClients);

        // Act
        List<ClientDTO> actualClients = clientService.getAllClients();

        // Assert
        assertEquals(expectedClients, actualClients);
    }

    @Test
    public void testGetAllClientsWhenNoClients() {
        // Arrange
        when(clientRepository.findAllByEnabledTrue()).thenReturn(Arrays.asList());

        // Act
        List<ClientDTO> actualClients = clientService.getAllClients();

        // Assert
        assertEquals(0, actualClients.size());
    }
}
