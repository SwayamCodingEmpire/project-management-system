
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.ProjectTypeSummaryDTO;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.services.ProjectTypeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@SpringBootTest
public class ProjectTypeControllerTest {

    @Autowired
    private ProjectTypeController projectTypeController;

    @MockBean
    private ProjectTypeService projectTypeService;

    @Test
    public void testSaveProjectType() {
        ProjectType projectType = new ProjectType();
        Mockito.when(projectTypeService.save(any(ProjectType.class))).thenReturn(projectType);
        ResponseEntity<ProjectType> response = projectTypeController.saveProjectType(new ProjectType());
        assertEquals(projectType, response.getBody());
    }

    @Test
    public void testUpdateProjectType() {
        ProjectType projectType = new ProjectType();
        Mockito.when(projectTypeService.update(anyInt(), any(ProjectType.class))).thenReturn(projectType);
        ResponseEntity<ProjectType> response = projectTypeController.updateProjectType(1, new ProjectType());
        assertEquals(projectType, response.getBody());
    }

    @Test
    public void testGetAllProjectTypeSummaries() {
        List<ProjectTypeSummaryDTO> projectTypeSummaryDTOList = Arrays.asList(new ProjectTypeSummaryDTO(), new ProjectTypeSummaryDTO());
        Mockito.when(projectTypeService.getAllProjectTypeSummaries()).thenReturn(projectTypeSummaryDTOList);
        ResponseEntity<List<ProjectTypeSummaryDTO>> response = projectTypeController.getAllProjectTypeSummaries();
        assertEquals(projectTypeSummaryDTOList, response.getBody());
    }
}
