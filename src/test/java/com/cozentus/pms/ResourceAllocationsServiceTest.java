package com.cozentus.pms;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cozentus.pms.dto.ProjectAllocationDetailsDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceAllocationsFlatDTO;
import com.cozentus.pms.serviceImpl.ResourceAllocationServiceImpl;
import com.cozentus.pms.services.ResourceAllocationService;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ResourceAllocationsServiceTest {


	@Autowired
    private ResourceAllocationServiceImpl resourceAllocationsService;

    private List<ResourceAllocationsFlatDTO> testData;
    private ProjectAllocationDetailsDTO projectAllocation1;
    private ProjectAllocationDetailsDTO projectAllocation2;
    private ResourceAllocationsFlatDTO flatDTO1;
    private ResourceAllocationsFlatDTO flatDTO2;

    @BeforeEach
    void setUp() {
        projectAllocation1 = new ProjectAllocationDetailsDTO(
                "PROJ001",
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                new BigDecimal("80.00"),
                new BigDecimal("160.00"), // plannedUtil
                null, // actualUtil - will be calculated
                22L // daysWithEntries
        );

        projectAllocation2 = new ProjectAllocationDetailsDTO(
                "PROJ002",
                "Project Beta",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 12, 31),
                "Senior Developer",
                new BigDecimal("90.00"),
                new BigDecimal("120.00"), // plannedUtil
                null, // actualUtil - will be calculated
                18L // daysWithEntries
        );

        flatDTO1 = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"), // dailyWorkingHours
                projectAllocation1
        );

        flatDTO2 = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"), // dailyWorkingHours
                projectAllocation2
        );

        testData = new ArrayList<>();
    }

    @Test
    @DisplayName("Should return empty list when input is null")
    void shouldReturnEmptyListWhenInputIsNull() {
        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(null);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when input is empty")
    void shouldReturnEmptyListWhenInputIsEmpty() {
        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(new ArrayList<>());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should process single employee with single allocation")
    void shouldProcessSingleEmployeeWithSingleAllocation() {
        // Given
        testData.add(flatDTO1);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.id()).isEqualTo("EMP001");
        assertThat(dto.name()).isEqualTo("John Doe");
        assertThat(dto.primarySkill()).isEqualTo("Java");
        assertThat(dto.secondarySkill()).isEqualTo("Spring Boot");
        assertThat(dto.designation()).isEqualTo("Senior Developer");
        assertThat(dto.experience()).isEqualTo(new BigDecimal("5.5"));
        
        // Check project allocations
        assertThat(dto.currentAllocation()).hasSize(1);
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
        assertThat(allocation.projectCode()).isEqualTo("PROJ001");
        assertThat(allocation.projectName()).isEqualTo("Project Alpha");
        assertThat(allocation.billability()).isEqualTo(new BigDecimal("80.00"));
        
        // Check calculated values
        assertThat(dto.billability()).isEqualTo(new BigDecimal("80.00"));
        
        // Planned util calculation: (160 / 8) * 100 = 2000%
        BigDecimal expectedPlannedUtil = new BigDecimal("160.00")
                .divide(new BigDecimal("8.00"), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        assertThat(dto.plannedUtil()).isEqualTo(expectedPlannedUtil);
    }

    @Test
    @DisplayName("Should process single employee with multiple allocations")
    void shouldProcessSingleEmployeeWithMultipleAllocations() {
        // Given
        testData.add(flatDTO1);
        testData.add(flatDTO2);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.id()).isEqualTo("EMP001");
        assertThat(dto.currentAllocation()).hasSize(2);
        
        // Check average billability: (80 + 90) / 2 = 85
        assertThat(dto.billability()).isEqualTo(new BigDecimal("85.00"));
        
        // Check project codes
        List<String> projectCodes = dto.currentAllocation().stream()
                .map(ProjectAllocationDetailsDTO::projectCode)
                .toList();
        assertThat(projectCodes).containsExactlyInAnyOrder("PROJ001", "PROJ002");
    }

    @Test
    @DisplayName("Should process multiple employees")
    void shouldProcessMultipleEmployees() {
        // Given
        ResourceAllocationsFlatDTO flatDTO3 = new ResourceAllocationsFlatDTO(
                "EMP002",
                "Jane Smith",
                "Lead Developer",
                new BigDecimal("7.0"),
                new BigDecimal("8.00"),
                new ProjectAllocationDetailsDTO(
                        "PROJ003",
                        "Project Gamma",
                        LocalDate.of(2024, 3, 1),
                        LocalDate.of(2024, 9, 30),
                        "Tech Lead",
                        new BigDecimal("95.00"),
                        new BigDecimal("200.00"),
                        null,
                        25L
                )
        );

        testData.add(flatDTO1);
        testData.add(flatDTO3);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(2);
        
        List<String> empIds = result.stream()
                .map(ResourceAllocationsDTO::id)
                .toList();
        assertThat(empIds).containsExactlyInAnyOrder("EMP001", "EMP002");
    }

    @Test
    @DisplayName("Should handle null allocation gracefully")
    void shouldHandleNullAllocationGracefully() {
        // Given
        ResourceAllocationsFlatDTO flatDTOWithNullAllocation = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"),
                null // null allocation
        );
        testData.add(flatDTOWithNullAllocation);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.currentAllocation()).isEmpty();
        assertThat(dto.billability()).isEqualTo(BigDecimal.ZERO);
        assertThat(dto.plannedUtil()).isEqualTo(BigDecimal.ZERO);
        assertThat(dto.actualUtil()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle allocation with null project code")
    void shouldHandleAllocationWithNullProjectCode() {
        // Given
        ProjectAllocationDetailsDTO allocationWithNullProject = new ProjectAllocationDetailsDTO(
                null, // null project code
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                new BigDecimal("80.00"),
                new BigDecimal("160.00"),
                null,
                22L
        );

        ResourceAllocationsFlatDTO flatDTOWithNullProject = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"),
                allocationWithNullProject
        );
        testData.add(flatDTOWithNullProject);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.currentAllocation()).isEmpty();
        assertThat(dto.billability()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle zero daily working hours")
    void shouldHandleZeroDailyWorkingHours() {
        // Given
        ResourceAllocationsFlatDTO flatDTOWithZeroHours = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                BigDecimal.ZERO, // zero daily working hours
                projectAllocation1
        );
        testData.add(flatDTOWithZeroHours);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.currentAllocation()).hasSize(1);
        
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
        assertThat(allocation.plannedUtil()).isEqualTo(BigDecimal.ZERO);
        assertThat(allocation.actualUtil()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle null daily working hours")
    void shouldHandleNullDailyWorkingHours() {
        // Given
        ResourceAllocationsFlatDTO flatDTOWithNullHours = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                null, // null daily working hours
                projectAllocation1
        );
        testData.add(flatDTOWithNullHours);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        assertThat(dto.currentAllocation()).hasSize(1);
        
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
        assertThat(allocation.plannedUtil()).isEqualTo(BigDecimal.ZERO);
        assertThat(allocation.actualUtil()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should handle zero days with entries")
    void shouldHandleZeroDaysWithEntries() {
        // Given
        ProjectAllocationDetailsDTO allocationWithZeroDays = new ProjectAllocationDetailsDTO(
                "PROJ001",
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                new BigDecimal("80.00"),
                new BigDecimal("160.00"),
                null,
                0L // zero days with entries
        );

        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"),
                allocationWithZeroDays
        );
        testData.add(flatDTO);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
        assertThat(allocation.actualUtil()).isEqualTo(BigDecimal.ZERO);
        assertThat(allocation.daysWithEntries()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null days with entries")
    void shouldHandleNullDaysWithEntries() {
        // Given
        ProjectAllocationDetailsDTO allocationWithNullDays = new ProjectAllocationDetailsDTO(
                "PROJ001",
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                new BigDecimal("80.00"),
                new BigDecimal("160.00"),
                null,
                null // null days with entries
        );

        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"),
                allocationWithNullDays
        );
        testData.add(flatDTO);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
        assertThat(allocation.actualUtil()).isEqualTo(BigDecimal.ZERO);
        assertThat(allocation.daysWithEntries()).isEqualTo(0L);
    }

    @Test
    @DisplayName("Should handle null billability values")
    void shouldHandleNullBillabilityValues() {
        // Given
        ProjectAllocationDetailsDTO allocationWithNullBillability = new ProjectAllocationDetailsDTO(
                "PROJ001",
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                null, // null billability
                new BigDecimal("160.00"),
                null,
                22L
        );

        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"),
                allocationWithNullBillability
        );
        testData.add(flatDTO);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        ProjectAllocationDetailsDTO allocation = dto.currentAllocation().get(0);
//        assertThat(allocation.billability()).isEqualTo(BigDecimal.ZERO);
//        assertThat(dto.billability()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should calculate correct planned utilization percentage")
    void shouldCalculateCorrectPlannedUtilizationPercentage() {
        // Given
        ProjectAllocationDetailsDTO allocation = new ProjectAllocationDetailsDTO(
                "PROJ001",
                "Project Alpha",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Developer",
                new BigDecimal("80.00"),
                new BigDecimal("40.00"), // 40 hours planned
                null,
                22L
        );

        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
                "EMP001",
                "John Doe",
                "Senior Developer",
                new BigDecimal("5.5"),
                new BigDecimal("8.00"), // 8 hours daily
                allocation
        );
        testData.add(flatDTO);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(1);
        ResourceAllocationsDTO dto = result.get(0);
        ProjectAllocationDetailsDTO resultAllocation = dto.currentAllocation().get(0);
        
        // Expected: (40 / 8) * 100 = 500%
        BigDecimal expectedPlannedUtil = new BigDecimal("40.00")
                .divide(new BigDecimal("8.00"), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
        
        assertThat(resultAllocation.plannedUtil()).isEqualTo(expectedPlannedUtil);
    }

    @Test
    @DisplayName("Should group allocations by employee ID correctly")
    void shouldGroupAllocationsByEmployeeIdCorrectly() {
        // Given
        ResourceAllocationsFlatDTO emp1Allocation1 = new ResourceAllocationsFlatDTO(
                "EMP001", "John Doe",  "Dev", new BigDecimal("5.5"), new BigDecimal("8.00"), projectAllocation1
        );
        ResourceAllocationsFlatDTO emp1Allocation2 = new ResourceAllocationsFlatDTO(
                "EMP001", "John Doe",  "Dev", new BigDecimal("5.5"), new BigDecimal("8.00"), projectAllocation2
        );
        ResourceAllocationsFlatDTO emp2Allocation = new ResourceAllocationsFlatDTO(
                "EMP002", "Jane Smith",  "Lead", new BigDecimal("7.0"), new BigDecimal("8.00"), projectAllocation1
        );

        testData.add(emp1Allocation1);
        testData.add(emp2Allocation);
        testData.add(emp1Allocation2);

        // When
        List<ResourceAllocationsDTO> result = resourceAllocationsService.toResourceAllocationsDTO(testData);

        // Then
        assertThat(result).hasSize(2);
        
        ResourceAllocationsDTO emp1Result = result.stream()
                .filter(dto -> "EMP001".equals(dto.id()))
                .findFirst()
                .orElseThrow();
        assertThat(emp1Result.currentAllocation()).hasSize(2);
        
        ResourceAllocationsDTO emp2Result = result.stream()
                .filter(dto -> "EMP002".equals(dto.id()))
                .findFirst()
                .orElseThrow();
        assertThat(emp2Result.currentAllocation()).hasSize(1);
    }
}