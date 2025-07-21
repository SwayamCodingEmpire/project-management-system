	package com.cozentus.pms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cozentus.pms.dto.ProjectAllocationDTO;
import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.dto.ProjectManagerFlatDTO;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.dto.ResourceFlatDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.serviceImpl.UserInfoServiceImpl;
import com.cozentus.pms.services.UserInfoService;

@SpringBootTest
public class UserInfoTests {

    @MockitoBean
    private UserInfoRepository userInfoRepository;

    private UserInfoService userInfoService;

    @BeforeEach
    void setUp() {
        userInfoService = new UserInfoServiceImpl(userInfoRepository);
    }

    @Test
    void testGetAllProjectManagersWithProjects() {
        // Arrange
        ProjectManagerFlatDTO row1 = new ProjectManagerFlatDTO("EMP001", "Alice", "alice@example.com", "Project A","PRK103");
        ProjectManagerFlatDTO row2 = new ProjectManagerFlatDTO("EMP001", "Alice", "alice@example.com", "Project B","PRK183");
        ProjectManagerFlatDTO row3 = new ProjectManagerFlatDTO("EMP002", "Bob", "bob@example.com", "Project X","PRK143");

        when(userInfoRepository.findAllProjectManagersAndProjectNames(Roles.PROJECT_MANAGER))
                .thenReturn(List.of(row1, row2, row3));

        // Act
        List<ProjectManagerDTO> result = userInfoService.getAllProjectManagersWithProjects();

        // Assert
        assertEquals(2, result.size());

        ProjectManagerDTO alice = result.stream().filter(pm -> pm.getId().equals("EMP001")).findFirst().orElse(null);
        assertNotNull(alice);
        assertEquals("Alice", alice.getName());
        assertEquals(2, alice.getProjects().size());
        assertTrue(alice.getProjects().contains("Project A"));
        assertTrue(alice.getProjects().contains("Project B"));

        ProjectManagerDTO bob = result.stream().filter(pm -> pm.getId().equals("EMP002")).findFirst().orElse(null);
        assertNotNull(bob);
        assertEquals("Bob", bob.getName());
        assertEquals(1, bob.getProjects().size());
        assertEquals("Project X", bob.getProjects().get(0));
    }
//    
//    @Test
//    void testGetAllResourcesWithAllocations() {
//        // Arrange
//        ResourceFlatDTO row1 = new ResourceFlatDTO(
//                "EMP100", "John", "Java", "Spring", "Developer", BigDecimal.valueOf(4.5), "EMP101", "Jane",
//                new ProjectAllocationDTO("Project Alpha", "API dev", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30), "Alice")
//        );
//
//        ResourceFlatDTO row2 = new ResourceFlatDTO(
//                "EMP100", "John", "Java", "Spring", "Developer", BigDecimal.valueOf(4.5),"EMP101", "Jane",
//                new ProjectAllocationDTO("Project Beta", "Frontend", LocalDate.of(2024, 7, 1), LocalDate.of(2024, 12, 31), "Bob")
//        );
//
//        ResourceFlatDTO row3 = new ResourceFlatDTO(
//                "EMP101", "Jane", "Python", "ML", "Engineer", BigDecimal.valueOf(3.0),"EMP101", "Jane",
//                new ProjectAllocationDTO("Project Gamma", "ML Pipeline", LocalDate.of(2024, 2, 1), LocalDate.of(2024, 8, 31), "Carol")
//        );
//
//        when(userInfoRepository.findAllResourcesWithAllocations())
//                .thenReturn(List.of(row1, row2, row3));
//
//        // Act
//        List<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations();
//
//        // Assert
//        assertEquals(2, result.size());
//
//        ResourceDTO john = result.stream().filter(r -> r.id().equals("EMP100")).findFirst().orElse(null);
//        assertNotNull(john);
//        assertEquals("John", john.name());
//        assertEquals(2, john.allocation().size());
//
//        ResourceDTO jane = result.stream().filter(r -> r.id().equals("EMP101")).findFirst().orElse(null);
//        assertNotNull(jane);
//        assertEquals("Jane", jane.name());
//        assertEquals(1, jane.allocation().size());
//        assertEquals("Project Gamma", jane.allocation().get(0).projectName());
//    }
}
