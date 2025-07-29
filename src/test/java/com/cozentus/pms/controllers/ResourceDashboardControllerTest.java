
package com.cozentus.pms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ResourceWeeklySummaryDTO;
import com.cozentus.pms.dto.UtilizationPairDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.ResourceAllocationService;

@SpringBootTest
public class ResourceDashboardControllerTest {

    @Autowired
    private ResourceDashboardController resourceDashboardController;

    @MockBean
    private ResourceAllocationService resourceAllocationService;

    @MockBean
    private ProjectDetailsService projectDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void testGetResourceProjectCountAndWeeklyHours() {
        ResourceWeeklySummaryDTO mockSummary = new ResourceWeeklySummaryDTO(5, BigDecimal.valueOf(40));
        when(resourceAllocationService.getResourceProjectCountAndWeeklyHours()).thenReturn(mockSummary);

        ResponseEntity<ResourceWeeklySummaryDTO> response = resourceDashboardController.getResourceProjectCountAndWeeklyHours();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockSummary, response.getBody());
    }

    @Test
    public void testGetProjectDetailsForResource() {
        List<ProjectDetailsForProjectListDTO> mockProjects = Arrays.asList(
                new ProjectDetailsForProjectListDTO("P1", "Project 1", "Customer 1", "USD", LocalDate.now(), LocalDate.now().plusDays(10), "Manager 1", "Type 1"),
                new ProjectDetailsForProjectListDTO("P2", "Project 2", "Customer 2", "USD", LocalDate.now(), LocalDate.now().plusDays(20), "Manager 2", "Type 2")
        );
        when(projectDetailsService.getAllProjectsForResource()).thenReturn(mockProjects);

        ResponseEntity<List<ProjectDetailsForProjectListDTO>> response = resourceDashboardController.getProjectDetailsForResource();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockProjects, response.getBody());
    }

    @Test
    public void testGetResourceUtilization() {
        List<UtilizationPairDTO> mockUtilization = Arrays.asList(
                new UtilizationPairDTO(LocalDate.now(), BigDecimal.valueOf(80), BigDecimal.valueOf(70)),
                new UtilizationPairDTO(LocalDate.now().plusDays(1), BigDecimal.valueOf(90), BigDecimal.valueOf(80))
        );
        String mockEmpId = "E1";
        LocalDate today = LocalDate.now();
        LocalDate friday = today.with(DayOfWeek.FRIDAY);
        if (today.getDayOfWeek().getValue() >= DayOfWeek.SATURDAY.getValue()) {
            friday = friday.minusWeeks(1);
        }
        LocalDate monday = friday.with(DayOfWeek.MONDAY);
        when(authenticationService.getCurrentUserDetails().getRight().empId()).thenReturn(mockEmpId);
        when(resourceAllocationService.getResourceDashboardUtilStats(mockEmpId, monday, friday)).thenReturn(mockUtilization);

        ResponseEntity<List<UtilizationPairDTO>> response = resourceDashboardController.getResourceUtilization();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockUtilization, response.getBody());
    }
}
