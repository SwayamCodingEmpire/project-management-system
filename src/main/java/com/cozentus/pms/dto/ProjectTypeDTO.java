package com.cozentus.pms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectTypeDTO(
		@NotNull
		boolean customerProject,
		@NotNull
		Integer projectType) {

}
