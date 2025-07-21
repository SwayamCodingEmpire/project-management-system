package com.cozentus.pms.serviceImpl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ProjectDetailsForTimesheetDTO;
import com.cozentus.pms.dto.ProjectManagerDetailsForTImesheetEmailDTO;
import com.cozentus.pms.dto.ProjectTimesheetForEmailDTO;
import com.cozentus.pms.dto.ResourceDetailsForTimesheetDTO;
import com.cozentus.pms.dto.TimesheetSummaryToDMAndPMDTO;
import com.cozentus.pms.dto.UserProjectTimesheetReminderDTO;
import com.cozentus.pms.helpers.ApprovalStatus;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.EmailProcessingService;
import com.cozentus.pms.services.EmailScheduleService;
import com.cozentus.pms.services.EmailService;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class EmailSchedulerServiceImpl implements EmailScheduleService {
	private final EmailProcessingService emailProcessingService;
	private final ProjectDetailsRepository projectDetailsRepository;
	private final UserInfoRepository userInfoRepository;
	private final EmailService emailService;
	
	
		public EmailSchedulerServiceImpl(ProjectDetailsRepository projectDetailsRepository, 
				UserInfoRepository userInfoRepository, EmailService emailService, EmailProcessingService emailProcessingService) {
		this.projectDetailsRepository = projectDetailsRepository;
		this.userInfoRepository = userInfoRepository;
		this.emailService = emailService;
		this.emailProcessingService = emailProcessingService;
	}
	
//    @Scheduled(cron = "0 0 7,19 * * *")
//    public void sendReminderMailSendingSchedule() {
//        System.out.println("Running task at 7 AM or 7 PM");
//        String day = LocalDate.now().getDayOfWeek().toString(); // e.g., "MONDAY"
//        sendReminderMail(day);
//    }
//    
//    private void sendReminderMail(String day) {
//        log.info("Sending timesheet reminder emails for day: " + day);
//
//        List<UserProjectTimesheetReminderDTO> reminderDTOs = userInfoRepository
//                .findAllResourcesWithPendingAllocations(Roles.RESOURCE, day);
//
//        if (reminderDTOs.isEmpty()) {
//            log.info("No pending timesheets found for resources on " + day);
//            return;
//        }
//
//        Map<String, List<UserProjectTimesheetReminderDTO>> userProjectMap =
//                reminderDTOs.stream().collect(Collectors.groupingBy(UserProjectTimesheetReminderDTO::empId));
//
//        userProjectMap.forEach((empId, projects) -> {
//            UserProjectTimesheetReminderDTO userProjectTimesheetDTO = projects.get(0);
//            emailService.sendTimesheetReminderEmailToResource(userProjectTimesheetDTO.email(), userProjectTimesheetDTO.name(), projects);
//        });
//    }
//    
//    @Scheduled(cron = "0 0 9 * * *")
//    public void sendSummaryMailToManagerSchedule() {
//     
//    	String day = LocalDate.now().getDayOfWeek().toString();
//    	
//    	List<String> projectCodes = projectDetailsRepository
//				.findAllProjectCodesByTimesheetSummaryDay(day);
//    	sendSummaryTimesheetEmailToManager(projectCodes);
//    	
//    	List<String> projectCodesFor1stWarning = projectDetailsRepository.findAllProjectCodesByWarningMailDay1(day);
//    	if (!projectCodesFor1stWarning.isEmpty()) {
//			sendWarningMail1(projectCodesFor1stWarning, day);
//		}
//		
//		List<String> projectCodesFor2ndWarning = projectDetailsRepository.findAllProjectCodesByWarningMailDay2(day);
//		if (!projectCodesFor2ndWarning.isEmpty()) {
//			sendWarningMail2(projectCodesFor2ndWarning, day);
//		}
//
//        
//    }
//    
//    private void sendSummaryTimesheetEmailToManager(List<String> projectCodes) {
//    	   LocalDate today = LocalDate.now();
//           LocalDate fromDate;
//           LocalDate toDate = today.minusDays(1); // Always yesterday
//
//           if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
//               // If today is Monday, go back one full week
//               fromDate = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
//           } else {
//               // If today is Tue–Sun, use this week's Monday
//               fromDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//           }
//
//           System.out.println("Timesheet summary range:");
//           System.out.println("From: " + fromDate);
//           System.out.println("To:   " + toDate);
//           
//           List<ProjectTimesheetForEmailDTO> projectTimesheetForEmailDTOs =  userInfoRepository.findAllProjectTimesheetForEmailByResourceIdAndProjectCode(
//			   Roles.DELIVERY_MANAGER, 
//			   ApprovalStatus.APPROVED, 
//			   fromDate, 
//			   toDate, 
//			   projectCodes);
//           
//           List<TimesheetSummaryToDMAndPMDTO> timesheetSummaryDTo = convertToSummaryDTO(projectTimesheetForEmailDTOs);
//           sendTimesheetSummaryToManagers(timesheetSummaryDTo).thenAccept(aVoid -> {
//			   log.info("Timesheet summary emails sent successfully to managers.");
//		   }).exceptionally(ex -> {
//			   log.error("Error sending timesheet summary emails: ", ex);
//			   return null;
//		   });
//           
//           
//    }
//    
//    private CompletableFuture<Void> sendTimesheetSummaryToManagers(List<TimesheetSummaryToDMAndPMDTO> summaryList) {
//        	emailProcessingService.sendTimesheetSummaryToManagers(summaryList);// call async method
//        return CompletableFuture.completedFuture(null);
//    }
//        
//    
//    private List<TimesheetSummaryToDMAndPMDTO> convertToSummaryDTO(List<ProjectTimesheetForEmailDTO> flatList) {
//
//        // Group by Delivery Manager
//        return flatList.stream()
//            .collect(Collectors.groupingBy(dto -> dto.deliveryManagerEmail()))
//            .entrySet().stream()
//            .map(dmEntry -> {
//                List<ProjectTimesheetForEmailDTO> dmGroup = dmEntry.getValue();
//                ProjectTimesheetForEmailDTO any = dmGroup.get(0); // common DM info
//
//                // Group by Project Manager within this DM
//                List<ProjectManagerDetailsForTImesheetEmailDTO> pmList = dmGroup.stream()
//                    .collect(Collectors.groupingBy(dto -> dto.projectManagerEmail()))
//                    .entrySet().stream()
//                    .map(pmEntry -> {
//                        List<ProjectTimesheetForEmailDTO> pmGroup = pmEntry.getValue();
//                        ProjectTimesheetForEmailDTO pmSample = pmGroup.get(0);
//
//                        // Group by Project within this PM
//                        List<ProjectDetailsForTimesheetDTO> projectList = pmGroup.stream()
//                            .collect(Collectors.groupingBy(dto -> dto.projectCode()))
//                            .entrySet().stream()
//                            .map(projEntry -> {
//                                List<ProjectTimesheetForEmailDTO> projGroup = projEntry.getValue();
//
//                                // Map to resource details
//                                List<ResourceDetailsForTimesheetDTO> resources = projGroup.stream()
//                                    .map(dto -> new ResourceDetailsForTimesheetDTO(
//                                        dto.resourceId(),
//                                        dto.resourceEmail(),
//                                        dto.resourceName()
//                                    ))
//                                    .distinct()
//                                    .toList();
//
//                                ProjectTimesheetForEmailDTO projSample = projGroup.get(0);
//                                return new ProjectDetailsForTimesheetDTO(
//                                    projSample.projectCode(),
//                                    projSample.projectName(),
//                                    resources
//                                );
//                            })
//                            .toList();
//
//                        return new ProjectManagerDetailsForTImesheetEmailDTO(
//                            pmSample.projectManagerName(),
//                            pmSample.projectManagerId(),
//                            pmSample.projectManagerEmail(),
//                            projectList
//                        );
//                    })
//                    .toList();
//
//                return new TimesheetSummaryToDMAndPMDTO(
//                    any.deliverymanagerName(),
//                    any.deluveryManagerId(),
//                    any.deliveryManagerEmail(),
//                    pmList
//                );
//            })
//            .toList();
//    }
//    
//    private void sendWarningMail1(List<String> projectCodes, String day) {
//        log.info("Sending timesheet reminder emails for day: " + day);
//        LocalDate today = LocalDate.now();
//        LocalDate fromDate;
//        LocalDate toDate;
//
//        DayOfWeek todayDay = today.getDayOfWeek();
//
//        if (todayDay == DayOfWeek.SATURDAY || todayDay == DayOfWeek.SUNDAY) {
//            // This week's Monday to yesterday
//            fromDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//            toDate = today.minusDays(1);
//        } else {
//            // Last week's Monday to last week's Friday
//            fromDate = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
//            toDate = fromDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
//        }
//
//
//        System.out.println("Timesheet summary range:");
//        System.out.println("From: " + fromDate);
//        System.out.println("To:   " + toDate);
//
//        List<UserProjectTimesheetReminderDTO> reminderDTOs = userInfoRepository
//                .findAllResourcesWithPendingAllocationsFor1stWarning(Roles.RESOURCE, day, projectCodes, fromDate, toDate, ApprovalStatus.PENDING);
//
//        if (reminderDTOs.isEmpty()) {
//            log.info("No pending timesheets found for resources on " + day);
//            return;
//        }
//
//        Map<String, List<UserProjectTimesheetReminderDTO>> userProjectMap =
//                reminderDTOs.stream().collect(Collectors.groupingBy(UserProjectTimesheetReminderDTO::empId));
//
//        userProjectMap.forEach((empId, projects) -> {
//            UserProjectTimesheetReminderDTO userProjectTimesheetDTO = projects.get(0);
//            emailService.sendTimesheetReminderEmailToResource(userProjectTimesheetDTO.email(), userProjectTimesheetDTO.name(), projects);
//        });
//    }
//    
//    
//    private void sendWarningMail2(List<String> projectCodes, String day) {
//    	
//    	LocalDate today = LocalDate.now();
//    	LocalDate fromDate;
//    	LocalDate toDate;
//
//    	DayOfWeek todayDay = today.getDayOfWeek();
//
//    	if (todayDay == DayOfWeek.SATURDAY || todayDay == DayOfWeek.SUNDAY) {
//    	    // This week's Monday to yesterday
//    	    fromDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
//    	    toDate = today.minusDays(1);
//    	} else {
//    	    // Last week's Monday to last week's Friday
//    	    fromDate = today.with(TemporalAdjusters.previous(DayOfWeek.MONDAY));
//    	    toDate = fromDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
//    	}
//
//        log.info("Sending timesheet reminder emails for day: " + day);
//
//        List<UserProjectTimesheetReminderDTO> reminderDTOs = userInfoRepository
//                .findAllResourcesWithPendingAllocationsFor2ndWarning(Roles.RESOURCE, day, projectCodes, fromDate, toDate, ApprovalStatus.PENDING);
//        
//        
//
//        if (reminderDTOs.isEmpty()) {
//            log.info("No pending timesheets found for resources on " + day);
//            return;
//        }
//
//        Map<String, List<UserProjectTimesheetReminderDTO>> userProjectMap =
//                reminderDTOs.stream().collect(Collectors.groupingBy(UserProjectTimesheetReminderDTO::empId));
//
//        userProjectMap.forEach((empId, projects) -> {
//            UserProjectTimesheetReminderDTO userProjectTimesheetDTO = projects.get(0);
//            emailService.sendTimesheetReminderEmailToResource(userProjectTimesheetDTO.email(), userProjectTimesheetDTO.name(), projects);
//        });
//    }
    
}
