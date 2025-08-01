package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.dto.ManagerDTO;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.dto.ResourceEditDTO;
import com.cozentus.pms.dto.SkillUpsertDTO;
import com.cozentus.pms.serviceImpl.SkillServiceImpl;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.UserInfoService;

import lombok.extern.slf4j.Slf4j;



@RestController
@RequestMapping("/users")
@Slf4j
public class UserInfoController {
	private final UserInfoService userInfoService;
	private final SkillServiceImpl skillServiceImpl;
	private final AuthenticationService authenticationService;
	
	public UserInfoController(UserInfoService userInfoService, SkillServiceImpl skillServiceImpl, AuthenticationService authenticationService) {
		this.userInfoService = userInfoService;
		this.skillServiceImpl = skillServiceImpl;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/project-managers")
	public ResponseEntity<List<ProjectManagerDTO>> getAllProjectManagersWithProjects() {
		String dmEmpId = authenticationService.getCurrentUserDetails().getRight().empId();
		return ResponseEntity.ok(userInfoService.getAllProjectResourcesWithAssociatedProjectsProjects(dmEmpId));
	}
	
	@GetMapping("/resources")
	public ResponseEntity<Page<ResourceDTO>> getAllResourcesWithAllocations(@RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "5") int size,
		    @RequestParam(required = false) String search) {
		Pageable pageable = PageRequest.of(page, size);
		log.info("Fetching resources with search: {}, page: {}, size: {}", search, page, size);
		return ResponseEntity.ok(userInfoService.getAllResourcesWithAllocations(search, pageable));
	}
	
	@GetMapping("/reporting-managers")
	public ResponseEntity<List<ManagerDTO>> getAllReportingManagers() {
		return ResponseEntity.ok(userInfoService.getAllReportingManagers());
	}
	
	@GetMapping("delivery-managers")
	public ResponseEntity<List<ManagerDTO>> getAllDeliveryManagers() {
		return ResponseEntity.ok(userInfoService.getAllDeliveryManagers());
	}
	
	// Add more endpoints as needed for user-related operations
	@PostMapping("/resource")
	public ResponseEntity<String> addResource(@RequestBody ResourceDTO resourceDTO) {
		userInfoService.addResource(resourceDTO);
		return ResponseEntity.ok("Resource added successfully");
	}
	
	// Add more endpoints as needed for user-related operations
	@PutMapping("/resource")
	public ResponseEntity<String> updateResource(@RequestBody ResourceEditDTO resourceEditDTO) {
		userInfoService.updateResource(resourceEditDTO);
		return ResponseEntity.ok("Resource updated successfully");
	}
	
	@GetMapping("/designations")
	public ResponseEntity<List<String>> getAllDesignations() {
		List<String> designations = userInfoService.getAllDesignations();
		return ResponseEntity.ok(designations);
	}
	
	@PutMapping("/resource/{empId}/skills/{skillName}")
	public void updateResourceSkills(@PathVariable String empId, @PathVariable String skillName, @RequestBody SkillUpsertDTO skillUpsertDTO) {
		userInfoService.updateResourceSkills(empId, skillName, skillUpsertDTO);
	}
	
	@PostMapping("/resource/{empId}/skills/{skillName}")
	public void addResourceSkills(@PathVariable String empId, @PathVariable String skillName, @RequestBody SkillUpsertDTO skillUpsertDTO) {
		userInfoService.addSkillToResources(empId, skillName, skillUpsertDTO);
	}
	
	@DeleteMapping("/resource/{empId}/skills/{skillName}")
	public void deleteResourceSkills(@PathVariable String empId, @PathVariable String skillName) {
		userInfoService.deleteSkillFromResource(empId, skillName);
	}
	
	@PostMapping("/skills")
	public ResponseEntity<String> createNewSkill(@RequestParam String skillName) {
		skillServiceImpl.createNewSkill(skillName);
		return ResponseEntity.ok("Skill created successfully");
	}
}
