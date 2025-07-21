package com.cozentus.pms.controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/manager")
@Slf4j
public class ManagerController {
	private final ProjectDetailsService projectDetailsService;
	private final AuthenticationService authenticationService;
	public ManagerController(ProjectDetailsService projectDetailsService, AuthenticationService authenticationService) {
		this.projectDetailsService = projectDetailsService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/projects")
	public Page<ProjectDetailsForProjectListDTO> getAllProjectsForManager(@RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "5") int size,
		    @RequestParam(required = false) String search) {
		Pair<Roles, UserAuthDetails> currentUserDetails =  authenticationService.getCurrentUserDetails();
		Pageable pageable = PageRequest.of(page, size);
		log.info("Fetching projects for user: {}, search: {}, page: {}, size: {}", 
				currentUserDetails.getRight().userId(), search, page, size);
		log.info("User role: {}", currentUserDetails.getLeft());
		if(currentUserDetails.getLeft().equals(Roles.DELIVERY_MANAGER)) {
			return projectDetailsService.fetchAllProjectsForDeliveryManager(search, pageable, currentUserDetails.getRight().userId());
		}
		return projectDetailsService.fetchAllProjectsForProjectManager(search, pageable, currentUserDetails.getRight().userId());
	}
}
