//
//package com.cozentus.pms.serviceImpl;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import com.cozentus.pms.dto.UserProjectTimesheetReminderDTO;
//import com.cozentus.pms.helpers.Roles;
//import com.cozentus.pms.repositories.ProjectDetailsRepository;
//import com.cozentus.pms.repositories.UserInfoRepository;
//import com.cozentus.pms.services.EmailProcessingService;
//import com.cozentus.pms.services.EmailService;
//
//@SpringBootTest
//public class EmailSchedulerServiceImplTest {
//
//    @Mock
//    private EmailProcessingService emailProcessingService;
//
//    @Mock
//    private ProjectDetailsRepository projectDetailsRepository;
//
//    @Mock
//    private UserInfoRepository userInfoRepository;
//
//    @Mock
//    private EmailService emailService;
//
//    private EmailSchedulerServiceImpl emailSchedulerService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        emailSchedulerService = new EmailSchedulerServiceImpl(projectDetailsRepository, userInfoRepository, emailService, emailProcessingService);
//    }
//
//    @Test
//    public void testSendReminderMail() {
//        List<UserProjectTimesheetReminderDTO> reminderDTOs = Arrays.asList(
//                new UserProjectTimesheetReminderDTO("1", "John Doe", "john.doe@example.com", "P1", "Project 1", "Manager 1", "manager1@example.com", "1234567890", "M1"),
//                new UserProjectTimesheetReminderDTO("2", "Jane Doe", "jane.doe@example.com", "P2", "Project 2", "Manager 2", "manager2@example.com", "0987654321", "M2")
//        );
//
//        when(userInfoRepository.findAllResourcesWithPendingAllocations(Roles.RESOURCE, "MONDAY")).thenReturn(reminderDTOs);
//
//        emailSchedulerService.sendReminderMail("MONDAY");
//
//        verify(emailService, times(2)).sendTimesheetReminderEmailToResource(any(), any(), any());
//    }
//
//    // TODO: Add more test methods for other public methods in EmailSchedulerServiceImpl
//}
