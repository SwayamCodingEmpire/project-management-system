package com.cozentus.pms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.entites.Client;

public interface ClientRepository extends JpaRepository<Client, Integer> {
	List<ClientDTO> findAllByEnabledTrue();

}
