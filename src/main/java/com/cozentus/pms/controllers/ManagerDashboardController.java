package com.cozentus.pms.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.BenchResourceDTO;
import com.cozentus.pms.dto.KeyPerformanceIndicatorsDTO;
import com.cozentus.pms.dto.ManagerDashboardExportDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ProjectManagerProjectCountDTO;
import com.cozentus.pms.dto.ProjectMinimalDataDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.ResourceBasics;
import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.serviceImpl.SkillServiceImpl;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.DMDashboardService;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.UserInfoService;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/manager-dashboard")
@Slf4j
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
	public ResponseEntity<List<SkillCountDTO>> getSkillStats(@RequestParam(required = false) String search) {
		Pair<Roles, UserAuthDetails> userAuthDetails = authenticationService.getCurrentUserDetails();
		Roles role = userAuthDetails.getLeft();
		String empId = userAuthDetails.getRight().empId();
		log.info("Fetching skill counts for role: {}, empId: {}, search: {}", role, empId, search);
		List<SkillCountDTO> sk = skillServiceImpl.getSkillCounts(role, search, empId);
		log.info("Fetched {} skill counts", sk.size());
		log.info(sk.toString());
			return ResponseEntity.ok(sk);
	}
	
	@GetMapping("/skill-resource-details")
	public ResponseEntity<List<ResourceBasicDTO>> getSkillResourceDetails(@RequestParam String skillName, @RequestParam String level, @RequestParam(required = false) String search) {	
		return ResponseEntity.ok(userInfoService.getAllResourcesAccordingToSkillsAndLevels(skillName, level, search));
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
	
	@GetMapping("/export-all")
	public ResponseEntity<ManagerDashboardExportDTO> exportCompleteDashboard(
	        @RequestParam(required = false) String skillName,
	        @RequestParam(required = false) String level
	) {
	    Pair<Roles, UserAuthDetails> userDetails = authenticationService.getCurrentUserDetails();
	    Roles role = userDetails.getLeft();
	    String empId = userDetails.getRight().empId();
	    Integer userId = userDetails.getRight().userId();
 
	    // Fetch each piece of data
	    KeyPerformanceIndicatorsDTO kpi = KeyPerformanceIndicatorsDTO.from(
	        dmDashboardService.getResourceBillabilityStats(),
	        dmDashboardService.computeUtilizationBreakdown()
	    );
 
	    List<SkillCountDTO> skillCounts = skillServiceImpl.getSkillCounts(role, null, empId);
 
	    Set<ResourceBasics> skillResourceDetails = new HashSet<>();
	    if(role.equals(Roles.DELIVERY_MANAGER) && skillName != null && level != null) {
	    	skillResourceDetails = userInfoService.getSkillsForDM(empId);
	    	log.info("Fetching skills for DM: {}", empId);
	    	log.info(skillResourceDetails.toString());
 
	    }
	    if(role.equals(Roles.PROJECT_MANAGER) && skillName != null && level != null) {
	    	skillResourceDetails = userInfoService.getSkillsForPM(empId);
	    	log.info("Fetching skills for PM: {}", empId);
	    	log.info(skillResourceDetails.toString());
	    }
	    
 
 
	    List<ProjectDashboardDTO> projectDetails = projectDetailsService.getDashboardData(userId, role);
 
	    List<ProjectManagerProjectCountDTO> projectCount = projectDetailsService.getProjectManagersUnderManager(empId);
 
	    // Collect projects under all PMs
	    Map<String, List<ProjectMinimalDataDTO>> projectsByPm = new HashMap<>();
	    for (ProjectManagerProjectCountDTO pm : projectCount) {
	        String pmEmpId = pm.getEmpId();
	        List<ProjectMinimalDataDTO> pmProjects = projectDetailsService.getProjectsUnderManager(pmEmpId, empId);
	        projectsByPm.put(pmEmpId, pmProjects);
	    }
 
	    log.info(skillResourceDetails.toString());
	    ManagerDashboardExportDTO exportDTO = new ManagerDashboardExportDTO(
	        kpi, skillCounts, skillResourceDetails, projectDetails, projectCount, projectsByPm
	    );
 
	    return ResponseEntity.ok(exportDTO);
	}
	
	@GetMapping("/bench-resources")
	public ResponseEntity<List<BenchResourceDTO>> getBenchResources() {
		return ResponseEntity.ok(dmDashboardService.getNonUnitilizedResources());
	}
	
	
}
