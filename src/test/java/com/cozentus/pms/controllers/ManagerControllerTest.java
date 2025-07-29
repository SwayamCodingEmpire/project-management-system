
package com.cozentus.pms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.ProjectDetailsService;

import java.util.Collections;

@SpringBootTest
public class ManagerControllerTest {

    @MockBean
    private ProjectDetailsService projectDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    private ManagerController managerController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        managerController = new ManagerController(projectDetailsService, authenticationService);
    }

    @Test
    public void testGetAllProjectsForManager() {
        UserAuthDetails userAuthDetails = new UserAuthDetails(1, "test@test.com", "EMP001");
        Pair<Roles, UserAuthDetails> currentUserDetails = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(currentUserDetails);

        Pageable pageable = PageRequest.of(0, 5);
        ProjectDetailsForProjectListDTO projectDetails = new ProjectDetailsForProjectListDTO("P001", "Project 1", "Customer 1", "USD", null, null, "Manager 1", "Type 1");
        Page<ProjectDetailsForProjectListDTO> expectedPage = new PageImpl<>(Collections.singletonList(projectDetails));
        when(projectDetailsService.fetchAllProjectsForDeliveryManager(null, pageable, userAuthDetails.userId())).thenReturn(expectedPage);

        Page<ProjectDetailsForProjectListDTO> actualPage = managerController.getAllProjectsForManager(0, 5, null);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    public void testGetAllProjectsForManagerWithProjectManagerRole() {
        UserAuthDetails userAuthDetails = new UserAuthDetails(1, "test@test.com", "EMP001");
        Pair<Roles, UserAuthDetails> currentUserDetails = Pair.of(Roles.PROJECT_MANAGER, userAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(currentUserDetails);

        Pageable pageable = PageRequest.of(0, 5);
        ProjectDetailsForProjectListDTO projectDetails = new ProjectDetailsForProjectListDTO("P001", "Project 1", "Customer 1", "USD", null, null, "Manager 1", "Type 1");
        Page<ProjectDetailsForProjectListDTO> expectedPage = new PageImpl<>(Collections.singletonList(projectDetails));
        when(projectDetailsService.fetchAllProjectsForProjectManager(null, pageable, userAuthDetails.userId())).thenReturn(expectedPage);

        Page<ProjectDetailsForProjectListDTO> actualPage = managerController.getAllProjectsForManager(0, 5, null);
        assertEquals(expectedPage, actualPage);
    }
}
