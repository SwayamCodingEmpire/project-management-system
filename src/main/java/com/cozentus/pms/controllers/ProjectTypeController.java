package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ProjectTypeSummaryDTO;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.services.ProjectTypeService;

@RestController
@RequestMapping("/project-type")
public class ProjectTypeController {

    private final ProjectTypeService projectTypeService;
    
    public ProjectTypeController(@Autowired ProjectTypeService projectTypeService) {
		this.projectTypeService = projectTypeService;
	}
    

    // ✅ Create new ProjectType
    @PostMapping
    public ResponseEntity<ProjectType> saveProjectType(@RequestBody ProjectType projectType) {
        ProjectType saved = projectTypeService.save(projectType);
        return ResponseEntity.ok(saved);
    }

    // ✅ Update existing ProjectType
    @PutMapping("/{id}")
    public ResponseEntity<ProjectType> updateProjectType(@PathVariable Integer id,
                                                         @RequestBody ProjectType updatedType) {
        ProjectType updated = projectTypeService.update(id, updatedType);
        return ResponseEntity.ok(updated);
    }

    // ✅ Get list of ProjectType summary DTOs
    @GetMapping
    public ResponseEntity<List<ProjectTypeSummaryDTO>> getAllProjectTypeSummaries() {
        List<ProjectTypeSummaryDTO> result = projectTypeService.getAllProjectTypeSummaries();
        return ResponseEntity.ok(result);
    }
}