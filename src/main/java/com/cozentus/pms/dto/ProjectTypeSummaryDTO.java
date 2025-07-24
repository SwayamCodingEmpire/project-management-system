package com.cozentus.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTypeSummaryDTO {
	private Integer id;
	private String projectType;
	private Boolean customer;
}