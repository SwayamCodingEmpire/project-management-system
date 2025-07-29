//Here is a JUnit 5 test class for the `EmailServiceImpl` class. This test class uses Mockito to mock dependencies and simulate their behavior. It also uses `@SpringBootTest` to load the Spring context and `@MockBean` to replace certain beans with mocks in the context.
//
//```java
//package com.cozentus.pms.serviceImpl;
//
//import com.cozentus.pms.dto.*;
//import jakarta.mail.MessagingException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.mail.javamail.JavaMailSender;
//
//import java.io.IOException;
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class EmailServiceImplTest {
//
//    @InjectMocks
//    private EmailServiceImpl emailService;
//
//    @MockBean
//    private JavaMailSender mailSender;
//
//    @MockBean
//    private SingleEmailServiceImpl singleEmailService;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testSendProjectCreationEmailToManager() throws MessagingException, IOException {
//        String managerEmail = "manager@company.com";
//        ProjectDTO projectDTO = new ProjectDTO(null, null, null, "managerId");
//        String managerName = "Manager Name";
//
//        CompletableFuture<Void> result = emailService.sendProjectCreationEmailToManager(managerEmail, projectDTO, managerName);
//
//        assertEquals(CompletableFuture.completedFuture(null), result);
//        verify(mailSender, times(1)).createMimeMessage();
//    }
//
//    @Test
//    public void testSendProjectEditEmailToManager() throws MessagingException, IOException {
//        String managerEmail = "manager@company.com";
//        ProjectDTO projectDTO = new ProjectDTO(null, null, null, "managerId");
//        String managerName = "Manager Name";
//
//        CompletableFuture<Void> result = emailService.sendProjectEditEmailToManager(managerEmail, projectDTO, managerName);
//
//        assertEquals(CompletableFuture.completedFuture(null), result);
//        verify(mailSender, times(1)).createMimeMessage();
//    }
//
//    @Test
//    public void testSendAllocationSummaryToResources() throws MessagingException, IOException {
//        List<ResourceAllocationSummaryDTO> resourceAllocationSummaryDTOs = Arrays.asList(new ResourceAllocationSummaryDTO());
//        String managerEmail = "manager@company.com";
//        String managerId = "managerId";
//        String managerName = "Manager Name";
//        String managerPhone = "1234567890";
//
//        CompletableFuture<Void> result = emailService.sendAllocationSummaryToResources(resourceAllocationSummaryDTOs, managerEmail, managerId, managerName, managerPhone);
//
//        assertEquals(CompletableFuture.completedFuture(null), result);
//        verify(mailSender, times(1)).createMimeMessage();
//    }
//
//    // Add similar tests for other methods in the service class
//
//}
//```
//
//Please note that the actual email sending code is commented out in the provided service class. Therefore, the tests are only verifying that the `createMimeMessage()` method of the `JavaMailSender` mock is called once. If the actual email sending code is uncommented, the tests would need to be updated to verify the correct behavior.
//
//Also, the tests are not checking the content of the emails. If this is required, you would need to use an argument captor to capture the `MimeMessage` object and then assert that it has the correct content.