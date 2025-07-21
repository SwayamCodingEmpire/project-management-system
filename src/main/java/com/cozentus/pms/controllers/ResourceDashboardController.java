package com.cozentus.pms.controllers;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ResourceWeeklySummaryDTO;
import com.cozentus.pms.dto.UtilizationPairDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.ResourceAllocationService;
@RestController
@RequestMapping("/resource-dashboard")
public class ResourceDashboardController {
	private final ResourceAllocationService resourceAllocationService;
	private final ProjectDetailsService projectDetailsService;
	private final AuthenticationService authenticationService;
	public ResourceDashboardController(ResourceAllocationService resourceAllocationService, ProjectDetailsService projectDetailsService, AuthenticationService authenticationService) {
		this.resourceAllocationService = resourceAllocationService;
		this.projectDetailsService = projectDetailsService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/kpi")
	public ResponseEntity<ResourceWeeklySummaryDTO> getResourceProjectCountAndWeeklyHours() {
		return ResponseEntity.ok(resourceAllocationService.getResourceProjectCountAndWeeklyHours());
	}
	
	@GetMapping("/projects")
	public ResponseEntity<List<ProjectDetailsForProjectListDTO>> getProjectDetailsForResource() {
		return ResponseEntity.ok(projectDetailsService.getAllProjectsForResource());
	}
	
	@GetMapping("/utilization")
	public ResponseEntity<List<UtilizationPairDTO>> getResourceUtilization() {
		 LocalDate today = LocalDate.now();

	        // Start by assuming this week's Friday
	        LocalDate friday = today.with(DayOfWeek.FRIDAY);

	        // If today is Saturday (6) or Sunday (7), go back one week
	        if (today.getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
	            friday = friday.minusWeeks(1);
	        }

	        LocalDate monday = friday.with(DayOfWeek.MONDAY);

	        System.out.println("Latest Monday: " + monday);
	        System.out.println("Latest Friday: " + friday);
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		
		return ResponseEntity.ok(resourceAllocationService.getResourceDashboardUtilStats(empId, monday, friday));
	}
	
	

}
