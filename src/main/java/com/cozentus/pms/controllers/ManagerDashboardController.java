package com.cozentus.pms.controllers;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.KeyPerformanceIndicatorsDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ProjectManagerProjectCountDTO;
import com.cozentus.pms.dto.ProjectMinimalDataDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.serviceImpl.SkillServiceImpl;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.DMDashboardService;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.UserInfoService;
@RestController
@RequestMapping("/manager-dashboard")
public class ManagerDashboardController {
	private final DMDashboardService dmDashboardService;
	private final SkillServiceImpl skillServiceImpl;
	private final UserInfoService userInfoService;
	private final ProjectDetailsService projectDetailsService;
	private final AuthenticationService authenticationService;
	
	public ManagerDashboardController(DMDashboardService dmDashboardService, SkillServiceImpl skillServiceImpl, UserInfoService userInfoService, ProjectDetailsService projectDetailsService, AuthenticationService authenticationService) {
		this.dmDashboardService = dmDashboardService;
		this.skillServiceImpl = skillServiceImpl;
		this.userInfoService = userInfoService;
		this.projectDetailsService = projectDetailsService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/kpi")
	public ResponseEntity<KeyPerformanceIndicatorsDTO> getKPI() {
		return ResponseEntity.ok(
				KeyPerformanceIndicatorsDTO.from(
						dmDashboardService.getResourceBillabilityStats(), 
						dmDashboardService.computeUtilizationBreakdown()
						)
				);
	}
	
	@GetMapping("/skill-counts")
	public ResponseEntity<List<SkillCountDTO>> getSkillStats() {
		Pair<Roles, UserAuthDetails> userAuthDetails = authenticationService.getCurrentUserDetails();
		Roles role = userAuthDetails.getLeft();
		String empId = userAuthDetails.getRight().empId();
			return ResponseEntity.ok(skillServiceImpl.getSkillCounts(role, empId));
	}
	
	@GetMapping("/skill-resource-details")
	public ResponseEntity<List<ResourceBasicDTO>> getSkillResourceDetails(@RequestParam String skillName, @RequestParam String level) {	
		return ResponseEntity.ok(userInfoService.getAllResourcesAccordingToSkillsAndLevels(skillName, level));
	}
	
	@GetMapping("/projects")
	public ResponseEntity<List<ProjectDashboardDTO>> getProjectDetails() {
		
		Integer managerId = authenticationService.getCurrentUserDetails().getRight().userId();
//		Integer managerId = 25; // Replace with actual manager ID or fetch dynamically
		Roles role = authenticationService.getCurrentUserDetails().getLeft();
		return ResponseEntity.ok(projectDetailsService.getDashboardData(managerId, role));
	}
	
	@GetMapping("/project-count")
	public ResponseEntity<List<ProjectManagerProjectCountDTO>> getProjectCount() {
		String managerId = authenticationService.getCurrentUserDetails().getRight().empId();
		return ResponseEntity.ok(projectDetailsService.getProjectManagersUnderManager(managerId));
	}
	
	@GetMapping("/projects-by-pm/{projectManagerEmpId}")
	public ResponseEntity<List<ProjectMinimalDataDTO>> getProjectManagersUnderManager(@PathVariable String projectManagerEmpId) {
		String dmEmpId = authenticationService.getCurrentUserDetails().getRight().empId();
		return ResponseEntity.ok(projectDetailsService.getProjectsUnderManager(projectManagerEmpId, dmEmpId));
	}
}
