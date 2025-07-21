package com.cozentus.pms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.ProjectAllocationDTO;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.UserInfoService;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Resource Module Tests - TDD Approach")
public class ResourceModuleTests {
    
    @Autowired
    private UserInfoService userInfoService;
    
    @Autowired
    private UserInfoRepository userInfoRepository;
    
    @Autowired
    private ProjectDetailsRepository projectDetailsRepository;
    
    @Autowired
    private ResourceAllocationRepository resourceAllocationRepository;
    
    private UserInfo testEmployee1;
    private UserInfo testEmployee2;
    private UserInfo testManager;
    private ProjectDetails testProject1;
    private ProjectDetails testProject2;
    
    @BeforeEach
    void setUp() {
        // Create test manager
        testManager = new UserInfo();
        testManager.setEmpId("MGR001");
        testManager.setName("Test Manager");
        testManager.setEmailId("manager@test.com");
        testManager.setPhoneNo("9999999999");
        testManager.setDesignation("Senior Manager");
        testManager.setExpInYears(BigDecimal.valueOf(2));
        testManager.setRole("MANAGER");
        testManager.setEnabled(true);
        testManager = userInfoRepository.save(testManager);
        
        // Create test employees
        testEmployee1 = new UserInfo();
        testEmployee1.setEmpId("EMP001");
        testEmployee1.setName("John Doe");
        testEmployee1.setEmailId("john.doe@test.com");
        testEmployee1.setPhoneNo("1234567890");

        testEmployee1.setDesignation("Senior Developer");
        testEmployee1.setExpInYears(BigDecimal.valueOf(2));
        testEmployee1.setRole("DEVELOPER");
        testEmployee1.setReportingManager(testManager);
        testEmployee1.setEnabled(true);
        testEmployee1 = userInfoRepository.save(testEmployee1);
        
        testEmployee2 = new UserInfo();
        testEmployee2.setEmpId("EMP002");
        testEmployee2.setName("Jane Smith");
        testEmployee2.setEmailId("jane.smith@test.com");
        testEmployee2.setPhoneNo("0987654321");

        testEmployee2.setDesignation("Frontend Developer");
        testEmployee2.setExpInYears(BigDecimal.valueOf(2));
        testEmployee2.setRole("DEVELOPER");
        testEmployee2.setReportingManager(testManager);
        testEmployee2.setEnabled(true);
        testEmployee2 = userInfoRepository.save(testEmployee2);
        
        // Create test projects
        testProject1 = new ProjectDetails();
        testProject1.setProjectCode("PROJ001");
        testProject1.setProjectName("E-Commerce Platform");
        testProject1.setProjectDescription("Building modern e-commerce solution");
        testProject1.setProjectManager(testManager);
        testProject1.setEnabled(true);
        testProject1 = projectDetailsRepository.save(testProject1);
        
        testProject2 = new ProjectDetails();
        testProject2.setProjectCode("PROJ002");
        testProject2.setProjectName("Mobile Banking App");
        testProject2.setProjectDescription("Secure mobile banking application");
        testProject2.setProjectManager(testManager);
        testProject2.setEnabled(true);
        testProject2 = projectDetailsRepository.save(testProject2);
        
        // Create allocations
        ResourceAllocation allocation1 = new ResourceAllocation();
        allocation1.setResource(testEmployee1);
        allocation1.setProject(testProject1);
        allocation1.setAllocationStartDate(LocalDate.of(2024, 1, 1));
        allocation1.setAllocationEndDate(LocalDate.of(2024, 6, 30));
        allocation1.setEnabled(true);
        resourceAllocationRepository.save(allocation1);
        
        ResourceAllocation allocation2 = new ResourceAllocation();
        allocation2.setResource(testEmployee1);
        allocation2.setProject(testProject2);
        allocation2.setAllocationStartDate(LocalDate.of(2024, 7, 1));
        allocation2.setAllocationEndDate(LocalDate.of(2024, 12, 31));
        allocation2.setEnabled(true);
        resourceAllocationRepository.save(allocation2);
        
        ResourceAllocation allocation3 = new ResourceAllocation();
        allocation3.setResource(testEmployee2);
        allocation3.setProject(testProject1);
        allocation3.setAllocationStartDate(LocalDate.of(2024, 2, 1));
        allocation3.setAllocationEndDate(LocalDate.of(2024, 8, 31));
        allocation3.setEnabled(true);
        resourceAllocationRepository.save(allocation3);
    }
    
