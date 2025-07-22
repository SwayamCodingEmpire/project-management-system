package com.cozentus.pms.dto;

import java.util.List;
import java.util.Map;


import lombok.AllArgsConstructor;
import lombok.Data;

public record ManagerDashboardExportDTO(
		KeyPerformanceIndicatorsDTO kpi,
		List<SkillCountDTO> skillCounts,
		List<ResourceBasics> skillResourceDetails,
		List<ProjectDashboardDTO> projectDetails,
		List<ProjectManagerProjectCountDTO> projectCount,
		Map<String, List<ProjectMinimalDataDTO>> projectsByPm
		) 
{


}	