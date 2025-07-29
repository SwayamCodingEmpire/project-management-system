
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.ProjectResourceSummaryCountDTO;
import com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ProjectManagerDashboardControllerTest {

    @MockBean
    private ProjectDetailsService projectDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    private ProjectManagerDashboardController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ProjectManagerDashboardController(projectDetailsService, authenticationService);
    }

    @Test
    public void testGetProjectUnderManager() {
        String empId = "EMP001";
        List<ProjectResourceSummaryCountDTO> expected = Arrays.asList(
                new ProjectResourceSummaryCountDTO("P1", "Project 1", "Customer 1", 5L, empId),
                new ProjectResourceSummaryCountDTO("P2", "Project 2", "Customer 2", 3L, empId)
        );

        when(authenticationService.getCurrentUserDetails().getRight().empId()).thenReturn(empId);
        when(projectDetailsService.getProjectResourceSummaryByManager(empId)).thenReturn(expected);

        ResponseEntity<List<ProjectResourceSummaryCountDTO>> response = controller.getProjectUnderManager();

        assertEquals(expected, response.getBody());
    }

    @Test
    public void testGetProjectResourceSummary() {
        String projectCode = "P1";
        List<ResourceProjectMinimalDashboardDTO> expected = Arrays.asList(
                new ResourceProjectMinimalDashboardDTO("R1", "Resource 1", "Role 1", BigDecimal.valueOf(0.8)),
                new ResourceProjectMinimalDashboardDTO("R2", "Resource 2", "Role 2", BigDecimal.valueOf(0.6))
        );

        when(projectDetailsService.getResourceProjectMinimalDashboardData(projectCode)).thenReturn(expected);

        ResponseEntity<List<ResourceProjectMinimalDashboardDTO>> response = controller.getProjectResourceSummary(projectCode);

        assertEquals(expected, response.getBody());
    }
}
