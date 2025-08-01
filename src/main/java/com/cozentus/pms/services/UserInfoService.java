package com.cozentus.pms.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.dto.ManagerDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.ResourceBasics;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.dto.ResourceEditDTO;
import com.cozentus.pms.dto.SkillUpsertDTO;

public interface UserInfoService {
	
	List<ProjectManagerDTO> getAllProjectManagersWithProjects();
	List<ProjectManagerDTO> getAllProjectResourcesWithAssociatedProjectsProjects(String dmEmpId);
	Page<ResourceDTO> getAllResourcesWithAllocations(String search, Pageable pageable);
	List<ManagerDTO> getAllReportingManagers();
	void addResource(ResourceDTO resourceDTO);
	void updateResource(ResourceEditDTO resourceEditDTO);
	List<String> getAllDesignations();
	List<ResourceBasicDTO> getAllResourcesAccordingToSkillsAndLevels(String skillName, String level, String search);
	void updateResourceSkills(String empId, String skillName, SkillUpsertDTO skillUpsertDTO);
	void addSkillToResources(String empId, String skillName, SkillUpsertDTO skillUpsertDTO);
	List<String> getAllSkills();
	void deleteSkillFromResource(String empId, String skillName);
//	Set<ResourceBasics> getAllResourceSkillLevel();
	Set<ResourceBasics> getSkillsForDM();
	Set<ResourceBasics> getSkillsForPM();
	List<ManagerDTO> getAllDeliveryManagers();
}
