
package com.cozentus.pms.controllers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.TimesheetDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.TimesheetService;

@SpringBootTest
public class TimesheetControllerTest {

    @MockBean
    private TimesheetService timesheetService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    public void testGetTimesheetByEmpId() {
        TimesheetDTO timesheetDTO = new TimesheetDTO("projectCode", "projectName", Collections.emptyList());
//        Mockito.when(authenticationService.getCurrentUserDetails()).thenReturn(new Pair<Roles, UserAuthDetails>(Roles.RESOURCE, new User("empId", "empName", true, true, true, true, Collections.emptyList())));
        Mockito.when(timesheetService.getTimeSheetByEmpId(anyString(), any(LocalDate.class), any(LocalDate.class))).thenReturn(Collections.singletonList(timesheetDTO));

        TimesheetController timesheetController = new TimesheetController(timesheetService, authenticationService);
        ResponseEntity<List<TimesheetDTO>> responseEntity = timesheetController.getTimesheetByEmpId(LocalDate.now(), LocalDate.now());

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(timesheetDTO, responseEntity.getBody().get(0));
    }

    // Add similar tests for other methods in TimesheetController
}
