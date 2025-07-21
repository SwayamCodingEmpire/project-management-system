package com.cozentus.pms.serviceImpl;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ProjectDetailsForTimesheetDTO;
import com.cozentus.pms.dto.ProjectManagerDetailsForTImesheetEmailDTO;
import com.cozentus.pms.dto.ResourceDetailsForTimesheetDTO;
import com.cozentus.pms.dto.TimesheetSubmissionEmailDTO;
import com.cozentus.pms.dto.TimesheetSummaryToDMAndPMDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class SingleEmailServiceImpl {
	private final JavaMailSender mailSender;
	@Value("${spring.mail.username}")
	private String senderEmail;
	
	public SingleEmailServiceImpl(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	@Async
	public CompletableFuture<Void> sendSingleTimesheetEmail(
	        TimesheetSubmissionEmailDTO dto,
	        String resourceEmail,
	        String resourceId,
	        String resourcePhoneNo,
	        String resourceName,
	        LocalDate startDate,
	        LocalDate endDate
	) throws MessagingException {

	    MimeMessage message = mailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	    helper.setFrom(senderEmail);
	    helper.setTo(dto.managerEmail());
	    helper.setSubject("Timesheet Submitted for Approval: " + dto.projectName());

	    String approvalLink = "https://localhost:4200/project-manager/approve-timesheet?projectCode=" + dto.projectCode();

	    String emailContent = "<html><body>" +
	        "<p>Dear " + dto.managerName() + ",</p>" +
	        "<p>The following resource has submitted their timesheet for approval:</p>" +
	        "<ul>" +
	        "<li><b>Employee ID:</b> " + resourceId + "</li>" +
	        "<li><b>Employee Name:</b> " + resourceName + "</li>" +
	        "<li><b>Employee Email:</b> " + resourceEmail + "</li>" +
	        "<li><b>Phone No:</b> " + resourcePhoneNo + "</li>" +
	        "<li><b>Project Code:</b> " + dto.projectCode() + "</li>" +
	        "<li><b>Project Name:</b> " + dto.projectName() + "</li>" +
	        "<li><b>Timesheet Period:</b> " + startDate + " to " + endDate + "</li>" +
	        "</ul>" +
	        "<p><a href='" + approvalLink + "' style='color:blue; font-weight:bold;'>Go to Timesheet Approval Page</a></p>" +
	        "<p>Best Regards,<br>Project Management System</p>" +
	        "</body></html>";

	    helper.setText(emailContent, true);
	    mailSender.send(message);
	    return CompletableFuture.completedFuture(null);
	}
	
	@Async
	public CompletableFuture<Void> sendTimesheetSummaryToPM(ProjectManagerDetailsForTImesheetEmailDTO pmDto) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setFrom(senderEmail);
	        helper.setTo(pmDto.projectManagerEmail());
	        helper.setSubject("Weekly Timesheet Summary - Projects You Manage");

	        StringBuilder body = new StringBuilder();
	        body.append("<html><body>")
	            .append("<p>Dear ").append(pmDto.projectManagerName()).append(",</p>")
	            .append("<p>Below is the weekly timesheet summary of the resources in your project(s):</p>");

	        for (ProjectDetailsForTimesheetDTO project : pmDto.projectDetailsForTimesheetDTO()) {
	            body.append("<h4>Project: ").append(project.projectName())
	                .append(" (").append(project.projectCode()).append(")</h4>")
	                .append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse;'>")
	                .append("<thead><tr>")
	                .append("<th>Resource Name</th>")
	                .append("<th>Employee ID</th>")
	                .append("<th>Email</th>")
	                .append("</tr></thead><tbody>");

	            for (ResourceDetailsForTimesheetDTO resource : project.resourceDetailsForTimesheetDTO()) {
	                body.append("<tr>")
	                    .append("<td>").append(resource.resourceName()).append("</td>")
	                    .append("<td>").append(resource.resourceId()).append("</td>")
	                    .append("<td>").append(resource.resourceEmail()).append("</td>")
	                    .append("</tr>");
	            }

	            body.append("</tbody></table><br/>");
	        }

	        body.append("<p>Best regards,<br/>Project Management System</p>")
	            .append("</body></html>");

	        helper.setText(body.toString(), true);
	        mailSender.send(message);

	        log.info("Timesheet summary email sent to PM: {}", pmDto.projectManagerEmail());

	    } catch (Exception e) {
	        log.error("Failed to send timesheet summary email to PM: " + pmDto.projectManagerEmail(), e);
	    }

	    return CompletableFuture.completedFuture(null);
	}

	
	
	@Async
	public CompletableFuture<Void> sendTimesheetSummaryToDM(TimesheetSummaryToDMAndPMDTO dmDto) {
	    try {
	        MimeMessage message = mailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setFrom(senderEmail);
	        helper.setTo(dmDto.deliveryManagerEmail());
	        helper.setSubject("Weekly Timesheet Summary - Projects under Your Supervision");

	        StringBuilder body = new StringBuilder();
	        body.append("<html><body>")
	            .append("<p>Dear ").append(dmDto.deliverymanagerName()).append(",</p>")
	            .append("<p>Below is the weekly timesheet summary of projects and resources under your supervision:</p>");

	        for (ProjectManagerDetailsForTImesheetEmailDTO pmDto : dmDto.projectManagerDetailsForTImesheetEmailDTO()) {
	            body.append("<h4>Project Manager: ").append(pmDto.projectManagerName()).append("</h4>");

	            for (ProjectDetailsForTimesheetDTO project : pmDto.projectDetailsForTimesheetDTO()) {
	                body.append("<p><b>Project:</b> ").append(project.projectName())
	                    .append(" (").append(project.projectCode()).append(")</p>")
	                    .append("<table border='1' cellpadding='6' cellspacing='0' style='border-collapse: collapse;'>")
	                    .append("<thead><tr>")
	                    .append("<th>Resource Name</th>")
	                    .append("<th>Employee ID</th>")
	                    .append("<th>Email</th>")
	                    .append("</tr></thead><tbody>");

	                for (ResourceDetailsForTimesheetDTO resource : project.resourceDetailsForTimesheetDTO()) {
	                    body.append("<tr>")
	                        .append("<td>").append(resource.resourceName()).append("</td>")
	                        .append("<td>").append(resource.resourceId()).append("</td>")
	                        .append("<td>").append(resource.resourceEmail()).append("</td>")
	                        .append("</tr>");
	                }

	                body.append("</tbody></table><br/>");
	            }
	        }

	        body.append("<p>Best regards,<br/>Project Management System</p></body></html>");

	        helper.setText(body.toString(), true);
	        mailSender.send(message);

	        log.info("Timesheet summary email sent to DM: {}", dmDto.deliveryManagerEmail());

	    } catch (Exception e) {
	        log.error("Failed to send timesheet summary email to DM: " + dmDto.deliveryManagerEmail(), e);
	    }

	    return CompletableFuture.completedFuture(null);
	}


	

}
