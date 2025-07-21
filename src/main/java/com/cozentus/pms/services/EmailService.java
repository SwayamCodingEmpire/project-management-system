package com.cozentus.pms.services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ResourceAllocationSummaryDTO;
import com.cozentus.pms.dto.ResourceDetailsDTO;
import com.cozentus.pms.dto.TimesheetSubmissionEmailDTO;
import com.cozentus.pms.dto.UserProjectTimesheetReminderDTO;
import com.cozentus.pms.helpers.ApprovalStatus;

import jakarta.mail.MessagingException;

public interface EmailService {
	CompletableFuture<Void> sendProjectCreationEmailToManager(String managerEmail, ProjectDTO projectDTO, String managerName) throws MessagingException, IOException;
	CompletableFuture<Void> sendProjectEditEmailToManager(String managerEmail, ProjectDTO projectDTO, String managerName) throws MessagingException, IOException;

	
	CompletableFuture<Void> sendAllocationSummaryToResources(
			List<ResourceAllocationSummaryDTO> resourceAllocationSummaryDTOs, String managerEmail, String managerId, String managerName, String managerPhone
	) throws MessagingException, IOException;
	
	CompletableFuture<Void> sendTimesheetSubmissionEmailToManager(
	        List<TimesheetSubmissionEmailDTO> timesheetDTOs,
	        String resourceEmail,
	        String resourceId,
	        String resourcePhoneNo,
	        String resourceName,
	        LocalDate startDate,
	        LocalDate endDate
	);
	
	CompletableFuture<Void> sendTimesheetApprovalEmailToResource(
	        ResourceDetailsDTO resourceNameAndProjectNameDTO,
	        String resourceId,
	        LocalDate startDate,
	        LocalDate endDate,
	        String projectCode,
	        ApprovalStatus approvalStatus,
	        String managerName,
	        String managerEmail,
	        String managerPhoneNo
	) throws MessagingException;
	
	 CompletableFuture<Void> sendTimesheetReminderEmailToResource(
		        String empEmail,
		        String empName,
		        List<UserProjectTimesheetReminderDTO> projects
		);
	 
	 public CompletableFuture<Void> sendTimesheetWarning1EmailToResource(
		        String empEmail,
		        String empName,
		        List<UserProjectTimesheetReminderDTO> projects
		);
	 
	 public CompletableFuture<Void> sendTimesheetWarning2EmailToResource(
		        String empEmail,
		        String empName,
		        List<UserProjectTimesheetReminderDTO> projects
		);
	 
	 
	 

}