    @Test
    @DisplayName("Should fetch all resources with allocations - Happy Path")
    void testGetAllResourcesWithAllocations_HappyPath() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(10, result.getContent().size());
        
//        ResourceDTO employee1DTO = result.getContent().stream()
//            .filter(r -> r.id().equals("EMP001"))
//            .findFirst()
//            .orElse(null);
//        
////        assertNotNull(employee1DTO);
//        assertEquals("John Doe", employee1DTO.name());
//        assertEquals("Java", employee1DTO.primarySkill());
//        assertEquals(2, employee1DTO.allocation().size()); // Employee1 has 2 allocations
    }
    
    @Test
    @DisplayName("Should return correct pagination metadata")
    void testGetAllResourcesWithAllocations_PaginationMetadata() {
        // Given
        Pageable pageable = PageRequest.of(0, 1);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(15, result.getTotalElements());
        assertEquals(15, result.getTotalPages());
        assertEquals(0, result.getNumber());
        assertTrue(result.hasNext());
        assertFalse(result.hasPrevious());
    }
    
    @Test
    @DisplayName("Should search by employee name - Case Insensitive")
    void testGetAllResourcesWithAllocations_SearchByName() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "john";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).name());
        assertEquals("EMP001", result.getContent().get(0).id());
    }
    
    @Test
    @DisplayName("Should search by employee ID")
    void testGetAllResourcesWithAllocations_SearchByEmployeeId() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "EMP002";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("Jane Smith", result.getContent().get(0).name());
        assertEquals("EMP002", result.getContent().get(0).id());
    }
    
    @Test
    @DisplayName("Should search by primary skill")
    void testGetAllResourcesWithAllocations_SearchByPrimarySkill() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "java";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(2, result.getContent().size());
        assertEquals("Java", result.getContent().get(0).primarySkill());
    }
    
    @Test
    @DisplayName("Should search by project name")
    void testGetAllResourcesWithAllocations_SearchByProjectName() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "e-commerce";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(2, result.getContent().size()); // Both employees work on e-commerce project
    }
    
    @Test
    @DisplayName("Should return empty result for non-matching search")
    void testGetAllResourcesWithAllocations_NoMatchingSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "NonExistentSkill";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
    }
    
    @Test
    @DisplayName("Should handle null search parameter")
    void testGetAllResourcesWithAllocations_NullSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        assertFalse(result.isEmpty());
        assertEquals(10, result.getContent().size());
    }
    
    @Test
    @DisplayName("Should handle empty search parameter")
    void testGetAllResourcesWithAllocations_EmptySearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertFalse(result.isEmpty());
        assertEquals(10, result.getContent().size());
    }
    
    @Test
    @Disabled
    @DisplayName("Should group allocations correctly for same employee")
    void testGetAllResourcesWithAllocations_AllocationGrouping() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        ResourceDTO employee1 = result.getContent().stream()
            .filter(r -> r.id().equals("EMP001"))
            .findFirst()
            .orElse(null);
        
        assertEquals(2, employee1.allocation().size());
        
        List<ProjectAllocationDTO> allocations = employee1.allocation();
        assertTrue(allocations.stream().anyMatch(a -> a.projectName().equals("E-Commerce Platform")));
        assertTrue(allocations.stream().anyMatch(a -> a.projectName().equals("Mobile Banking App")));
    }
    
    @Test
    @DisplayName("Should return correct page when requesting second page")
    void testGetAllResourcesWithAllocations_SecondPage() {
        // Given
        Pageable pageable = PageRequest.of(1, 1);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertTrue(result.hasNext());
        assertTrue(result.hasPrevious());
    }
    
    @Test
    @DisplayName("Should search by experience years")
    void testGetAllResourcesWithAllocations_SearchByExperience() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "5";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(5.17, result.getContent().get(0).experience());
    }
    
    @Test
    @DisplayName("Should search by designation")
    void testGetAllResourcesWithAllocations_SearchByDesignation() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "frontend";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(1, result.getContent().size());
        assertEquals("Frontend Developer", result.getContent().get(0).designation());
    }
    
    @Disabled
    @Test
    @DisplayName("Should search by project manager name")
    void testGetAllResourcesWithAllocations_SearchByProjectManager() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String search = "Test Manager";
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(search, pageable);
        
        // Then
        assertEquals(2, result.getContent().size());
        // This test will initially fail if PM search is not implemented
    }
    
    @Disabled
    @Test
    @DisplayName("Should filter by active allocations only")
    void testGetAllResourcesWithAllocations_ActiveAllocationsOnly() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        // This would fail if inactive allocations are included
        result.getContent().forEach(resource -> {
            resource.allocation().forEach(allocation -> {
                assertFalse(allocation.endDate().isBefore(LocalDate.now()));
            });
        });
    }
    
    @Disabled
    @Test
    @DisplayName("Should handle large dataset efficiently")
    void testGetAllResourcesWithAllocations_LargeDatasetPerformance() {
        // This test would fail initially due to N+1 query issues
        long startTime = System.currentTimeMillis();
        
        Pageable pageable = PageRequest.of(0, 100);
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        
        // Should complete within 2 seconds for reasonable dataset
        assertTrue(executionTime < 2000, "Query took too long: " + executionTime + "ms");
    }
    
    @Test
    @DisplayName("Should handle edge case - Employee with no allocations")
    void testGetAllResourcesWithAllocations_EmployeeWithNoAllocations() {
        // Given - Create employee without allocations
        UserInfo employeeWithoutAllocation = new UserInfo();
        employeeWithoutAllocation.setEmpId("EMP003");
        employeeWithoutAllocation.setName("No Allocation Employee");
        employeeWithoutAllocation.setEmailId("no.allocation@test.com");
        employeeWithoutAllocation.setPhoneNo("5555555555");
        employeeWithoutAllocation.setDesignation("Junior Developer");
        employeeWithoutAllocation.setExpInYears(BigDecimal.valueOf(1));
        employeeWithoutAllocation.setRole("DEVELOPER");
        employeeWithoutAllocation.setReportingManager(testManager);
        employeeWithoutAllocation.setEnabled(true);
        userInfoRepository.save(employeeWithoutAllocation);
        
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then - Should only return employees with allocations
        assertEquals(10, result.getContent().size());
        assertFalse(result.getContent().stream()
            .anyMatch(r -> r.id().equals("EMP003")));
    }
    
    @Test
    @DisplayName("Should validate ResourceDTO structure")
    void testGetAllResourcesWithAllocations_DTOStructure() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        
        // When
        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations(null, pageable);
        
        // Then
        ResourceDTO resource = result.getContent().get(0);
        assertNotNull(resource.id());
        assertNotNull(resource.name());
        assertNotNull(resource.emailId());
        assertNotNull(resource.phoneNumber());
        assertNotNull(resource.primarySkill());
        assertNotNull(resource.designation());
        assertNotNull(resource.experience());
        assertNotNull(resource.role());
        assertNotNull(resource.reportingManagerId());
        assertNotNull(resource.reportingManagerName());
        assertNotNull(resource.allocation());
        
        if (!resource.allocation().isEmpty()) {
            ProjectAllocationDTO allocation = resource.allocation().get(0);
            assertNotNull(allocation.projectName());
            assertNotNull(allocation.description());
            assertNotNull(allocation.startDate());
            assertNotNull(allocation.endDate());
            assertNotNull(allocation.projectManager());
        }
    }
}
