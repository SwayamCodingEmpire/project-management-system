package com.cozentus.pms.services;



import java.util.List;

import com.cozentus.pms.dto.ProjectTypeSummaryDTO;
import com.cozentus.pms.entites.ProjectType;

public interface ProjectTypeService {
	ProjectType save(ProjectType projectType);

	List<ProjectTypeSummaryDTO> getAllProjectTypeSummaries();

	ProjectType update(Integer id, ProjectType updatedType);
}