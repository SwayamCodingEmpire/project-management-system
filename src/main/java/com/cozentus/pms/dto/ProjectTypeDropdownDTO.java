package com.cozentus.pms.dto;

public record ProjectTypeDropdownDTO(
		Integer id,
		String projectType,
		boolean isCustomerProject
		) {

}
