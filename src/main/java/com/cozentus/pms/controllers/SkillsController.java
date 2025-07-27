package com.cozentus.pms.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
	public ResponseEntity<?> createNewSkill(@RequestParam String skillName) {
	    if (skillName == null || skillName.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("Skill name must not be blank.");
	    }
		skillService.createNewSkill(skillName.toUpperCase().trim());
		return ResponseEntity.ok("Skill updated successfully.");
	}
	
	@PutMapping
	public ResponseEntity<?> updateSkill(@RequestParam String oldSkillName, @RequestParam String newSkillName) {
	    if (oldSkillName == null || oldSkillName.trim().isEmpty() ) {
	        return ResponseEntity.badRequest().body("Old Skill name must not be blank.");
	    }
	    else if (newSkillName == null || newSkillName.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("New skill name must not be blank.");
	    }
	    else if (oldSkillName.equalsIgnoreCase(newSkillName)) {
	        return ResponseEntity.badRequest().body("New skill name must be different from the old skill name.");
	    }
		skillService.updateSkill(oldSkillName, newSkillName.toUpperCase().trim());
		return ResponseEntity.ok("Skill updated successfully.");
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteSkill(@RequestParam String skillName) {
			    if (skillName == null || skillName.trim().isEmpty()) {
	        return ResponseEntity.badRequest().body("Skill name must not be blank.");
	    }
		skillService.deleteSkill(skillName);
		return ResponseEntity.ok("Skill deleted successfully.");
	}
	
}
