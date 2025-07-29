
package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SingleEmailServiceImplTest {

    @MockBean
    private JavaMailSender mailSender;

    @InjectMocks
    private SingleEmailServiceImpl singleEmailService;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    private TimesheetSubmissionEmailDTO timesheetSubmissionEmailDTO;
    private ProjectManagerDetailsForTImesheetEmailDTO projectManagerDetailsForTImesheetEmailDTO;
    private TimesheetSummaryToDMAndPMDTO timesheetSummaryToDMAndPMDTO;

    @BeforeEach
    public void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        timesheetSubmissionEmailDTO = new TimesheetSubmissionEmailDTO("projectCode", "projectName", "managerName", "managerId", "managerEmail");
        projectManagerDetailsForTImesheetEmailDTO = new ProjectManagerDetailsForTImesheetEmailDTO("projectManagerName", "projectManagerId", "projectManagerEmail", Collections.emptyList());
        timesheetSummaryToDMAndPMDTO = new TimesheetSummaryToDMAndPMDTO("deliverymanagerName", "deluveryManagerId", "deliveryManagerEmail", Collections.emptyList());
    }

//    @Test
//    public void testSendSingleTimesheetEmail() throws MessagingException {
//        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
//        when(mimeMessageHelper.setTo(any(String.class))).thenReturn(mimeMessageHelper);
//        when(mimeMessageHelper.setSubject(any(String.class))).thenReturn(mimeMessageHelper);
//        when(mimeMessageHelper.setText(any(String.class), any(Boolean.class))).thenReturn(mimeMessageHelper);
//
//        CompletableFuture<Void> result = singleEmailService.sendSingleTimesheetEmail(timesheetSubmissionEmailDTO, "resourceEmail", "resourceId", "resourcePhoneNo", "resourceName", LocalDate.now(), LocalDate.now());
//
//        verify(mailSender, times(1)).send(any(MimeMessage.class));
//    }

    @Test
    public void testSendTimesheetSummaryToPM() {
        CompletableFuture<Void> result = singleEmailService.sendTimesheetSummaryToPM(projectManagerDetailsForTImesheetEmailDTO);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testSendTimesheetSummaryToDM() {
        CompletableFuture<Void> result = singleEmailService.sendTimesheetSummaryToDM(timesheetSummaryToDMAndPMDTO);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}