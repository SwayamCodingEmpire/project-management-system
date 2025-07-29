
package com.cozentus.pms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.KeyPerformanceIndicatorsDTO;
import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.serviceImpl.SkillServiceImpl;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.DMDashboardService;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.UserInfoService;

@SpringBootTest
public class ManagerDashboardControllerTest {

    @InjectMocks
    private ManagerDashboardController managerDashboardController;

    @MockBean
    private DMDashboardService dmDashboardService;

    @MockBean
    private SkillServiceImpl skillServiceImpl;

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private ProjectDetailsService projectDetailsService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void testGetKPI() {
//        KeyPerformanceIndicatorsDTO kpi = new KeyPerformanceIndicatorsDTO();
//        when(dmDashboardService.getResourceBillabilityStats()).thenReturn(kpi);
//        when(dmDashboardService.computeUtilizationBreakdown()).thenReturn(kpi);
//        assertEquals(managerDashboardController.getKPI().getBody(), kpi);
//    }

    @Test
    public void testGetSkillStats() {
        List<SkillCountDTO> skillCounts = new ArrayList<>();
        when(authenticationService.getCurrentUserDetails()).thenReturn(Pair.of(Roles.DELIVERY_MANAGER, new UserAuthDetails(1, "test@test.com", "EMP1")));
        when(skillServiceImpl.getSkillCounts(Roles.DELIVERY_MANAGER, "EMP1", "test")).thenReturn(skillCounts);
        assertEquals(managerDashboardController.getSkillStats("test").getBody(), skillCounts);
    }

    // Similar tests can be written for other methods in the service class
}
