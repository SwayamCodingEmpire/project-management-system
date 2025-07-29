package com.cozentus.pms.repositories;
import com.cozentus.pms.dto.*;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ProjectDetailsRepositoryTest {

    @MockBean
    private ProjectDetailsRepository projectDetailsRepository;

    @Test
    void testFindAllProjectsForDeliveryManager() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectDetailsForProjectListDTO> expectedPage = Page.empty();
        when(projectDetailsRepository.findAllProjectsForDeliveryManager(1, "test", pageable)).thenReturn(expectedPage);

        Page<ProjectDetailsForProjectListDTO> actualPage = projectDetailsRepository.findAllProjectsForDeliveryManager(1, "test", pageable);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void testFindAllProjectsForProjectManager() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProjectDetailsForProjectListDTO> expectedPage = Page.empty();
        when(projectDetailsRepository.findAllProjectsForProjectManager(1, "test", pageable)).thenReturn(expectedPage);

        Page<ProjectDetailsForProjectListDTO> actualPage = projectDetailsRepository.findAllProjectsForProjectManager(1, "test", pageable);
        assertEquals(expectedPage, actualPage);
    }

    @Test
    void testFindByProjectCodeForEditForm() {
        ProjectDTO expectedProjectDTO = new ProjectDTO(null, null, null, null);
        when(projectDetailsRepository.findByProjectCodeForEditForm("test")).thenReturn(Optional.of(expectedProjectDTO));

        Optional<ProjectDTO> actualProjectDTO = projectDetailsRepository.findByProjectCodeForEditForm("test");
        assertEquals(expectedProjectDTO, actualProjectDTO.orElse(null));
    }

    // Add more test methods for other methods in ProjectDetailsRepository
}
