package com.cozentus.pms.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cozentus.pms.dto.MailNotificationConfigDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ProjectManagerProjectCountDTO;
import com.cozentus.pms.dto.ProjectMinimalDataDTO;
import com.cozentus.pms.dto.ProjectResourceSummaryCountDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownGroupDTO;
import com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;

public interface ProjectDetailsService {
	
	void createProjectDetails(ProjectDTO projectDTO);
	 Page<ProjectDetailsForProjectListDTO> fetchAllProjectsForProjectManager(
		        String search, Pageable pageable, Integer projectManagerId);
	Page<ProjectDetailsForProjectListDTO> fetchAllProjectsForDeliveryManager(String search, Pageable pageable, Integer deliveryManagerId);
	void updateProjectDetails(ProjectDTO projectDTO, String code);
	ProjectDTO getProjectDetails(String code);
	MailNotificationConfigDTO getProjectMailConfig(String projectCode);
	void updateProjectMailConfig(MailNotificationConfigDTO mailConfig, String projectCode);
	void updateDefaultProjectMailConfig(MailNotificationConfigDTO mailConfig);
	List<ProjectTypeDropdownGroupDTO> getAllProjectTypes();
	void addSkillsToResources(String projectCode, SkillDTO skills, SkillPriority skillPriority);
	List<ProjectDashboardDTO> getDashboardData(Integer deliveryManagerId, Roles role) ;
	List<ProjectManagerProjectCountDTO> getProjectManagersUnderManager(String empId);
	List<ProjectMinimalDataDTO> getProjectsUnderManager(String projectManagerEmpId, String dmEmpId);
	List<ProjectResourceSummaryCountDTO> getProjectResourceSummaryByManager(String dmEmpId);
	List<ResourceProjectMinimalDashboardDTO> getResourceProjectMinimalDashboardData(String projectCode);
	List<ProjectDetailsForProjectListDTO> getAllProjectsForResource();
	
	
	
	

}
