package com.cozentus.pms.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.DateRange;
import com.cozentus.pms.dto.DayWiseTimesheet;
import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.ProjectTimeSheetDTO;
import com.cozentus.pms.dto.ResourceDetailsDTO;
import com.cozentus.pms.dto.SingularTimesheetPayload;
import com.cozentus.pms.dto.TimesheetApprovalDTO;
import com.cozentus.pms.dto.TimesheetDTO;
import com.cozentus.pms.dto.TimesheetFlatDTO;
import com.cozentus.pms.dto.TimesheetForManagerFlatDTO;
import com.cozentus.pms.dto.TimesheetSubmissionEmailDTO;
import com.cozentus.pms.dto.TimesheetSummaryDTO;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.entites.TimeSheet;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.ApprovalStatus;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.TimeSheetRepository;
import com.cozentus.pms.services.EmailService;
import com.cozentus.pms.services.TimesheetService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TimesheetServiceImpl implements TimesheetService {
	private final TimeSheetRepository timeSheetRepository;
	private final ResourceAllocationRepository resourceAllocationRepository;
	private final ProjectDetailsRepository projectDetailsRepository;	
	private final EmailService emailService;
	@PersistenceContext
	private EntityManager entityManager;

	public TimesheetServiceImpl(TimeSheetRepository timeSheetRepository,
			ResourceAllocationRepository resourceAllocationRepository, EmailService emailService, ProjectDetailsRepository projectDetailsRepository) {
		this.timeSheetRepository = timeSheetRepository;
		this.resourceAllocationRepository = resourceAllocationRepository;
		this.emailService = emailService;
		this.projectDetailsRepository = projectDetailsRepository;
	}

	public List<TimesheetDTO> getTimeSheetByEmpId(String resourceId, LocalDate startDate, LocalDate endDate) {
		List<TimesheetFlatDTO> timesheetFlatDTOs = timeSheetRepository.findAllTimesheetByEmpIdAndDateBetween(resourceId,
				startDate, endDate, null);
		log.info("Fetched {} timesheets for empId: {} between {} and {}", timesheetFlatDTOs, resourceId, startDate,
				endDate);
		return timesheetFlatDTOs.stream()
				.collect(Collectors.groupingBy(ts -> new AbstractMap.SimpleEntry<>(ts.projectCode(), ts.projectName())))
				.entrySet().stream().map(entry -> {
					String projectCode = entry.getKey().getKey();
					String projectName = entry.getKey().getValue();
					List<ProjectTimeSheetDTO> timeSheets = entry.getValue().stream()
							.map(ts -> new ProjectTimeSheetDTO(ts.projectTimeSheet().date(), ts.projectTimeSheet().approvalStatus(),
									ts.projectTimeSheet().attendanceStatus(),
									ts.projectTimeSheet().hoursWorked() /* , etc */))
							.collect(Collectors.toList());
					return new TimesheetDTO(projectCode, projectName, timeSheets);
				}).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void saveTimesheet(SingularTimesheetPayload singularTimesheetPayload, String resourceId) {
		log.info("Saving timesheet for resource: {}", resourceId);
		log.info("Timesheet payload: {}", singularTimesheetPayload);
		List<IdAndCodeDTO> allocationIdsForResourceAndProjects = resourceAllocationRepository
				.fetchAllocationsIdForProjectCodeAndResourceId(
						singularTimesheetPayload.dayTimeSheet().stream().map(DayWiseTimesheet::projectCode).toList(),
						resourceId);

		log.info("Allocation IDs for resource and projects: {}", allocationIdsForResourceAndProjects);
		Map<Integer, String> idToCodeMap = allocationIdsForResourceAndProjects.stream()
				.collect(Collectors.toMap(IdAndCodeDTO::id, IdAndCodeDTO::code));
		log.info("ID to Code Map: {}", idToCodeMap);
		Map<String, DayWiseTimesheet> projectCodeToDayWiseTimesheetMap = singularTimesheetPayload.dayTimeSheet()
				.stream().collect(Collectors.toMap(DayWiseTimesheet::projectCode, dayWise -> dayWise));

		LocalDate timesheetDate = singularTimesheetPayload.date();
		log.info("Today's date for timesheet: {}", timesheetDate);
		List<TimeSheet> existingTimeSheets = timeSheetRepository.findByResourceAllocation_IdInAndDateAndEnabledTrue(
				allocationIdsForResourceAndProjects.stream().map(IdAndCodeDTO::id).toList(), timesheetDate);
		if (!existingTimeSheets.isEmpty()) {
			log.info("Found existing timesheets for date: {}", timesheetDate);
			for (TimeSheet existingTimeSheet : existingTimeSheets) {
				log.info("Found existing timesheet for resource allocation ID: {}",
						existingTimeSheet.getResourceAllocation().getId());
				String projectCode = idToCodeMap.get(existingTimeSheet.getResourceAllocation().getId());
				log.info("Project code for existing timesheet: {}", projectCode);
				existingTimeSheet.setEnabled(true);
				existingTimeSheet.setApprovalStatus(ApprovalStatus.PENDING);
				existingTimeSheet.setApproval(false);
				existingTimeSheet.setUpdatedBy(resourceId);
				log.info("Updating existing timesheet with project code: {}", projectCode);
				if (!projectCodeToDayWiseTimesheetMap.containsKey(projectCode)) {
					log.info("Project code {} not found in the new timesheet payload, setting hours to zero",
							projectCode);
				} else {
					log.info("Updating existing timesheet with hours worked: {}",
							projectCodeToDayWiseTimesheetMap.get(projectCode).hoursWorked());
					existingTimeSheet.setHours(projectCodeToDayWiseTimesheetMap.get(projectCode).hoursWorked());
				}
				existingTimeSheet.setHours(projectCodeToDayWiseTimesheetMap.get(projectCode).hoursWorked());
				singularTimesheetPayload.dayTimeSheet().removeIf(dayWise -> dayWise.projectCode().equals(projectCode));
				existingTimeSheet.setUpdatedAt(LocalDateTime.now());// Reset hours to zero
				existingTimeSheet
						.setAttendanceStatus(projectCodeToDayWiseTimesheetMap.get(projectCode).attendanceStatus());

			}

		}

		List<TimeSheet> timeSheets = new ArrayList<>();
		List<DayWiseTimesheet> dayWiseTimesheets = singularTimesheetPayload.dayTimeSheet();

		timeSheets.addAll(existingTimeSheets);
		log.info("Processing {} day-wise timesheets for resource: {}", dayWiseTimesheets, resourceId);
		log.info(allocationIdsForResourceAndProjects.toString());
		for (DayWiseTimesheet dayWiseTimesheet : singularTimesheetPayload.dayTimeSheet()) {
			String projectCode = dayWiseTimesheet.projectCode();
			BigDecimal hoursWorked = dayWiseTimesheet.hoursWorked();
			IdAndCodeDTO allocationIdForProject = allocationIdsForResourceAndProjects.stream()
					.filter(allocation -> allocation.code().equals(projectCode)).findFirst().orElseThrow(
							() -> new RecordNotFoundException("No allocation found for project code: " + projectCode));
			ResourceAllocation resourceAllocation = entityManager.getReference(ResourceAllocation.class,
					allocationIdForProject.id());

			TimeSheet timeSheet = new TimeSheet();

			timeSheet.setDate(timesheetDate);
			timeSheet.setHours(hoursWorked);
			timeSheet.setResourceAllocation(resourceAllocation);
			timeSheet.setAttendanceStatus(dayWiseTimesheet.attendanceStatus());
			timeSheet.setApproval(false);
			timeSheet.setCreatedBy(resourceId);
			timeSheet.setUpdatedBy(resourceId);
			timeSheet.setEnabled(true);
			timeSheets.add(timeSheet);
			timeSheet.setApprovalStatus(ApprovalStatus.SUBMITTED);

		}
		if (!existingTimeSheets.isEmpty()) {
			timeSheets.addAll(existingTimeSheets);
		}

		timeSheetRepository.saveAllAndFlush(timeSheets);
	}

	@Override
	public List<TimesheetSummaryDTO> getTimeSheetSummaryByManagerId(String resourceId, LocalDate startDate,
			LocalDate endDate) {
		// TODO Auto-generated method stub
		return timeSheetRepository.findTimeSheetSummaryByProjectManagerIdAndDateBetween(resourceId, startDate, endDate);
	}

	@Override
	public TimesheetDTO getTimeSheetByManagerIdAndresourceIdAndProjectCode(String resourceId, LocalDate startDate,
			LocalDate endDate, String projectCode, String managerId) {
		List<TimesheetForManagerFlatDTO> timesheetFlatDTOs = timeSheetRepository
				.findAllTimesheetByEmpIdAndProjectCodeAndManagerIdDateBetween(resourceId, managerId, startDate, endDate,
						projectCode);

		log.info("Fetched {} timesheets for empId: {} between {} and {}", timesheetFlatDTOs.size(), resourceId,
				startDate, endDate);

		if (timesheetFlatDTOs.isEmpty()) {
			return new TimesheetDTO("", "", new ArrayList<>());
		}

		String projectName = timesheetFlatDTOs.get(0).projectName();
		String finalProjectCode = timesheetFlatDTOs.get(0).projectCode();

		List<ProjectTimeSheetDTO> timeSheets = timesheetFlatDTOs.stream()
				.map(ts -> new ProjectTimeSheetDTO(ts.projectTimeSheet().date(), ts.projectTimeSheet().approvalStatus(),
						ts.projectTimeSheet().attendanceStatus(), ts.projectTimeSheet().hoursWorked()))
				.collect(Collectors.toList());

		return new TimesheetDTO(finalProjectCode, projectName, timeSheets);
	}

	@Override
	@Transactional
	public void submitTimesheet(List<TimesheetDTO> timesheetDTOs, String resourceId) {
		LocalDate startDate = getStartDate(timesheetDTOs);
		LocalDate endDate = getEndDate(timesheetDTOs);

		List<IdAndCodeDTO> allocationIdsForResourceAndProjects = resourceAllocationRepository
				.fetchAllocationsIdForProjectCodeAndResourceId(
						timesheetDTOs.stream().map(TimesheetDTO::projectCode).toList(), resourceId);

		Map<Integer, String> idToCodeMap = allocationIdsForResourceAndProjects.stream()
				.collect(Collectors.toMap(IdAndCodeDTO::id, IdAndCodeDTO::code));

		Map<String, Integer> codeToIdMap = allocationIdsForResourceAndProjects.stream()
				.collect(Collectors.toMap(IdAndCodeDTO::code, IdAndCodeDTO::id));

		List<TimeSheet> existingTimeSheets = timeSheetRepository
				.findByResourceAllocation_IdInAndDateBetweenAndEnabledTrue(
						allocationIdsForResourceAndProjects.stream().map(IdAndCodeDTO::id).toList(), startDate,
						endDate);

		existingTimeSheets = existingTimeSheets.stream()
				.filter(ts -> !ts.getApprovalStatus().equals(ApprovalStatus.APPROVED))
				.collect(Collectors.toList());
		log.info("Found {} existing timesheets for resource: {} between {} and {}", existingTimeSheets.size(),
				resourceId, startDate, endDate);
		log.info(existingTimeSheets.stream().map(ts -> "Project Code: "
				+ idToCodeMap.get(ts.getResourceAllocation().getId()) + ", Date: " + ts.getDate())
				.collect(Collectors.joining(", ")));
		Map<String, Map<LocalDate, ProjectTimeSheetDTO>> timesheetMap = timesheetDTOs.stream()
				.collect(Collectors.toMap(TimesheetDTO::projectCode, dto -> dto.projectTimeSheet().stream()
						.collect(Collectors.toMap(ProjectTimeSheetDTO::date, Function.identity()))));

		if (!existingTimeSheets.isEmpty()) {
			for (TimeSheet existingTimeSheet : existingTimeSheets) {
				String projectCode = idToCodeMap.get(existingTimeSheet.getResourceAllocation().getId());
				existingTimeSheet.setEnabled(true);
				existingTimeSheet.setApprovalStatus(ApprovalStatus.PENDING);
				existingTimeSheet.setApproval(false);
				existingTimeSheet.setUpdatedBy(resourceId);
				existingTimeSheet.setUpdatedAt(LocalDateTime.now());

				if (!timesheetMap.containsKey(projectCode)
						|| !timesheetMap.get(projectCode).containsKey(existingTimeSheet.getDate())) {
					existingTimeSheet.setHours(BigDecimal.ZERO);
					existingTimeSheet.setAttendanceStatus(false);
				} else {
					ProjectTimeSheetDTO dto = timesheetMap.get(projectCode).get(existingTimeSheet.getDate());
					existingTimeSheet.setHours(dto.hoursWorked());
					existingTimeSheet.setAttendanceStatus(dto.attendanceStatus());

					// Remove matched entry so only new ones remain
					timesheetMap.get(projectCode).remove(existingTimeSheet.getDate());
				}
			}
		}

		List<TimeSheet> timeSheets = new ArrayList<>(existingTimeSheets);

		// Create new entries for remaining timesheet data
		for (Map.Entry<String, Map<LocalDate, ProjectTimeSheetDTO>> entry : timesheetMap.entrySet()) {
			String projectCode = entry.getKey();
			Integer allocationId = codeToIdMap.get(projectCode);
			ResourceAllocation allocation = entityManager.getReference(ResourceAllocation.class, allocationId);
			for (Map.Entry<LocalDate, ProjectTimeSheetDTO> innerEntry : entry.getValue().entrySet()) {
				LocalDate date = innerEntry.getKey();
				ProjectTimeSheetDTO projectTimesheetDTO = innerEntry.getValue();

				TimeSheet newTimeSheet = new TimeSheet();
				newTimeSheet.setDate(date);
				newTimeSheet.setHours(projectTimesheetDTO.hoursWorked());
				newTimeSheet.setAttendanceStatus(projectTimesheetDTO.attendanceStatus());
				newTimeSheet.setApprovalStatus(ApprovalStatus.PENDING);
				newTimeSheet.setApproval(false);
				newTimeSheet.setEnabled(true);
				newTimeSheet.setCreatedAt(LocalDateTime.now());
				newTimeSheet.setCreatedBy(resourceId);
				newTimeSheet.setUpdatedAt(LocalDateTime.now());
				newTimeSheet.setUpdatedBy(resourceId);
				newTimeSheet.setResourceAllocation(allocation);

				timeSheets.add(newTimeSheet);
			}
		}

		// Finally save all
		timeSheetRepository.saveAll(timeSheets);
		
			List<TimesheetSubmissionEmailDTO> timesheetSubmissionEmailDTO = projectDetailsRepository.findTimesheetSubmissionEmailDetailsByProjectCode(timesheetDTOs.stream()
					.map(TimesheetDTO::projectCode).distinct().toList());
			emailService.sendTimesheetSubmissionEmailToManager(
					timesheetSubmissionEmailDTO, 
					"resourceEmail@gmail.com",
					"resourceId",
					"resourcePhoneNo",
					"resource Name",
					startDate, 
					endDate);
	}
	
	@Override
	public void approval(TimesheetApprovalDTO timesheetApprovalDTO) {
		 ApprovalStatus approvalStatus = timesheetApprovalDTO.approve()? ApprovalStatus.APPROVED : ApprovalStatus.REJECTED;
		IdAndCodeDTO allocationIdWithProjectCodeDTO =  resourceAllocationRepository.fetchAllocationsIdForSingleProjectCodeAndResourceId(timesheetApprovalDTO.projectId(), timesheetApprovalDTO.resourceId());
		if (allocationIdWithProjectCodeDTO == null) {
			throw new RecordNotFoundException("No allocation found for project code: " + timesheetApprovalDTO.projectId());
		}
		String username = "ADMIN";
		int rows = timeSheetRepository.approvetimesheetByAllocationIdAndDateAndEnabledTrue(
				allocationIdWithProjectCodeDTO.id(), timesheetApprovalDTO.startDate(), timesheetApprovalDTO.endDate(),
				timesheetApprovalDTO.approve(), approvalStatus, username);
		
		if(rows == 0) {
			throw new RecordNotFoundException("No timesheet found for project code: " + timesheetApprovalDTO.projectId() + 
					" and resource id: " + timesheetApprovalDTO.resourceId() + " between " + timesheetApprovalDTO.startDate() + " and " + timesheetApprovalDTO.endDate());
		}
		
		else if(rows>0) {
			
			try {
				ResourceDetailsDTO resourceDetails = resourceAllocationRepository
						.findDetailsFromAllocationId(allocationIdWithProjectCodeDTO.id());
				
				emailService.sendTimesheetApprovalEmailToResource(resourceDetails, timesheetApprovalDTO.resourceId(), timesheetApprovalDTO.startDate(), timesheetApprovalDTO.endDate(), timesheetApprovalDTO.projectId(), approvalStatus, "managerName", "managerEmail@gmail.com", "managerPhone");
			}catch (jakarta.mail.MessagingException e) {
				log.error("Error sending timesheet approval email: {}", e.getMessage());
			}
			
		}
		
	}

	private LocalDate getStartDate(List<TimesheetDTO> timesheets) {
		return timesheets.stream().flatMap(dto -> dto.projectTimeSheet().stream()).map(ProjectTimeSheetDTO::date)
				.min(LocalDate::compareTo).orElse(null);
	}

	private LocalDate getEndDate(List<TimesheetDTO> timesheets) {
		return timesheets.stream().flatMap(dto -> dto.projectTimeSheet().stream()).map(ProjectTimeSheetDTO::date)
				.max(LocalDate::compareTo).orElse(null);
	}

	public DateRange getDateRange(List<TimesheetDTO> timesheets) {
		LocalDate start = getStartDate(timesheets);
		LocalDate end = getEndDate(timesheets);
		return (start != null && end != null) ? new DateRange(start, end) : null;
	}

	// Optional record for returning both dates

}
