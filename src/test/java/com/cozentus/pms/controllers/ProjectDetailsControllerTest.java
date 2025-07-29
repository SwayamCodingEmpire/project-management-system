
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.MailNotificationConfigDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownGroupDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.services.ProjectDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectDetailsService projectDetailsService;

    private ProjectDTO projectDTO;
    private MailNotificationConfigDTO mailConfig;
    private ProjectTypeDropdownGroupDTO projectType;
    private SkillDTO skillDTO;

    @BeforeEach
    public void setUp() {
        projectDTO = new ProjectDTO(null, null, null, "1");
        mailConfig = new MailNotificationConfigDTO("1", "2", "3", "4");
        projectType = new ProjectTypeDropdownGroupDTO(true, Collections.emptyList());
        skillDTO = new SkillDTO("Java", null, "Intermediate");
    }

    @Test
    public void testCreateProject() throws Exception {
        doNothing().when(projectDetailsService).createProjectDetails(any(ProjectDTO.class));

        mockMvc.perform(post("/project")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectDTO)))
                .andExpect(status().isCreated());
    }

    // Add similar tests for updateProject, getProjectDetails, getProjectMailConfig, saveProjectMailConfig, updateAllMailNotificationsConfig, getProjectTypes, addSkillsToProject
    // Make sure to mock the service methods and set the expected HTTP status in the response
    // Use the setUp method to initialize the DTO objects used in the tests
    // Use the objectMapper to convert the DTO objects to JSON strings
}
