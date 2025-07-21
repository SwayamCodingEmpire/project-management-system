package com.cozentus.pms.serviceImpl;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ResourceAllocationSummaryDTO;
import com.cozentus.pms.dto.ResourceDetailsDTO;
import com.cozentus.pms.dto.TimesheetSubmissionEmailDTO;
import com.cozentus.pms.dto.UserProjectTimesheetReminderDTO;
import com.cozentus.pms.helpers.ApprovalStatus;
import com.cozentus.pms.services.EmailService;

import jakarta.mail.MessagingException;
@Service
public class EmailServiceImpl implements EmailService {
	private final SingleEmailServiceImpl singleEmailService;
	private final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String senderEmail;
	
	public EmailServiceImpl(JavaMailSender mailSender, SingleEmailServiceImpl singleEmailService) {
		this.mailSender = mailSender;
		this.singleEmailService = singleEmailService;
	}
	@Async
	public CompletableFuture<Void> sendProjectCreationEmailToManager(String managerEmail, ProjectDTO projectDTO, String managerName) throws MessagingException, IOException {
//	    logger.info("Sending project creation email to manager");
//
//	    MimeMessage message = mailSender.createMimeMessage();
//	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	    // Email metadata
//	    helper.setFrom(senderEmail);
//	    helper.setTo(managerEmail);
//	    helper.setSubject("New Project Assigned: " + projectDTO.projectInfo().name());
//
//	    // Data from DTOs
//	    ProjectDetailsDTO project = projectDTO.projectInfo();
//	    ClientDTO client = projectDTO.customerInfo();
//
//	    // Link to resource allocation page
//	    String allocationLink = "https://localhost:4200/project-manager/allocate-resource?projectCode=" + project.code();
//
//	    // Email HTML content
//	    String emailContent = "<html><body>" +
//	            "<p>Dear " +  managerName + ",</p>" +
//
//	            "<p>You have been assigned a new project. Here are the details:</p>" +
//
//	            "<h3 style='color:darkblue;'>Project Information</h3>" +
//	            "<ul>" +
//	            "<li><b>Code:</b> " + project.code() + "</li>" +
//	            "<li><b>Name:</b> " + project.name() + "</li>" +
//	            "<li><b>Description:</b> " + project.description() + "</li>" +
//	            "<li><b>Start Date:</b> " + project.startDate() + "</li>" +
//	            "<li><b>End Date:</b> " + project.endDate() + "</li>" +
//	            "<li><b>Currency:</b> " + project.currency() + "</li>" +
//	            "<li><b>Contract Type:</b> " + project.contractType() + "</li>" +
//	            "<li><b>Billing Frequency:</b> " + project.billingFrequency() + "</li>" +
//	            "</ul>" +
//
//	            "<h3 style='color:darkgreen;'>Client Information</h3>" +
//	            "<ul>" +
//	            "<li><b>Name:</b> " + client.name() + "</li>" +
//	            "<li><b>Legal Entity:</b> " + client.legalEntity() + "</li>" +
//	            "<li><b>Business Unit:</b> " + client.businessUnit() + "</li>" +
//	            "</ul>" +
//
//	            "<p>Please click the link below to proceed with resource allocation:</p>" +
//	            "<p><a href='" + allocationLink + "' style='color:blue; font-weight:bold;'>Go to Resource Allocation Page</a></p>" +
//
//	            "<p>Best Regards,<br>Project Management System</p>" +
//	            "</body></html>";
//
//	    helper.setText(emailContent, true);
//
//	    // Send the email
//	    mailSender.send(message);
	    return CompletableFuture.completedFuture(null);
	}
	
