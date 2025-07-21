package com.cozentus.pms;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cozentus.pms.dto.ProjectAllocationDTO;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.services.ProjectDetailsService;
import com.cozentus.pms.services.UserInfoService;

@AutoConfigureMockMvc
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserInfoControllerTesting {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectDetailsService projectDetailsService;

    @MockitoBean
    private UserInfoService userInfoService;
    
    

    @Test
    void testGetAllProjectsForDM() throws Exception {
        List<ProjectDetailsForProjectListDTO> projectList = List.of(
            new ProjectDetailsForProjectListDTO(
                "P123", "NextGen AI", "OpenAI", "USD",
                LocalDate.of(2024, 10, 1),
                LocalDate.of(2025, 3, 31),
                "Alice Johnson","CLIENT"
            ),
            new ProjectDetailsForProjectListDTO(
                "P124", "Smart Retail", "Amazon", "INR",
                LocalDate.of(2024, 11, 15),
                LocalDate.of(2025, 5, 15),
                "Bob Singh","CLIENT"
            )
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<ProjectDetailsForProjectListDTO> mockPage = new PageImpl<>(projectList, pageable, projectList.size());

        String search = "Smart Retail";
        int deliveryManagerId = 32;

        Mockito.when(projectDetailsService.fetchAllProjectsForDeliveryManager(search, pageable, deliveryManagerId))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/manager/projects")
                .param("search", search)
                .param("page", "0")
                .param("size", "5")
                .param("deliveryManagerId", String.valueOf(deliveryManagerId))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].projectCode").value("P123"))
                .andExpect(jsonPath("$.content[1].projectCode").value("P124"));
    }

    @Test
    void testGetAllResourcesWithAllocations() throws Exception {
        List<ProjectAllocationDTO> allocations1 = List.of(
            new ProjectAllocationDTO(
            		"CODE1123",
                "E-Commerce Platform",
                "Building modern e-commerce solution",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30),
                "Alice Johnson","CLIENT"
            ),
            new ProjectAllocationDTO(
            		"CODE432",
                "Mobile Banking App",
                "Secure mobile banking application",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 12, 31),
                "Alice Johnson",
                "CLIENT"
            )
        );

        List<ProjectAllocationDTO> allocations2 = List.of(
            new ProjectAllocationDTO(
            		"CODE1234",
                "E-Commerce Platform",
                "Building modern e-commerce solution",
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 8, 31),
                "Alice Johnson",
                "CLIENT"
            )
        );

        List<ResourceDTO> resourceList = List.of(
            new ResourceDTO(
                "EMP001", "John Doe", "john.doe@test.com", "1234567890",
                "Java", "Spring Boot", "Senior Developer", 5.0,
                "DEVELOPER", "MGR001", "Test Manager", allocations1
            ),
            new ResourceDTO(
                "EMP002", "Jane Smith", "jane.smith@test.com", "0987654321",
                "React", "JavaScript", "Frontend Developer", 3.0,
                "DEVELOPER", "MGR001", "Test Manager", allocations2
            )
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(resourceList, pageable, resourceList.size());

        Mockito.when(userInfoService.getAllResourcesWithAllocations(null, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("EMP001"))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[0].primarySkill").value("Java"))
                .andExpect(jsonPath("$.content[0].allocation.length()").value(2))
                .andExpect(jsonPath("$.content[1].id").value("EMP002"))
                .andExpect(jsonPath("$.content[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.content[1].allocation.length()").value(1));
    }

    @Test
    void testGetAllResourcesWithAllocations_WithSearch() throws Exception {
        List<ProjectAllocationDTO> allocations = List.of(
            new ProjectAllocationDTO(
            		"CODE1234",
                "E-Commerce Platform",
                "Building modern e-commerce solution",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30),
                "Alice Johnson","CLIENT"
            )
        );

        List<ResourceDTO> resourceList = List.of(
            new ResourceDTO(
                "EMP001", "John Doe", "john.doe@test.com", "1234567890",
                "Java", "Spring Boot", "Senior Developer", 5.0,
                "DEVELOPER", "MGR001", "Test Manager", allocations
            )
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(resourceList, pageable, 1);

        String search = "Java";

        Mockito.when(userInfoService.getAllResourcesWithAllocations(search, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("search", search)
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value("EMP001"))
                .andExpect(jsonPath("$.content[0].primarySkill").value("Java"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetAllResourcesWithAllocations_EmptyResult() throws Exception {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(List.of(), pageable, 0);

        String search = "NonExistentSkill";

        Mockito.when(userInfoService.getAllResourcesWithAllocations(search, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("search", search)
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void testGetAllResourcesWithAllocations_DefaultPagination() throws Exception {
        List<ResourceDTO> resourceList = List.of(
            new ResourceDTO(
                "EMP001", "John Doe", "john.doe@test.com", "1234567890",
                "Java", "Spring Boot", "Senior Developer", 5.0,
                "DEVELOPER", "MGR001", "Test Manager", List.of()
            )
        );

        Pageable defaultPageable = PageRequest.of(0, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(resourceList, defaultPageable, resourceList.size());

        Mockito.when(userInfoService.getAllResourcesWithAllocations(null, defaultPageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void testGetAllResourcesWithAllocations_CustomPageSize() throws Exception {
        List<ResourceDTO> resourceList = List.of(
            new ResourceDTO(
                "EMP001", "John Doe", "john.doe@test.com", "1234567890",
                "Java", "Spring Boot", "Senior Developer", 5.0,
                "DEVELOPER", "MGR001", "Test Manager", List.of()
            ),
            new ResourceDTO(
                "EMP002", "Jane Smith", "jane.smith@test.com", "0987654321",
                "React", "JavaScript", "Frontend Developer", 3.0,
                "DEVELOPER", "MGR001", "Test Manager", List.of()
            )
        );

        Pageable pageable = PageRequest.of(0, 10);
        Page<ResourceDTO> mockPage = new PageImpl<>(resourceList, pageable, resourceList.size());

        Mockito.when(userInfoService.getAllResourcesWithAllocations(null, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void testGetAllResourcesWithAllocations_SecondPage() throws Exception {
        Pageable pageable = PageRequest.of(1, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(List.of(), pageable, 10);

        Mockito.when(userInfoService.getAllResourcesWithAllocations(null, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("page", "1")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.totalElements").value(10))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    void testGetAllResourcesWithAllocations_VerifyAllocationStructure() throws Exception {
        List<ProjectAllocationDTO> allocations = List.of(
            new ProjectAllocationDTO(
            		"CODE1234",
                "E-Commerce Platform",
                "Building modern e-commerce solution",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 6, 30),
                "Alice Johnson","CLIENT"
            )
        );

        List<ResourceDTO> resourceList = List.of(
            new ResourceDTO(
                "EMP001", "John Doe", "john.doe@test.com", "1234567890",
                "Java", "Spring Boot", "Senior Developer", 5.0,
                "DEVELOPER", "MGR001", "Test Manager", allocations
            )
        );

        Pageable pageable = PageRequest.of(0, 5);
        Page<ResourceDTO> mockPage = new PageImpl<>(resourceList, pageable, resourceList.size());

        Mockito.when(userInfoService.getAllResourcesWithAllocations(null, pageable))
               .thenReturn(mockPage);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/resources")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
