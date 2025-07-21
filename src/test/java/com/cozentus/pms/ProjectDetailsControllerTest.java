package com.cozentus.pms;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectDetailsDTO;
import com.cozentus.pms.services.ProjectDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProjectDetailsControllerTest {
	@BeforeEach
	void setup() {
	    objectMapper.registerModule(new JavaTimeModule());
	}


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectDetailsService projectDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProjectDTO getSampleProjectDTO() {
        return new ProjectDTO(
                new ProjectDetailsDTO(
                        "PX001", "Test Project", "Test Desc",
                        LocalDate.of(2024, 10, 1), LocalDate.of(2025, 3, 1),
                        "INR", "T&M", "Monthly"
                ),
                null, new ClientDTO(1, "TestClient", "LegalEntity", "BU"),
                "PM123"
        );
    }
    
    private ProjectDTO getValidDTO() {
        return new ProjectDTO(
                new ProjectDetailsDTO(
                        "PX001", "Test Project", "Test Desc",
                        LocalDate.of(2024, 10, 1), LocalDate.of(2025, 3, 1),
                        "INR", "T&M", "Monthly"
                ),
                null, new ClientDTO(1, "TestClient", "LegalEntity", "BU"),
                "PM123"
        );
    }
    
    @Test
    void testCreateProject() throws Exception {
        ProjectDTO dto = getSampleProjectDTO();

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());


        Mockito.verify(projectDetailsService).createProjectDetails(Mockito.any(ProjectDTO.class));
    }
    
    @Test
    void testUpdateProject() throws Exception {
        ProjectDTO dto = getSampleProjectDTO();

        mockMvc.perform(put("/v1/project/PX001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        Mockito.verify(projectDetailsService).updateProjectDetails(Mockito.any(ProjectDTO.class), Mockito.eq("PX001"));
    }
    
    @Test
    void testCreateProject_MissingManagerId_ShouldReturnBadRequest() throws Exception {
        ProjectDTO dto = new ProjectDTO(
                getValidDTO().projectInfo(),
                getValidDTO().projectType(),
                getValidDTO().customerInfo(),
                "" // Invalid managerId
        );

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testCreateProject_MissingProjectName_ShouldReturnBadRequest() throws Exception {
        ProjectDetailsDTO invalidDetails = new ProjectDetailsDTO(
                "PX001", "Test Project", "Test Desc",
                LocalDate.of(2024, 10, 1), LocalDate.of(2025, 3, 1),
                "INR", "T&M", "Monthly"
        );

        ProjectDTO dto = new ProjectDTO(
                invalidDetails,
                getValidDTO().projectType(),
                getValidDTO().customerInfo(),
                "PM123"
        );

        mockMvc.perform(post("/v1/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }




}
