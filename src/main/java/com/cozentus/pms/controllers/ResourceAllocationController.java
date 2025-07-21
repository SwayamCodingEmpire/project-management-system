package com.cozentus.pms.controllers;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;

import com.cozentus.pms.dto.ProjectAllocationViewDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceFilterDTO;
import com.cozentus.pms.services.ResourceAllocationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/resource-allocation")
@Slf4j
public class ResourceAllocationController {
	private final ResourceAllocationService resourceAllocationService;
	
	public ResourceAllocationController(ResourceAllocationService resourceAllocationService) {
		this.resourceAllocationService = resourceAllocationService;
	}
	
	 @GetMapping("/all")
	 public ResponseEntity<Page<ResourceAllocationsDTO>> getAllResourceAllocations(@RequestParam(defaultValue = "0") int page,
			    @RequestParam(defaultValue = "5") int size
			   ) {
			Pageable pageable = PageRequest.of(page, size);
			log.info("Fetching resource allocations, page: {}, size: {}", page, size);
		 return ResponseEntity.ok(resourceAllocationService.getAllResourceAllocations(pageable));
	 }
	 
	 @PostMapping
	 public ResponseEntity<String> submitResourceAllocations(@RequestBody ProjectResourceAllocationDTO projectResourceAllocationDTO) {
		 resourceAllocationService.allocateResources(projectResourceAllocationDTO);
		 return ResponseEntity.ok("Resource allocations submitted successfully");
	 }
	 
	 @PostMapping("/search")
	 public ResponseEntity<Page<ResourceAllocationsDTO>> seacrhAmongResources(@RequestParam(defaultValue = "0") int page,
			    @RequestParam(defaultValue = "5") int size,
			    @RequestBody ResourceFilterDTO resourceFilterDTO
			   ) {
			Pageable pageable = PageRequest.of(page, size);
			log.info("Fetching resource allocations, page: {}, size: {}", page, size);
		 return ResponseEntity.ok(resourceAllocationService.searchAmongResources(pageable, resourceFilterDTO));
	 }
	 
	 @GetMapping("/project/{projectCode}/allocations")
	 public ResponseEntity<ProjectAllocationViewDTO> getProjectAllocationsViewDTO(@PathVariable String projectCode,
			    @RequestParam(defaultValue = "0") int page,
			    @RequestParam(defaultValue = "5") int size) {
		 Pageable pageable = PageRequest.of(page, size);
		 log.info("Fetching project allocations for project code: {}, page: {}, size: {}", projectCode, page, size);
		 return ResponseEntity.ok(resourceAllocationService.getProjectAllocationsViewDTO(projectCode, pageable));
	 }
	 
	 @DeleteMapping("/project/{projectCode}/resource/{empId}")
	 public ResponseEntity<String> deleteResourceAllocation(@PathVariable String projectCode, @PathVariable String empId) {
		 log.info("Deleting resource allocation for employee ID: {} in project code: {}", empId, projectCode);
		 resourceAllocationService.dellocateResource(projectCode, empId);
		 return ResponseEntity.ok("Resource allocation deleted successfully");
	 }

}
