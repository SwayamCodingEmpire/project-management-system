package com.cozentus.pms.dto;

import java.time.LocalDate;

public record BenchResourceWithLastDateDTO(
		String id,
		String name,
		String previousProject,
		LocalDate date) {

}
