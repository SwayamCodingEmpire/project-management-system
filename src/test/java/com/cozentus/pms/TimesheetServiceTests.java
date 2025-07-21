package com.cozentus.pms;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.ProjectTimeSheetDTO;
import com.cozentus.pms.dto.TimesheetDTO;
import com.cozentus.pms.repositories.TimeSheetRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.TimesheetService;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Timesheet Retrieval Tests")
public class TimesheetServiceTests {

    @Autowired
    private TimesheetService timesheetService;

    @Autowired
    private TimeSheetRepository timeSheetRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Test
    @DisplayName("Should return grouped timesheets by project for employee")
    void testGetTimesheetByEmpId_HappyPath() {
        // Given
        String empId = "EMP001";
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When
        List<TimesheetDTO> timesheets = timesheetService.getTimeSheetByEmpId(empId, startDate, endDate);

        // Then
        assertNotNull(timesheets);
        assertFalse(timesheets.isEmpty());

        for (TimesheetDTO dto : timesheets) {
            assertNotNull(dto.projectCode());
            assertNotNull(dto.projectName());
            assertNotNull(dto.projectTimeSheet());
            assertFalse(dto.projectTimeSheet().isEmpty());

            for (ProjectTimeSheetDTO entry : dto.projectTimeSheet()) {
                assertNotNull(entry.date());
                assertNotNull(entry.attendanceStatus());
                assertNotNull(entry.hoursWorked());
            }
        }
    }

    @Test
    @DisplayName("Should return empty list when employee has no timesheets in given date range")
    void testGetTimesheetByEmpId_NoTimesheets() {
        // Given
        String empId = "EMP002"; // Assume no timesheets exist for this ID
        LocalDate startDate = LocalDate.of(2030, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        // When
        List<TimesheetDTO> timesheets = timesheetService.getTimeSheetByEmpId(empId, startDate, endDate);

        // Then
        assertNotNull(timesheets);
        assertTrue(timesheets.isEmpty());
    }

    @Test
    @DisplayName("Should handle non-existent employee ID gracefully")
    void testGetTimesheetByEmpId_UnknownEmpId() {
        // Given
        String empId = "EMP999"; // Not present in DB
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);

        // When
        List<TimesheetDTO> timesheets = timesheetService.getTimeSheetByEmpId(empId, startDate, endDate);

        // Then
        assertNotNull(timesheets);
        assertTrue(timesheets.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when startDate > endDate")
    void testGetTimesheetByEmpId_InvalidDateRange() {
        // Given
        String empId = "EMP001";
        LocalDate startDate = LocalDate.of(2024, 12, 31);
        LocalDate endDate = LocalDate.of(2024, 1, 1);

        // When
        List<TimesheetDTO> timesheets = timesheetService.getTimeSheetByEmpId(empId, startDate, endDate);

        // Then
        assertNotNull(timesheets);
        assertTrue(timesheets.isEmpty());
    }

    @Test
    @DisplayName("Should return empty when dates are null (depending on fallback logic)")
    void testGetTimesheetByEmpId_NullDates() {
        // Given
        String empId = "EMP001";

        // When
        List<TimesheetDTO> timesheets = timesheetService.getTimeSheetByEmpId(empId, null, null);

        // Then
        assertNotNull(timesheets);
        assertTrue(timesheets.isEmpty()); // or handle defaulting if your method does so
    }
}
