
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PublicControllerTest {

    @MockBean
    private UserInfoService userInfoService;

    @MockBean
    private AuthenticationService authenticationService;

    private PublicController publicController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        publicController = new PublicController(userInfoService, authenticationService);
    }

    @Test
    public void testGetAllDesignations() {
        List<String> designations = Arrays.asList("Designation1", "Designation2");
        when(userInfoService.getAllDesignations()).thenReturn(designations);

        ResponseEntity<List<String>> response = publicController.getAllDesignations();

        assertEquals(designations, response.getBody());
        verify(userInfoService, times(1)).getAllDesignations();
    }

    @Test
    public void testGetAllSkills() {
        List<String> skills = Arrays.asList("Skill1", "Skill2");
        when(userInfoService.getAllSkills()).thenReturn(skills);

        ResponseEntity<List<String>> response = publicController.getAllSkills();

        assertEquals(skills, response.getBody());
        verify(userInfoService, times(1)).getAllSkills();
    }

    @Test
    public void testLogin() {
        LoginDTO loginRequest = new LoginDTO("username", "password");
        LoginResponseDTO loginResponse = new LoginResponseDTO("token", null, "name","CZ1345");
        when(authenticationService.authenticate(loginRequest)).thenReturn(loginResponse);

        ResponseEntity<LoginResponseDTO> response = publicController.login(loginRequest);

        assertEquals(loginResponse, response.getBody());
        verify(authenticationService, times(1)).authenticate(loginRequest);
    }
}