	@Async
	public CompletableFuture<Void> sendProjectEditEmailToManager(String managerEmail, ProjectDTO projectDTO, String managerName) throws MessagingException, IOException {
//	    logger.info("Sending project edit notification email to manager");
//
//	    MimeMessage message = mailSender.createMimeMessage();
//	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	    // Email metadata
//	    helper.setFrom(senderEmail);
//	    helper.setTo(managerEmail);
//	    helper.setSubject("Project Updated: " + projectDTO.projectInfo().name());
//
//	    // Data from DTOs
//	    ProjectDetailsDTO project = projectDTO.projectInfo();
//	    ClientDTO client = projectDTO.customerInfo();
//
//	    // Link to resource allocation page
//	    String allocationLink = "https://localhost:4200/project-manager/allocate-resource?projectCode=" + project.code();
//
//	    // Email HTML content
//	    String emailContent = "<html><body>" +
//	            "<p>Dear " + managerName + ",</p>" +
//
//	            "<p>The following project assigned to you has been <b>updated</b>. Please review the revised details below:</p>" +
//
//	            "<h3 style='color:darkblue;'>Updated Project Information</h3>" +
//	            "<ul>" +
//	            "<li><b>Code:</b> " + project.code() + "</li>" +
//	            "<li><b>Name:</b> " + project.name() + "</li>" +
//	            "<li><b>Description:</b> " + project.description() + "</li>" +
//	            "<li><b>Start Date:</b> " + project.startDate() + "</li>" +
//	            "<li><b>End Date:</b> " + project.endDate() + "</li>" +
//	            "<li><b>Currency:</b> " + project.currency() + "</li>" +
//	            "<li><b>Contract Type:</b> " + project.contractType() + "</li>" +
//	            "<li><b>Billing Frequency:</b> " + project.billingFrequency() + "</li>" +
//	            "</ul>" +
//
//	            "<h3 style='color:darkgreen;'>Client Information</h3>" +
//	            "<ul>" +
//	            "<li><b>Name:</b> " + client.name() + "</li>" +
//	            "<li><b>Legal Entity:</b> " + client.legalEntity() + "</li>" +
//	            "<li><b>Business Unit:</b> " + client.businessUnit() + "</li>" +
//	            "</ul>" +
//
//	            "<p>You can proceed with any required changes in resource allocation using the link below:</p>" +
//	            "<p><a href='" + allocationLink + "' style='color:blue; font-weight:bold;'>Go to Resource Allocation Page</a></p>" +
//
//	            "<p>Best Regards,<br>Project Management System</p>" +
//	            "</body></html>";
//
//	    helper.setText(emailContent, true);
//
//	    // Send the email
//	    mailSender.send(message);
	    return CompletableFuture.completedFuture(null);
	}
	
