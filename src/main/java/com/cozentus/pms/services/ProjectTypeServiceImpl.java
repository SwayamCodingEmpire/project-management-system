package com.cozentus.pms.services;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ProjectTypeSummaryDTO;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.repositories.ProjectTypeRepository;

@Service
public class ProjectTypeServiceImpl implements ProjectTypeService {
	@Autowired
	private ProjectTypeRepository projectTypeRepository;

	@Override
	public ProjectType save(ProjectType projectType) {
		return projectTypeRepository.save(projectType);
	}

	@Override
	public List getAllProjectTypeSummaries() {
		return projectTypeRepository.findAll().stream()
				.map(pt -> new ProjectTypeSummaryDTO(pt.getId(), pt.getProjectType(), pt.isCustomerProject() // Fixed
																												// line
				)).collect(Collectors.toList());
	}

	@Override
	public ProjectType update(Integer id, ProjectType updatedType) {
		ProjectType existing = projectTypeRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("ProjectType not found"));

		existing.setProjectType(updatedType.getProjectType());
		existing.setCustomerProject(updatedType.isCustomerProject());
		return projectTypeRepository.save(existing);
	}
}