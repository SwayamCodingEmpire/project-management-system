package com.cozentus.pms.dto;

import com.cozentus.pms.helpers.Roles;

public record UserInfoIdentifierDTO(
		Integer id,
		String name,
		String emailId, 
		Roles role) {

}