	@Async
	public CompletableFuture<Void> sendAllocationSummaryToResources(
			List<ResourceAllocationSummaryDTO> resourceAllocationSummaryDTOs, String managerEmail, String managerId, String managerName, String managerPhone
	) throws MessagingException, IOException {

//	    logger.info("Sending allocation summary email to allocated resources...");
//
//	    MimeMessage message = mailSender.createMimeMessage();
//	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	    // Email metadata
//	    helper.setFrom(senderEmail);
////	    List<String> emails = resourceAllocationSummaryDTOs.stream()
////	            .map(r -> r.empEmail())
////	            .collect(Collectors.toList());
//	    
//	    List<String> emails = List.of("sjfbsk@cozentus.com", "johnDoe@cozentus.com");
//	    emails.add(managerEmail);
//	    helper.setBcc(emails.toArray(new String[0]));
//	    helper.setSubject("Resource Allocation for " + resourceAllocationSummaryDTOs.get(0).projectName());
//
//	    // Build the resource table
//	    StringBuilder table = new StringBuilder();
//	    table.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse; font-family: Arial, sans-serif; font-size: 14px;'>")
//	         .append("<thead style='background-color: #f0f0f0;'>")
//	         .append("<tr>")
//	         .append("<th>Name</th>")
//	         .append("<th>Primary Skill</th>")
//	         .append("<th>Secondary Skill</th>")
//	         .append("<th>Allocation Start Date</th>")
//	         .append("<th>Allocation End Date</th>")
//	         .append("<th>Designation</th>")
//	         .append("<th>Years of Exp</th>")
//	         .append("<th>Billability %</th>")
//	         .append("<th>Planned Utilization %</th>")
//	         .append("</tr>")
//	         .append("</thead><tbody>");
//
//	    for (ResourceAllocationSummaryDTO r : resourceAllocationSummaryDTOs) {
//	        table.append("<tr>")
//	             .append("<td>").append(r.empName()).append("</td>")
//	             .append("<td>").append(r.primarySkill()).append("</td>")
//	             .append("<td>").append(r.secondarySkill()).append("</td>")
//	             .append("<td>").append(r.allocationStartDate()).append("</td>")
//	             .append("<td>").append(r.allocationEndDate()).append("</td>")
//	             .append("<td>").append(r.designation()).append("</td>")
//	             .append("<td>").append(r.yearsOfExperience()).append("</td>")
//	             .append("<td>").append(r.billabilityPercentage()).append("</td>")
//	             .append("<td>").append(r.plannedUtilizationPercentage()).append("</td>")
//	             .append("</tr>");
//	    }
//
//	    table.append("</tbody></table>");
//
//	    // Compose email content
//	    String content = "<html><body>" +
//	        "<p>Dear Team,</p>" +
//	        "<p>You have been allocated to the following project:</p>" +
//	        "<ul>" +
//	        "<li><b>Project Name:</b> " + resourceAllocationSummaryDTOs.get(0).projectName() + "</li>" +
//	        "<li><b>Project Manager:</b> " + managerName + "</li>" +
//	        "<li><b>Manager Email:</b> <a href='mailto:" + managerName + "'>" + managerName + "</a></li>" +
//	        "<li><b>Contact No:</b> " + managerPhone + "</li>" +
//	        "</ul>" +
//	        "<p><b>Resource Allocation Details:</b></p>" +
//	        table +
//	        "<p>This is a system-generated email. For any queries, please contact your project manager.</p>" +
//	        "<p>Regards,<br/>Project Management System</p>" +
//	        "</body></html>";
//
//	    helper.setText(content, true);
//	    mailSender.send(message);
//
	    return CompletableFuture.completedFuture(null);
	}

	
	@Async
	@Override
	public CompletableFuture<Void> sendTimesheetSubmissionEmailToManager(
	        List<TimesheetSubmissionEmailDTO> timesheetDTOs,
	        String resourceEmail,
	        String resourceId,
	        String resourcePhoneNo,
	        String resourceName,
	        LocalDate startDate,
	        LocalDate endDate
	) {

//	    logger.info("Sending timesheet submission emails to managers...");
//
//	    // Send one email per manager
//	    timesheetDTOs.forEach(dto -> {
//	        try {
//	           singleEmailService.sendSingleTimesheetEmail(
//	                dto,
//	                resourceEmail,
//	                resourceId,
//	                resourcePhoneNo,
//	                resourceName,
//	                startDate,
//	                endDate
//	            );
//	        } catch (Exception e) {
//	            logger.error("Failed to send email to manager: " + dto.managerEmail(), e);
//	        }
//	    });

	    return CompletableFuture.completedFuture(null);
	}
	
	@Async
	public CompletableFuture<Void> sendTimesheetApprovalEmailToResource(
	        ResourceDetailsDTO resourceNameAndProjectNameDTO,
	        String resourceId,
	        LocalDate startDate,
	        LocalDate endDate,
	        String projectCode,
	        ApprovalStatus approvalStatus,
	        String managerName,
	        String managerEmail,
	        String managerPhoneNo
	) throws MessagingException {

//	    logger.info("Sending timesheet " + approvalStatus + " email to resource: " + resourceId);
//
//	    MimeMessage message = mailSender.createMimeMessage();
//	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	    // Replace with your official sender email
//	    helper.setFrom(senderEmail);
//
//	    // Assuming resource email is part of the DTO or another parameter
//	    // If not, extend DTO or method to include it
//	    String resourceEmail = resourceNameAndProjectNameDTO.resourceEmail(); // If not available, pass as separate param
//	    helper.setTo(resourceEmail);
//
//	    String subject = "Timesheet " + approvalStatus.name() + ": " + resourceNameAndProjectNameDTO.projectName();
//	    helper.setSubject(subject);
//
//	    // Approval status style
//	    String statusLabel = approvalStatus == ApprovalStatus.APPROVED ? "Approved ✅" : "Rejected ❌";
//	    String statusColor = approvalStatus == ApprovalStatus.APPROVED ? "green" : "red";
//
//	    // Compose HTML content
//	    String emailContent = "<html><body>" +
//	            "<p>Dear " + resourceNameAndProjectNameDTO.resourceName() + ",</p>" +
//	            "<p>Your timesheet for the following has been <b style='color:" + statusColor + ";'>" +
//	            statusLabel + "</b>:</p>" +
//
//	            "<ul>" +
//	            "<li><b>Project Name:</b> " + resourceNameAndProjectNameDTO.projectName() + "</li>" +
//	            "<li><b>Project Code:</b> " + projectCode + "</li>" +
//	            "<li><b>Timesheet Period:</b> " + startDate + " to " + endDate + "</li>" +
//	            "<li><b>Employee ID:</b> " + resourceId + "</li>" +
//	            "</ul>" +
//
//	            "<p><b>Manager Contact:</b></p>" +
//	            "<ul>" +
//	            "<li><b>Name:</b> " + managerName + "</li>" +
//	            "<li><b>Email:</b> <a href='mailto:" + managerEmail + "'>" + managerEmail + "</a></li>" +
//	            "<li><b>Phone:</b> " + managerPhoneNo + "</li>" +
//	            "</ul>" +
//
//	            "<p>Please reach out to your manager for any clarification.</p>" +
//	            "<p>Best regards,<br/>Project Management System</p>" +
//	            "</body></html>";
//
//	    helper.setText(emailContent, true);
//	    mailSender.send(message);

	    return CompletableFuture.completedFuture(null);
	}
	
