package com.cozentus.pms.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cozentus.pms.dto.SingularTimesheetPayload;
import com.cozentus.pms.dto.TimesheetApprovalDTO;
import com.cozentus.pms.dto.TimesheetDTO;
import com.cozentus.pms.dto.TimesheetSummaryDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.TimesheetService;
@RestController
@RequestMapping("/timesheet")
public class TimesheetController {
	private final TimesheetService timesheetService;
	private final AuthenticationService authenticationService;
	
	public TimesheetController(TimesheetService timesheetService, AuthenticationService authenticationService) {
		this.timesheetService = timesheetService;
		this.authenticationService = authenticationService;
	}
	
	@GetMapping
	public ResponseEntity<List<TimesheetDTO>> getTimesheetByEmpId(@RequestParam
			LocalDate startDate, 
			@RequestParam
			
			LocalDate endDate) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		List<TimesheetDTO> timesheetDTOs = timesheetService.getTimeSheetByEmpId(empId, startDate, endDate);
		return ResponseEntity.ok(timesheetDTOs);
	}
	
	@PostMapping
	public ResponseEntity<String> createTimesheet(@RequestBody SingularTimesheetPayload singularTimesheetPayload) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		timesheetService.saveTimesheet(singularTimesheetPayload, empId);
		return ResponseEntity.ok("Timesheet created successfully");
	}
	
//	@PostMapping("/submit")
//	public ResponseEntity<String> submitTimesheet(@RequestBody SingularTimesheetPayload singularTimesheetPayload) {
//		timesheetService.submitTimesheet(singularTimesheetPayload, "EMP1007");
//		return ResponseEntity.ok("Timesheet submitted successfully");
//	}
	
	@GetMapping("/summary")
	public ResponseEntity<List<TimesheetSummaryDTO>> getTimesheetSummaryByManagerId(@RequestParam	
			LocalDate startDate, 
			@RequestParam
			LocalDate endDate) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		Roles role = authenticationService.getCurrentUserDetails().getLeft();
		List<TimesheetSummaryDTO> timesheetSummary = timesheetService.getTimeSheetSummaryByManagerId(empId, startDate, endDate, role);
		return ResponseEntity.ok(timesheetSummary);
	}
	
	@GetMapping("/resource/{resourceId}/project/{projectCode}")
	public ResponseEntity<TimesheetDTO> getTimesheetByManagerIdAndResourceIdAndProjectCode(
			@PathVariable String resourceId,
			@RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate,
			@PathVariable String projectCode) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		TimesheetDTO timesheetDTO = timesheetService.getTimeSheetByManagerIdAndresourceIdAndProjectCode(
				resourceId, startDate, endDate, projectCode, empId);
		return ResponseEntity.ok(timesheetDTO);
	}
	
	@PostMapping("/submit")
	public ResponseEntity<String> submitTimesheet(@RequestBody List<TimesheetDTO> timesheetDTOs) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		timesheetService.submitTimesheet(timesheetDTOs, empId);
		return ResponseEntity.ok("Timesheet submitted successfully");
	}
	
	@PostMapping("/approval")
	public ResponseEntity<String> approveTimesheet(@RequestBody TimesheetApprovalDTO timesheetApprovalDTO) {
		timesheetService.approval(timesheetApprovalDTO);
		return ResponseEntity.ok("Timesheet approval processed successfully");
	}
	

}
