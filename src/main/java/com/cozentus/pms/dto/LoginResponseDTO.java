package com.cozentus.pms.dto;

import com.cozentus.pms.helpers.Roles;

public record LoginResponseDTO(
		String token,
		Roles role,
		String name
		) {

}
