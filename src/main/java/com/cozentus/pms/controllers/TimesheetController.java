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
import com.cozentus.pms.services.TimesheetService;
@RestController
@RequestMapping("/timesheet")
public class TimesheetController {
	private final TimesheetService timesheetService;
	
	public TimesheetController(TimesheetService timesheetService) {
		this.timesheetService = timesheetService;
	}
	
	@GetMapping
	public ResponseEntity<List<TimesheetDTO>> getTimesheetByEmpId(@RequestParam
			LocalDate startDate, 
			@RequestParam
			LocalDate endDate) {
		List<TimesheetDTO> timesheetDTOs = timesheetService.getTimeSheetByEmpId("CZ0294", startDate, endDate);
		return ResponseEntity.ok(timesheetDTOs);
	}
	
	@PostMapping
	public ResponseEntity<String> createTimesheet(@RequestBody SingularTimesheetPayload singularTimesheetPayload) {
		timesheetService.saveTimesheet(singularTimesheetPayload, "CZ0294");
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
		List<TimesheetSummaryDTO> timesheetSummary = timesheetService.getTimeSheetSummaryByManagerId("CZ0462", startDate, endDate);
		return ResponseEntity.ok(timesheetSummary);
	}
	
	@GetMapping("/resource/{resourceId}/project/{projectCode}")
	public ResponseEntity<TimesheetDTO> getTimesheetByManagerIdAndResourceIdAndProjectCode(
			@PathVariable String resourceId,
			@RequestParam LocalDate startDate,
			@RequestParam LocalDate endDate,
			@PathVariable String projectCode) {
		TimesheetDTO timesheetDTO = timesheetService.getTimeSheetByManagerIdAndresourceIdAndProjectCode(
				resourceId, startDate, endDate, projectCode, "CZ0462");
		return ResponseEntity.ok(timesheetDTO);
	}
	
	@PostMapping("/submit")
	public ResponseEntity<String> submitTimesheet(@RequestBody List<TimesheetDTO> timesheetDTOs) {
		timesheetService.submitTimesheet(timesheetDTOs, "CZ0294");
		return ResponseEntity.ok("Timesheet submitted successfully");
	}
	
	@PostMapping("/approval")
	public ResponseEntity<String> approveTimesheet(@RequestBody TimesheetApprovalDTO timesheetApprovalDTO) {
		timesheetService.approval(timesheetApprovalDTO);
		return ResponseEntity.ok("Timesheet approval processed successfully");
	}
	

}
