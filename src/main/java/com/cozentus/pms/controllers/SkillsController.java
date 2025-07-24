package com.cozentus.pms.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.services.SkillService;

@RestController
@RequestMapping("/skills")
public class SkillsController {
	private final SkillService skillService;
	
	public SkillsController(SkillService skillService) {
		this.skillService = skillService;
	}
	@PostMapping
	public void createNewSkill(@RequestParam String skillName) {
		skillService.createNewSkill(skillName);
	}
	
	@PutMapping
	public void updateSkill(@RequestParam String oldSkillName, @RequestParam String newSkillName) {
		skillService.updateSkill(oldSkillName, newSkillName);
	}
	
	@DeleteMapping
	public void deleteSkill(@RequestParam String skillName) {
		skillService.deleteSkill(skillName);
	}
	
}
