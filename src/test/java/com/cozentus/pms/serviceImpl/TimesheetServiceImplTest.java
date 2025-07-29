package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.*;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.entites.TimeSheet;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.ApprovalStatus;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.TimeSheetRepository;
import com.cozentus.pms.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
@SpringBootTest
class TimesheetServiceImplTest {

    @Mock
    private TimeSheetRepository timeSheetRepository;
    @Mock
    private ResourceAllocationRepository resourceAllocationRepository;
    @Mock
    private ProjectDetailsRepository projectDetailsRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private TimesheetServiceImpl timesheetService;

    @BeforeEach
    void setUp() {
        timesheetService = new TimesheetServiceImpl(
                timeSheetRepository,
                resourceAllocationRepository,
                emailService,
                projectDetailsRepository
        );
        // Inject EntityManager manually since it's not in constructor
        java.lang.reflect.Field emField;
        try {
            emField = TimesheetServiceImpl.class.getDeclaredField("entityManager");
            emField.setAccessible(true);
            emField.set(timesheetService, entityManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSaveTimesheetWithValidPayload() {
        String resourceId = "emp123";
        LocalDate date = LocalDate.now();
        DayWiseTimesheet dayWise = new DayWiseTimesheet(true, "PRJ1", BigDecimal.valueOf(8));
        List<DayWiseTimesheet> dayWiseList = new ArrayList<>();
        dayWiseList.add(dayWise);
        SingularTimesheetPayload payload = new SingularTimesheetPayload(date, dayWiseList);

        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForProjectCodeAndResourceId(anyList(), eq(resourceId)))
                .thenReturn(List.of(allocation));
        when(timeSheetRepository.findByResourceAllocation_IdInAndDateAndEnabledTrue(anyList(), eq(date)))
                .thenReturn(Collections.emptyList());
        ResourceAllocation ra = new ResourceAllocation();
        when(entityManager.getReference(ResourceAllocation.class, 1)).thenReturn(ra);

        timesheetService.saveTimesheet(payload, resourceId);

        ArgumentCaptor<List<TimeSheet>> captor = ArgumentCaptor.forClass(List.class);
        verify(timeSheetRepository).saveAllAndFlush(captor.capture());
        List<TimeSheet> saved = captor.getValue();
        assertEquals(1, saved.size());
        TimeSheet ts = saved.get(0);
        assertEquals(date, ts.getDate());
        assertEquals(BigDecimal.valueOf(8), ts.getHours());
        assertEquals(ra, ts.getResourceAllocation());
        assertEquals(ApprovalStatus.PENDING, ts.getApprovalStatus());
        assertTrue(ts.getEnabled());
        assertEquals(resourceId, ts.getCreatedBy());
    }

    @Test
    void testGetTimeSheetByEmpIdReturnsGroupedData() {
        String empId = "emp123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);

        TimesheetFlatDTO flat1 = mock(TimesheetFlatDTO.class);
        TimesheetFlatDTO flat2 = mock(TimesheetFlatDTO.class);
        when(flat1.projectCode()).thenReturn("PRJ1");
        when(flat1.projectName()).thenReturn("Project One");
        ProjectTimeSheetDTO pts1 = new ProjectTimeSheetDTO(start, ApprovalStatus.PENDING, true, BigDecimal.valueOf(8));
        when(flat1.projectTimeSheet()).thenReturn(pts1);

        when(flat2.projectCode()).thenReturn("PRJ2");
        when(flat2.projectName()).thenReturn("Project Two");
        ProjectTimeSheetDTO pts2 = new ProjectTimeSheetDTO(start.plusDays(1), ApprovalStatus.APPROVED, true, BigDecimal.valueOf(7));
        when(flat2.projectTimeSheet()).thenReturn(pts2);

        when(timeSheetRepository.findAllTimesheetByEmpIdAndDateBetween(eq(empId), eq(start), eq(end), isNull()))
                .thenReturn(List.of(flat1, flat2));

        List<TimesheetDTO> result = timesheetService.getTimeSheetByEmpId(empId, start, end);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(dto -> dto.projectCode().equals("PRJ1") && dto.projectName().equals("Project One")));
        assertTrue(result.stream().anyMatch(dto -> dto.projectCode().equals("PRJ2") && dto.projectName().equals("Project Two")));
    }

    @Test
    void testSubmitTimesheetSendsNotification() {
        String resourceId = "emp123";
        LocalDate date = LocalDate.of(2024, 1, 1);
        ProjectTimeSheetDTO pts = new ProjectTimeSheetDTO(date, ApprovalStatus.PENDING, true, BigDecimal.valueOf(8));
        TimesheetDTO dto = new TimesheetDTO("PRJ1", "Project One", List.of(pts));
        List<TimesheetDTO> dtos = List.of(dto);

        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForProjectCodeAndResourceId(anyList(), eq(resourceId)))
                .thenReturn(List.of(allocation));
        when(timeSheetRepository.findByResourceAllocation_IdInAndDateBetweenAndEnabledTrue(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());
        ResourceAllocation ra = new ResourceAllocation();
        when(entityManager.getReference(ResourceAllocation.class, 1)).thenReturn(ra);

        TimesheetSubmissionEmailDTO emailDTO = new TimesheetSubmissionEmailDTO("PRJ1", "Project One", "Manager", "MGR1", "manager@cozentus.com");
        when(projectDetailsRepository.findTimesheetSubmissionEmailDetailsByProjectCode(anyList()))
                .thenReturn(List.of(emailDTO));

        timesheetService.submitTimesheet(dtos, resourceId);

        verify(timeSheetRepository).saveAll(anyList());
        verify(emailService).sendTimesheetSubmissionEmailToManager(anyList(), anyString(), anyString(), anyString(), anyString(), any(), any());
    }

    @Test
    void testSaveTimesheetWithInvalidProjectCodeThrowsException() {
        String resourceId = "emp123";
        LocalDate date = LocalDate.now();
        DayWiseTimesheet dayWise = new DayWiseTimesheet(true, "INVALID", BigDecimal.valueOf(8));
        SingularTimesheetPayload payload = new SingularTimesheetPayload(date, List.of(dayWise));

        when(resourceAllocationRepository.fetchAllocationsIdForProjectCodeAndResourceId(anyList(), eq(resourceId)))
                .thenReturn(Collections.emptyList());

        assertThrows(RecordNotFoundException.class, () -> timesheetService.saveTimesheet(payload, resourceId));
    }

    @Test
    void testGetTimeSheetByEmpIdWithNoDataReturnsEmptyList() {
        String empId = "emp123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);

        when(timeSheetRepository.findAllTimesheetByEmpIdAndDateBetween(eq(empId), eq(start), eq(end), isNull()))
                .thenReturn(Collections.emptyList());

        List<TimesheetDTO> result = timesheetService.getTimeSheetByEmpId(empId, start, end);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTimeSheetSummaryByManagerIdWithInvalidRoleThrowsException() {
        String resourceId = "emp123";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);

        Roles invalidRole = Roles.RESOURCE;

        assertThrows(IllegalArgumentException.class, () ->
                timesheetService.getTimeSheetSummaryByManagerId(resourceId, start, end, invalidRole));
    }

    @Test
    void testApprovalUpdatesStatusAndSendsEmail() throws MessagingException {
        TimesheetApprovalDTO approvalDTO = new TimesheetApprovalDTO("emp123", "PRJ1", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7), true);
        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForSingleProjectCodeAndResourceId("PRJ1", "emp123"))
                .thenReturn(allocation);
        when(timeSheetRepository.approvetimesheetByAllocationIdAndDateAndEnabledTrue(eq(1), any(), any(), eq(true), eq(ApprovalStatus.APPROVED), anyString()))
                .thenReturn(1);
        ResourceDetailsDTO resourceDetailsDTO = new ResourceDetailsDTO("Emp Name", "emp@cozentus.com", "Project One");
        when(resourceAllocationRepository.findDetailsFromAllocationId(1)).thenReturn(resourceDetailsDTO);
//
//        doNothing().when(emailService).sendTimesheetApprovalEmailToResource(any(), anyString(), any(), any(), anyString(), any(), anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> timesheetService.approval(approvalDTO));
        verify(emailService).sendTimesheetApprovalEmailToResource(any(), eq("emp123"), any(), any(), eq("PRJ1"), eq(ApprovalStatus.APPROVED), anyString(), anyString(), anyString());
    }

    @Test
    void testApprovalWithInvalidAllocationOrNoTimesheetThrowsException() {
        TimesheetApprovalDTO approvalDTO = new TimesheetApprovalDTO("emp123", "PRJ1", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 7), true);

        // Case 1: No allocation found
        when(resourceAllocationRepository.fetchAllocationsIdForSingleProjectCodeAndResourceId("PRJ1", "emp123"))
                .thenReturn(null);

        assertThrows(RecordNotFoundException.class, () -> timesheetService.approval(approvalDTO));

        // Case 2: Allocation found but no timesheet rows updated
        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForSingleProjectCodeAndResourceId("PRJ1", "emp123"))
                .thenReturn(allocation);
        when(timeSheetRepository.approvetimesheetByAllocationIdAndDateAndEnabledTrue(eq(1), any(), any(), eq(true), eq(ApprovalStatus.APPROVED), anyString()))
                .thenReturn(0);

        assertThrows(RecordNotFoundException.class, () -> timesheetService.approval(approvalDTO));
    }

    @Test
    void testGetTimeSheetByManagerIdAndResourceIdAndProjectCodeReturnsCorrectData() {
        String resourceId = "emp123";
        String managerId = "mgr456";
        String projectCode = "PRJ1";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);

        TimesheetForManagerFlatDTO flat = mock(TimesheetForManagerFlatDTO.class);
        when(flat.projectCode()).thenReturn(projectCode);
        when(flat.projectName()).thenReturn("Project One");
        ProjectTimeSheetDTO pts = new ProjectTimeSheetDTO(start, ApprovalStatus.PENDING, true, BigDecimal.valueOf(8));
        when(flat.projectTimeSheet()).thenReturn(pts);

        when(timeSheetRepository.findAllTimesheetByEmpIdAndProjectCodeAndManagerIdDateBetween(
                eq(resourceId), eq(managerId), eq(start), eq(end), eq(projectCode)))
                .thenReturn(List.of(flat));

        TimesheetDTO result = timesheetService.getTimeSheetByManagerIdAndresourceIdAndProjectCode(resourceId, start, end, projectCode, managerId);

        assertEquals(projectCode, result.projectCode());
        assertEquals("Project One", result.projectName());
        assertEquals(1, result.projectTimeSheet().size());
        assertEquals(start, result.projectTimeSheet().get(0).date());
    }

    @Test
    void testGetTimeSheetByManagerIdAndResourceIdAndProjectCodeWithNoDataReturnsEmptyDTO() {
        String resourceId = "emp123";
        String managerId = "mgr456";
        String projectCode = "PRJ1";
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);

        when(timeSheetRepository.findAllTimesheetByEmpIdAndProjectCodeAndManagerIdDateBetween(
                eq(resourceId), eq(managerId), eq(start), eq(end), eq(projectCode)))
                .thenReturn(Collections.emptyList());

        TimesheetDTO result = timesheetService.getTimeSheetByManagerIdAndresourceIdAndProjectCode(resourceId, start, end, projectCode, managerId);

        assertEquals("", result.projectCode());
        assertEquals("", result.projectName());
        assertNotNull(result.projectTimeSheet());
        assertTrue(result.projectTimeSheet().isEmpty());
    }

    @Test
    void testSubmitTimesheetUpdatesExistingEntries() {
        String resourceId = "emp123";
        LocalDate date = LocalDate.of(2024, 1, 1);
        ProjectTimeSheetDTO pts = new ProjectTimeSheetDTO(date, ApprovalStatus.SUBMITTED, false, BigDecimal.valueOf(6));
        TimesheetDTO dto = new TimesheetDTO("PRJ1", "Project One", List.of(pts));
        List<TimesheetDTO> dtos = List.of(dto);

        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForProjectCodeAndResourceId(anyList(), eq(resourceId)))
                .thenReturn(List.of(allocation));

        TimeSheet existing = new TimeSheet();
        ResourceAllocation ra = new ResourceAllocation();
        ra.setId(1);
        existing.setResourceAllocation(ra);
        existing.setDate(date);
        existing.setApprovalStatus(ApprovalStatus.PENDING);
        existing.setHours(BigDecimal.valueOf(8));
        existing.setAttendanceStatus(true);

        when(timeSheetRepository.findByResourceAllocation_IdInAndDateBetweenAndEnabledTrue(anyList(), any(), any()))
                .thenReturn(List.of(existing));
        when(entityManager.getReference(ResourceAllocation.class, 1)).thenReturn(ra);

        TimesheetSubmissionEmailDTO emailDTO = new TimesheetSubmissionEmailDTO("PRJ1", "Project One", "Manager", "MGR1", "manager@cozentus.com");
        when(projectDetailsRepository.findTimesheetSubmissionEmailDetailsByProjectCode(anyList()))
                .thenReturn(List.of(emailDTO));

        timesheetService.submitTimesheet(dtos, resourceId);

        ArgumentCaptor<List<TimeSheet>> captor = ArgumentCaptor.forClass(List.class);
        verify(timeSheetRepository).saveAll(captor.capture());
        List<TimeSheet> saved = captor.getValue();
        assertTrue(saved.stream().anyMatch(ts -> ts.getHours().equals(BigDecimal.valueOf(6)) && ts.getApprovalStatus() == ApprovalStatus.SUBMITTED));
    }

    @Test
    void testSubmitTimesheetWithNullApprovalStatusDefaultsToPending() {
        String resourceId = "emp123";
        LocalDate date = LocalDate.of(2024, 1, 1);
        ProjectTimeSheetDTO pts = new ProjectTimeSheetDTO(date, null, true, BigDecimal.valueOf(8));
        TimesheetDTO dto = new TimesheetDTO("PRJ1", "Project One", List.of(pts));
        List<TimesheetDTO> dtos = List.of(dto);

        IdAndCodeDTO allocation = new IdAndCodeDTO(1, "PRJ1");
        when(resourceAllocationRepository.fetchAllocationsIdForProjectCodeAndResourceId(anyList(), eq(resourceId)))
                .thenReturn(List.of(allocation));
        when(timeSheetRepository.findByResourceAllocation_IdInAndDateBetweenAndEnabledTrue(anyList(), any(), any()))
                .thenReturn(Collections.emptyList());
        ResourceAllocation ra = new ResourceAllocation();
        when(entityManager.getReference(ResourceAllocation.class, 1)).thenReturn(ra);

        TimesheetSubmissionEmailDTO emailDTO = new TimesheetSubmissionEmailDTO("PRJ1", "Project One", "Manager", "MGR1", "manager@cozentus.com");
        when(projectDetailsRepository.findTimesheetSubmissionEmailDetailsByProjectCode(anyList()))
                .thenReturn(List.of(emailDTO));

        timesheetService.submitTimesheet(dtos, resourceId);

        ArgumentCaptor<List<TimeSheet>> captor = ArgumentCaptor.forClass(List.class);
        verify(timeSheetRepository).saveAll(captor.capture());
        List<TimeSheet> saved = captor.getValue();
        assertTrue(saved.stream().anyMatch(ts -> ts.getApprovalStatus() == ApprovalStatus.PENDING));
    }
}