	@Override
	@Async
	public CompletableFuture<Void> sendTimesheetReminderEmailToResource(
	        String empEmail,
	        String empName,
	        List<UserProjectTimesheetReminderDTO> projects
	) {
//	    try {
//	        MimeMessage message = mailSender.createMimeMessage();
//	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	        helper.setFrom(senderEmail);
//	        helper.setTo(empEmail);
//	        helper.setSubject("Reminder: Submit Your Timesheet");
//
//	        StringBuilder projectList = new StringBuilder();
//	        projectList.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse; font-family: Arial, sans-serif;'>")
//	                .append("<thead><tr>")
//	                .append("<th>Project Code</th><th>Project Name</th><th>Manager</th><th>Email</th><th>Phone</th>")
//	                .append("</tr></thead><tbody>");
//
//	        for (UserProjectTimesheetReminderDTO dto : projects) {
//	            projectList.append("<tr>")
//	                    .append("<td>").append(dto.projectCode()).append("</td>")
//	                    .append("<td>").append(dto.projectName()).append("</td>")
//	                    .append("<td>").append(dto.managerName()).append("</td>")
//	                    .append("<td><a href='mailto:").append(dto.managerEmail()).append("'>").append(dto.managerEmail()).append("</a></td>")
//	                    .append("<td>").append(dto.managerPhoneNo()).append("</td>")
//	                    .append("</tr>");
//	        }
//
//	        projectList.append("</tbody></table>");
//
//	        String body = "<html><body>" +
//	                "<p>Dear " + empName + ",</p>" +
//	                "<p>This is a reminder to submit your timesheet for the following project(s):</p>" +
//	                projectList +
//	                "<p>The submission deadline is approaching. Kindly ensure your timesheet is filled and submitted on time to avoid escalation.</p>" +
//	                "<p>Best regards,<br/>Project Management System</p>" +
//	                "</body></html>";
//
//	        helper.setText(body, true);
//	        mailSender.send(message);
//	        logger.info("Reminder email sent to " + empEmail);
//
//	    } catch (Exception e) {
//	        logger.error("Failed to send reminder email to " + empEmail, e);
//	    }

	    return CompletableFuture.completedFuture(null);
	}
	
