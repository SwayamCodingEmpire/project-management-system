package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ProjectResourceSummaryCountDTO;
import com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;

@RestController
@RequestMapping("/pm-dashboard")
public class ProjectManagerDashboardController {
	private final ProjectDetailsService projectDetailsService;
	private final AuthenticationService authenticationService;
	public ProjectManagerDashboardController(ProjectDetailsService projectDetailsService, AuthenticationService authenticationService) {
		this.projectDetailsService = projectDetailsService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/projects")
	public ResponseEntity<List<ProjectResourceSummaryCountDTO>> getProjectUnderManager() {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		return ResponseEntity.ok(projectDetailsService.getProjectResourceSummaryByManager(empId));
	}
	
	@GetMapping("/project-resource/{projectCode}")
	public ResponseEntity<List<ResourceProjectMinimalDashboardDTO>> getProjectResourceSummary(@PathVariable String projectCode) {
		return ResponseEntity.ok(projectDetailsService.getResourceProjectMinimalDashboardData(projectCode));
	}

}
