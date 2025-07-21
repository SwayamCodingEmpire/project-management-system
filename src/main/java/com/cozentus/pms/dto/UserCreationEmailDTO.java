package com.cozentus.pms.dto;

public record UserCreationEmailDTO(
		String empId,
		String name,
		String email,
		String username,
		String password
		) {

}
