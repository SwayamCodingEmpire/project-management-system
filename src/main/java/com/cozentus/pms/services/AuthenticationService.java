package com.cozentus.pms.services;

import org.apache.commons.lang3.tuple.Pair;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.helpers.Roles;

public interface AuthenticationService {
	LoginResponseDTO authenticate(LoginDTO loginDTO);
	
	Pair<Roles,UserAuthDetails> getCurrentUserDetails();

}
