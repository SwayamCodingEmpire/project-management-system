package com.cozentus.pms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.helpers.Roles;

public interface CredentialRepository extends JpaRepository<Credential, Long> {

	@Transactional
	@Modifying
	@Query("""
	    UPDATE Credential c
	    SET c.role = :projectManager
	    WHERE c.enabled = true
	    AND EXISTS (
	        SELECT 1 FROM UserInfo u
	        WHERE u.credential = c
	        AND u.id = :id
	        AND u.enabled = true
	    )
	""")
	int updateRoleByUserId(@Param("id") Integer id, @Param("projectManager") Roles projectManager);
	
	
	
	Optional<Credential> findByUsernameAndEnabledTrue(String username);
	Optional<Credential> findByUsername(String username);



	// Define custom query methods if needed
	// For example:
	// Optional<Credential> findByUsername(String username);
	
	// You can also define methods for CRUD operations if needed
	// @Override
	// <S extends Credential> S save(S entity);
	
	// @Override
	// Optional<Credential> findById(Long id);
	
	// @Override
	// void deleteById(Long id);

}
