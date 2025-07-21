package com.cozentus.pms.serviceImpl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.repositories.ClientRepository;
import com.cozentus.pms.services.ClientService;
@Service
public class ClientServiceImpl implements ClientService {
	private final ClientRepository clientRepository;
	
	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	@Cacheable(value = "clients", key = "#root.method.name")
	public List<ClientDTO> getAllClients() {
		// TODO Auto-generated method stub
		return clientRepository.findAllByEnabledTrue();
	}

}
