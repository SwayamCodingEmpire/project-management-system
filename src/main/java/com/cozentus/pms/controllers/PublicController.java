package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.UserInfoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {
	private final UserInfoService userInfoService;
	private final AuthenticationService authenticationService;
	public PublicController(UserInfoService userInfoService, AuthenticationService authenticationService) {
		this.userInfoService = userInfoService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping("/designations")
	public ResponseEntity<List<String>> getAllDesignations() {
		List<String> designations = userInfoService.getAllDesignations();
		return ResponseEntity.ok(designations);
	}
	
	@GetMapping("/skills")
	public ResponseEntity<List<String>> getAllSkills() {
		List<String> skills = userInfoService.getAllSkills();
		return ResponseEntity.ok(skills);
	}
	
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginRequest) {
		log.info("Login attempt for user: {}", loginRequest);
		return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
	}

}