	@Override
	@Async
	public CompletableFuture<Void> sendTimesheetWarning1EmailToResource(
	        String empEmail,
	        String empName,
	        List<UserProjectTimesheetReminderDTO> projects
	) {
//	    try {
//	        MimeMessage message = mailSender.createMimeMessage();
//	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	        helper.setFrom(senderEmail);
//	        helper.setTo(empEmail);
//	        helper.setSubject("⚠️ Timesheet Missing for Assigned Project(s)");
//
//	        StringBuilder projectList = new StringBuilder();
//	        projectList.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse; font-family: Arial, sans-serif;'>")
//	                .append("<thead><tr>")
//	                .append("<th>Project Code</th><th>Project Name</th><th>Manager</th><th>Email</th><th>Phone</th>")
//	                .append("</tr></thead><tbody>");
//
//	        for (UserProjectTimesheetReminderDTO dto : projects) {
//	            projectList.append("<tr>")
//	                    .append("<td>").append(dto.projectCode()).append("</td>")
//	                    .append("<td>").append(dto.projectName()).append("</td>")
//	                    .append("<td>").append(dto.managerName()).append("</td>")
//	                    .append("<td><a href='mailto:").append(dto.managerEmail()).append("'>").append(dto.managerEmail()).append("</a></td>")
//	                    .append("<td>").append(dto.managerPhoneNo()).append("</td>")
//	                    .append("</tr>");
//	        }
//
//	        projectList.append("</tbody></table>");
//
//	        String body = "<html><body>" +
//	                "<p>Dear " + empName + ",</p>" +
//	                "<p><b>This is a warning notification.</b></p>" +
//	                "<p>Our records indicate that you have not submitted your timesheet for the following project(s):</p>" +
//	                projectList +
//	                "<p>Please complete your timesheet submissions <b>as soon as possible</b> to avoid escalation or compliance flags.</p>" +
//	                "<p>If you have already submitted but are seeing this message in error, please contact your project manager.</p>" +
//	                "<p>Regards,<br/>Project Management System</p>" +
//	                "</body></html>";
//
//	        helper.setText(body, true);
//	        mailSender.send(message);
//	        logger.info("Timesheet warning email sent to " + empEmail);
//
//	    } catch (Exception e) {
//	        logger.error("Failed to send timesheet warning email to " + empEmail, e);
//	    }

	    return CompletableFuture.completedFuture(null);
	}
	
	
	
	
	@Override
	@Async
	public CompletableFuture<Void> sendTimesheetWarning2EmailToResource(
	        String empEmail,
	        String empName,
	        List<UserProjectTimesheetReminderDTO> projects
	) {
//	    try {
//	        MimeMessage message = mailSender.createMimeMessage();
//	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//	        helper.setFrom(senderEmail);
//	        helper.setTo(empEmail);
//	        helper.setSubject("⚠️ Timesheet Missing for Assigned Project(s)");
//
//	        StringBuilder projectList = new StringBuilder();
//	        projectList.append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse; font-family: Arial, sans-serif;'>")
//	                .append("<thead><tr>")
//	                .append("<th>Project Code</th><th>Project Name</th><th>Manager</th><th>Email</th><th>Phone</th>")
//	                .append("</tr></thead><tbody>");
//
//	        for (UserProjectTimesheetReminderDTO dto : projects) {
//	            projectList.append("<tr>")
//	                    .append("<td>").append(dto.projectCode()).append("</td>")
//	                    .append("<td>").append(dto.projectName()).append("</td>")
//	                    .append("<td>").append(dto.managerName()).append("</td>")
//	                    .append("<td><a href='mailto:").append(dto.managerEmail()).append("'>").append(dto.managerEmail()).append("</a></td>")
//	                    .append("<td>").append(dto.managerPhoneNo()).append("</td>")
//	                    .append("</tr>");
//	        }
//
//	        projectList.append("</tbody></table>");
//
//	        String body = "<html><body>" +
//	                "<p>Dear " + empName + ",</p>" +
//	                "<p><b>This is a warning notification.</b></p>" +
//	                "<p>Our records indicate that you have not submitted your timesheet for the following project(s):</p>" +
//	                projectList +
//	                "<p>Please complete your timesheet submissions <b>as soon as possible</b> to avoid escalation or compliance flags.</p>" +
//	                "<p>If you have already submitted but are seeing this message in error, please contact your project manager.</p>" +
//	                "<p>Regards,<br/>Project Management System</p>" +
//	                "</body></html>";
//
//	        helper.setText(body, true);
//	        mailSender.send(message);
//	        logger.info("Timesheet warning email sent to " + empEmail);
//
//	    } catch (Exception e) {
//	        logger.error("Failed to send timesheet warning email to " + empEmail, e);
//	    }

	    return CompletableFuture.completedFuture(null);
	}



	

	
	





}
