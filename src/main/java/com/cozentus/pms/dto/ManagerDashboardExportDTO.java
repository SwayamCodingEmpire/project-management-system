package com.cozentus.pms.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ManagerDashboardExportDTO(
		KeyPerformanceIndicatorsDTO kpi,
		List<SkillCountDTO> skillCounts,
		Set<ResourceBasics> skillResourceDetails,
		List<ProjectDashboardDTO> projectDetails,
		List<ProjectManagerProjectCountDTO> projectCount,
		Map<String, List<ProjectMinimalDataDTO>> projectsByPm
		) 
{


}	