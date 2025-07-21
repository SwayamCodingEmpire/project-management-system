package com.cozentus.pms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record ProjectDTO(
		@Valid ProjectDetailsDTO projectInfo, 
		@Valid ProjectTypeDTO projectType,	
		@Valid ClientDTO customerInfo, 
		@NotBlank @JsonProperty("managerId") String managerId) {

}
