
package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.ProjectTypeSummaryDTO;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.repositories.ProjectTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ProjectTypeServiceImplTest {

    @MockBean
    private ProjectTypeRepository projectTypeRepository;

    @InjectMocks
    private ProjectTypeServiceImpl projectTypeService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSave() {
        ProjectType projectType = new ProjectType();
        when(projectTypeRepository.save(any(ProjectType.class))).thenReturn(projectType);
        ProjectType result = projectTypeService.save(projectType);
        assertEquals(projectType, result);
    }

    @Test
    public void testGetAllProjectTypeSummaries() {
        ProjectType projectType = new ProjectType();
        projectType.setId(1);
        projectType.setProjectType("Test");
        projectType.setIsCustomerProject(true);
        when(projectTypeRepository.findAll()).thenReturn(Arrays.asList(projectType));

        List<ProjectTypeSummaryDTO> result = projectTypeService.getAllProjectTypeSummaries();
        assertEquals(1, result.size());
        assertEquals(projectType.getId(), result.get(0).getId());
        assertEquals(projectType.getProjectType(), result.get(0).getProjectType());
        assertEquals(projectType.getIsCustomerProject(), result.get(0).getCustomer());
    }

    @Test
    public void testUpdate() {
        ProjectType existing = new ProjectType();
        existing.setId(1);
        existing.setProjectType("Test");
        existing.setIsCustomerProject(true);

        ProjectType updated = new ProjectType();
        updated.setProjectType("Updated");
        updated.setIsCustomerProject(false);

        when(projectTypeRepository.findById(anyInt())).thenReturn(Optional.of(existing));
        when(projectTypeRepository.save(any(ProjectType.class))).thenReturn(updated);

        ProjectType result = projectTypeService.update(1, updated);
        assertEquals(updated.getProjectType(), result.getProjectType());
        assertEquals(updated.getIsCustomerProject(), result.getIsCustomerProject());
    }

    @Test
    public void testUpdateNotFound() {
        ProjectType updated = new ProjectType();
        updated.setProjectType("Updated");
        updated.setIsCustomerProject(false);

        when(projectTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            projectTypeService.update(1, updated);
        });
    }
}
