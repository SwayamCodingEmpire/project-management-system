package com.cozentus.pms.dto;

import java.time.LocalDate;

public record BenchResourceDTO(
		String id,
		String name,
		String previousProject,
		int daysOnBench) {

}
