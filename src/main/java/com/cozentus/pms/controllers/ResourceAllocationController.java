package com.cozentus.pms.controllers;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.ProjectAllocationViewDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceFilterDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ResourceAllocationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/resource-allocation")
@Slf4j
public class ResourceAllocationController {
	private final ResourceAllocationService resourceAllocationService;
	private final AuthenticationService authenticationService;

	public ResourceAllocationController(ResourceAllocationService resourceAllocationService, 
			AuthenticationService authenticationService) {
		this.resourceAllocationService = resourceAllocationService;
		this.authenticationService = authenticationService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<ResourceAllocationsDTO>> getAllResourceAllocations() {
		return ResponseEntity.ok(resourceAllocationService.getAllResourceAllocations());
	}

	@PostMapping
	public ResponseEntity<String> submitResourceAllocations(
			@RequestBody @Validated ProjectResourceAllocationDTO projectResourceAllocationDTO) {
		resourceAllocationService.allocateResources(projectResourceAllocationDTO);
		return ResponseEntity.ok("Resource allocations submitted successfully");
	}

	@PostMapping("/search")
	public ResponseEntity<List<ResourceAllocationsDTO>> seacrhAmongResources(
			@RequestBody ResourceFilterDTO resourceFilterDTO) {

		return ResponseEntity.ok(resourceAllocationService.searchAmongResources(resourceFilterDTO));
	}

	@GetMapping("/project/{projectCode}/allocations")
	public ResponseEntity<ProjectAllocationViewDTO> getProjectAllocationsViewDTO(@PathVariable String projectCode,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size) {
		Pageable pageable = PageRequest.of(page, size);
		log.info("Fetching project allocations for project code: {}, page: {}, size: {}", projectCode, page, size);
		return ResponseEntity.ok(resourceAllocationService.getProjectAllocationsViewDTO(projectCode, pageable));
	}

	@DeleteMapping("/project/{projectCode}/resource/{empId}")
	public ResponseEntity<String> deleteResourceAllocation(@PathVariable String projectCode,
			@PathVariable String empId) {
		log.info("Deleting resource allocation for employee ID: {} in project code: {}", empId, projectCode);
		resourceAllocationService.dellocateResource(projectCode, empId);
		return ResponseEntity.ok("Resource allocation deleted successfully");
	}

	@PutMapping("/allocate-to-dm")
	public ResponseEntity<String> allocateToDM(@RequestParam String resourceEmpId) {
		Integer dmID = authenticationService.getCurrentUserDetails().getRight().userId();
		resourceAllocationService.allocateToDM(resourceEmpId, dmID);
		return ResponseEntity.ok("Resources allocated to Delivery Manager successfully");
	}
	
	@DeleteMapping("/deAllocate-from-dm")
	public ResponseEntity<String> deAllocateFromDM(@RequestParam String resourceEmpId) {
		Integer dmID = authenticationService.getCurrentUserDetails().getRight().userId();
		resourceAllocationService.deallocateResourceFromDM(resourceEmpId, dmID);
		return ResponseEntity.ok("Resources allocated to Delivery Manager successfully");
	}
}
