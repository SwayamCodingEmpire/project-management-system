package com.cozentus.pms.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.BenchResourceDTO;
import com.cozentus.pms.dto.BenchResourceWithLastDateDTO;
import com.cozentus.pms.dto.DMResourceStatsDTO;
import com.cozentus.pms.dto.DMResourceStatsPartialDTO;
import com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO;
import com.cozentus.pms.dto.UtilizationBreakdownDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.AuthenticationService;

@SpringBootTest
@SpringJUnitConfig
class DMDashboardServiceImplTest {

    @MockBean
    private UserInfoRepository userInfoRepository;
    
    @MockBean
    private ResourceAllocationRepository resourceAllocationRepository;
    
    @MockBean
    private AuthenticationService authenticationService;

    // Let Spring inject the actual service with mocked dependencies
    @org.springframework.beans.factory.annotation.Autowired
    private DMDashboardServiceImpl dmDashboardService;

    private final String DM_EMP_ID = "DM123";
    private final String PM_EMP_ID = "PM456";

    // Mock these instead of creating real instances
    private UserAuthDetails dmAuthDetails;
    private UserAuthDetails pmAuthDetails;

    @BeforeEach
    void setUp() {
        // Create properly mocked UserAuthDetails
        dmAuthDetails = mock(UserAuthDetails.class);
        when(dmAuthDetails.empId()).thenReturn(DM_EMP_ID);
        when(dmAuthDetails.emailId()).thenReturn("dm@cozentus.com");
        
        pmAuthDetails = mock(UserAuthDetails.class);
        when(pmAuthDetails.empId()).thenReturn(PM_EMP_ID);
        when(pmAuthDetails.emailId()).thenReturn("pm@cozentus.com");
    }

    @Test
    void testGetResourceBillabilityStats_AsDeliveryManager_ReturnsCorrectStats() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        
        long benchCount = 5L;
        DMResourceStatsDTO repoStats = new DMResourceStatsDTO(BigDecimal.valueOf(12.5), 10L, 2L, 0L);
        
        when(resourceAllocationRepository.findBenchResourcesCount(DM_EMP_ID, Roles.RESOURCE)).thenReturn(benchCount);
        when(userInfoRepository.getResourceStatsCombined(Roles.RESOURCE, DM_EMP_ID)).thenReturn(repoStats);

        // Act
        DMResourceStatsDTO result = dmDashboardService.getResourceBillabilityStats();

