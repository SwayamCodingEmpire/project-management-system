package com.cozentus.pms.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.MailNotificationConfigDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownGroupDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.services.ProjectDetailsService;


@RestController
@RequestMapping("/project")
public class ProjectDetailsController {
	private final Logger logger = LoggerFactory.getLogger(ProjectDetailsController.class);

	private final ProjectDetailsService projectDetailsService;

	public ProjectDetailsController(ProjectDetailsService projectDetailsService) {
		this.projectDetailsService = projectDetailsService;
	}

	@PostMapping
	public ResponseEntity<String> createProject(@RequestBody @Validated ProjectDTO projectDTO) {
		projectDetailsService.createProjectDetails(projectDTO);
		logger.info("Creating project with details: {}", projectDTO);
		return ResponseEntity.created(null).body("Project created successfully");
	}

	
	@PutMapping("/{code}")
	public ResponseEntity<String> updateProject(@RequestBody @Validated ProjectDTO projectDTO, @PathVariable String code) {
		//cHECK IF THE DM HAS THIS PROJECT CODE ITSELF
		projectDetailsService.updateProjectDetails(projectDTO, code);
		return ResponseEntity.ok("Project updated successfully");
	}
	
	@GetMapping("/{code}")
	public ResponseEntity<ProjectDTO> getProjectDetails(@PathVariable String code) {
		return ResponseEntity.ok(projectDetailsService.getProjectDetails(code));
	}
	
	@GetMapping("/{projectCode}/mail-config")
	public ResponseEntity<MailNotificationConfigDTO> getProjectMailConfig(@PathVariable String projectCode) {
		MailNotificationConfigDTO mailConfig = projectDetailsService.getProjectMailConfig(projectCode);
		logger.info("Fetching mail config for project code: {}", projectCode);
		return ResponseEntity.ok(mailConfig);
	}
	
	@PutMapping("/{projectCode}/mail-config")
	public ResponseEntity<String> saveProjectMailConfig(@PathVariable String projectCode, @RequestBody @Validated MailNotificationConfigDTO mailConfig) {
		projectDetailsService.updateProjectMailConfig(mailConfig, projectCode);
		return ResponseEntity.ok("Updated project mail config successfully");
	}
	
	@PutMapping("/default-mail-config")
	public ResponseEntity<String> updateAllMailNotificationsConfig(@RequestBody @Validated MailNotificationConfigDTO mailConfig) {
		projectDetailsService.updateDefaultProjectMailConfig(mailConfig);
		return ResponseEntity.ok("Updated project mail config successfully");
	}
	
	@GetMapping("/types")
	public ResponseEntity<List<ProjectTypeDropdownGroupDTO>> getProjectTypes() {
		List<ProjectTypeDropdownGroupDTO>projectTypes = projectDetailsService.getAllProjectTypes();
		return ResponseEntity.ok(projectTypes);
	}
	
	@PostMapping("/{projectCode}/skills")
	public void addSkillsToProject(@PathVariable String projectCode, @RequestBody SkillDTO skills, @RequestParam("priority") SkillPriority skillPriority) {
		projectDetailsService.addSkillsToResources(projectCode, skills, skillPriority);
		logger.info("Adding skills to project with code: {}", projectCode);
	}
}
