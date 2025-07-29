
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.services.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ClientControllerTest {

    @Autowired
    private ClientController clientController;

    @MockBean
    private ClientService clientService;

    @Test
    public void testGetAllClients() {
        // Arrange
        ClientDTO client1 = new ClientDTO(1, "Client 1", "Entity 1", "Unit 1");
        ClientDTO client2 = new ClientDTO(2, "Client 2", "Entity 2", "Unit 2");
        List<ClientDTO> expectedClients = Arrays.asList(client1, client2);
        when(clientService.getAllClients()).thenReturn(expectedClients);

        // Act
        ResponseEntity<List<ClientDTO>> responseEntity = clientController.getAllClients();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedClients, responseEntity.getBody());
    }

    @Test
    public void testGetAllClientsWhenNoClients() {
        // Arrange
        when(clientService.getAllClients()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<List<ClientDTO>> responseEntity = clientController.getAllClients();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(0, responseEntity.getBody().size());
    }
}