        // Assert
        assertNotNull(result);
        assertEquals(repoStats.totalBillability(), result.totalBillability());
        assertEquals(repoStats.totalResourceUsers(), result.totalResourceUsers());
        assertEquals(repoStats.zeroOrNoBillabilityUsers(), result.zeroOrNoBillabilityUsers());
        assertEquals(benchCount, result.zeroOrNoPlannedUtilisation());
    }

    @Test
    void testComputeUtilizationBreakdown_GroupsAndCalculatesCorrectly() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);

        List<ResourceProjectUtilizationSummaryDTO> rows = Arrays.asList(
                new ResourceProjectUtilizationSummaryDTO("E1", "P1", true, BigDecimal.valueOf(8), 2L, BigDecimal.valueOf(50), BigDecimal.valueOf(8), BigDecimal.valueOf(8)),
                new ResourceProjectUtilizationSummaryDTO("E2", "P2", false, BigDecimal.valueOf(4), 1L, BigDecimal.valueOf(25), BigDecimal.valueOf(4), BigDecimal.valueOf(8))
        );
        when(resourceAllocationRepository.findResourceUtilizationSummaryForDM(Roles.RESOURCE, DM_EMP_ID)).thenReturn(rows);

        // Act
        UtilizationBreakdownDTO result = dmDashboardService.computeUtilizationBreakdown();

        // Assert
        assertNotNull(result);
        assertNotNull(result.plannedUtilCustomer());
        assertNotNull(result.plannedUtilInternal());
        assertNotNull(result.actualUtilCustomer());
        assertNotNull(result.actualUtilInternal());
    }

    @Test
    void testGetNonUnitilizedResources_ReturnsBenchResourcesWithCorrectDays() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);

        BenchResourceWithLastDateDTO benchResource = mock(BenchResourceWithLastDateDTO.class);
        when(benchResource.id()).thenReturn("E1");
        when(benchResource.name()).thenReturn("Bench User");
        when(benchResource.previousProject()).thenReturn("OldProject");
        when(benchResource.date()).thenReturn(LocalDate.now().minusDays(10));

        when(resourceAllocationRepository.findBenchResourcesWithLastDate(DM_EMP_ID, Roles.RESOURCE))
                .thenReturn(List.of(benchResource));

        // Act
        List<BenchResourceDTO> result = dmDashboardService.getNonUnitilizedResources();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        BenchResourceDTO dto = result.get(0);
        assertEquals("E1", dto.id());
        assertEquals("Bench User", dto.name());
        assertEquals("OldProject", dto.previousProject());
        assertEquals(10, dto.daysOnBench());
    }

    @Test
    void testGetResourceBillabilityStats_NoAllocations_HandledGracefully() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        
        when(resourceAllocationRepository.findBenchResourcesCount(DM_EMP_ID, Roles.RESOURCE)).thenReturn(0L);
        DMResourceStatsDTO repoStats = new DMResourceStatsDTO(BigDecimal.ZERO, 0L, 0L, 0L);
        when(userInfoRepository.getResourceStatsCombined(Roles.RESOURCE, DM_EMP_ID)).thenReturn(repoStats);

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            DMResourceStatsDTO result = dmDashboardService.getResourceBillabilityStats();
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.totalBillability());
            assertEquals(0L, result.totalResourceUsers());
            assertEquals(0L, result.zeroOrNoBillabilityUsers());
            assertEquals(0L, result.zeroOrNoPlannedUtilisation());
        });
    }

    @Test
    void testComputeUtilizationBreakdown_NoProjects_ReturnsZeroes() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        when(resourceAllocationRepository.findResourceUtilizationSummaryForDM(Roles.RESOURCE, DM_EMP_ID))
                .thenReturn(Collections.emptyList());

        // Act & Assert - should not throw
        assertDoesNotThrow(() -> {
            UtilizationBreakdownDTO result = dmDashboardService.computeUtilizationBreakdown();
            assertNotNull(result);
            assertEquals(BigDecimal.ZERO, result.plannedUtilCustomer());
            assertEquals(BigDecimal.ZERO, result.actualUtilCustomer());
            assertEquals(BigDecimal.ZERO, result.plannedUtilInternal());
            assertEquals(BigDecimal.ZERO, result.actualUtilInternal());
        });
    }

    @Test
    void testGetNonUnitilizedResources_EmptyRepository_ReturnsEmptyList() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        when(resourceAllocationRepository.findBenchResourcesWithLastDate(DM_EMP_ID, Roles.RESOURCE))
                .thenReturn(Collections.emptyList());

        // Act
        List<BenchResourceDTO> result = dmDashboardService.getNonUnitilizedResources();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetResourceBillabilityStats_AsProjectManager_ReturnsCorrectStats() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.PROJECT_MANAGER, pmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        
        DMResourceStatsDTO repoStats = new DMResourceStatsDTO(BigDecimal.valueOf(20), 8L, 1L, 3L);
        when(userInfoRepository.getResourceStatsCombinedForPM(Roles.RESOURCE, PM_EMP_ID)).thenReturn(repoStats);

        // Act
        DMResourceStatsDTO result = dmDashboardService.getResourceBillabilityStats();

        // Assert
        assertNotNull(result);
        assertEquals(repoStats.totalBillability(), result.totalBillability());
        assertEquals(repoStats.totalResourceUsers(), result.totalResourceUsers());
        assertEquals(repoStats.zeroOrNoBillabilityUsers(), result.zeroOrNoBillabilityUsers());
        assertEquals(repoStats.zeroOrNoPlannedUtilisation(), result.zeroOrNoPlannedUtilisation());
    }

    @Test
    void testGetResourceBillabilityStatsModified_AsDeliveryManager_ReturnsCorrectStats() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        
        DMResourceStatsPartialDTO partialDTO = new DMResourceStatsPartialDTO(BigDecimal.valueOf(15), 7L, 2L);
        when(userInfoRepository.getResourceStatsDMSpecific(Roles.RESOURCE, DM_EMP_ID)).thenReturn(partialDTO);
        when(userInfoRepository.getGlobalZeroPlannedUtilizationCount(Roles.RESOURCE)).thenReturn(4L);

        // Act
        DMResourceStatsDTO result = dmDashboardService.getResourceBillabilityStatsModified();

        // Assert
        assertNotNull(result);
        assertEquals(partialDTO.totalBillability(), result.totalBillability());
        assertEquals(partialDTO.totalResourceUsers(), result.totalResourceUsers());
        assertEquals(partialDTO.zeroOrNoBillabilityUsers(), result.zeroOrNoBillabilityUsers());
        assertEquals(4L, result.zeroOrNoPlannedUtilisation());
    }

    @Test
    void testGetResourceBillabilityStatsModified_AsProjectManager_ReturnsCorrectStats() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.PROJECT_MANAGER, pmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        
        DMResourceStatsPartialDTO partialDTO = new DMResourceStatsPartialDTO(BigDecimal.valueOf(22), 9L, 3L);
        when(userInfoRepository.getResourceStatsPMSpecific(Roles.RESOURCE, PM_EMP_ID)).thenReturn(partialDTO);
        when(userInfoRepository.getGlobalZeroPlannedUtilizationCount(Roles.RESOURCE)).thenReturn(2L);

        // Act
        DMResourceStatsDTO result = dmDashboardService.getResourceBillabilityStatsModified();

        // Assert
        assertNotNull(result);
        assertEquals(partialDTO.totalBillability(), result.totalBillability());
        assertEquals(partialDTO.totalResourceUsers(), result.totalResourceUsers());
        assertEquals(partialDTO.zeroOrNoBillabilityUsers(), result.zeroOrNoBillabilityUsers());
        assertEquals(2L, result.zeroOrNoPlannedUtilisation());
    }




    

    @Test
    void testGetNonUnitilizedResources_NullOrMissingFields_HandledGracefully() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);

        BenchResourceWithLastDateDTO benchResource = mock(BenchResourceWithLastDateDTO.class);
        when(benchResource.id()).thenReturn(null);
        when(benchResource.name()).thenReturn(null);
        when(benchResource.previousProject()).thenReturn(null);
        when(benchResource.date()).thenReturn(LocalDate.now().minusDays(5));

        when(resourceAllocationRepository.findBenchResourcesWithLastDate(DM_EMP_ID, Roles.RESOURCE))
                .thenReturn(List.of(benchResource));

        // Act & Assert
        assertDoesNotThrow(() -> {
            List<BenchResourceDTO> result = dmDashboardService.getNonUnitilizedResources();
            assertNotNull(result);
        });
    }

    @Test
    void testComputeUtilizationBreakdown_WithCustomerAndInternalProjects() {
        // Arrange
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, dmAuthDetails);
        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);

        List<ResourceProjectUtilizationSummaryDTO> rows = Arrays.asList(
                new ResourceProjectUtilizationSummaryDTO("E1", "P1", true, BigDecimal.valueOf(8), 2L, BigDecimal.valueOf(50), BigDecimal.valueOf(8), BigDecimal.valueOf(8)),
                new ResourceProjectUtilizationSummaryDTO("E2", "P2", false, BigDecimal.valueOf(4), 1L, BigDecimal.valueOf(25), BigDecimal.valueOf(4), BigDecimal.valueOf(8))
        );
        when(resourceAllocationRepository.findResourceUtilizationSummaryForDM(Roles.RESOURCE, DM_EMP_ID)).thenReturn(rows);

        // Act
        UtilizationBreakdownDTO result = dmDashboardService.computeUtilizationBreakdown();

        // Assert
        assertNotNull(result);
        assertTrue(result.plannedUtilCustomer().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.plannedUtilInternal().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.actualUtilCustomer().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(result.actualUtilInternal().compareTo(BigDecimal.ZERO) >= 0);
    }
}