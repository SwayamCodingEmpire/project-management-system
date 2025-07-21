package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.services.ClientService;

@RestController
@RequestMapping("/clients")
public class ClientController {
	
	private final ClientService clientService;
	
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<ClientDTO>> getAllClients() {
		List<ClientDTO> clients = clientService.getAllClients();
		return ResponseEntity.ok(clients);
	}

}
