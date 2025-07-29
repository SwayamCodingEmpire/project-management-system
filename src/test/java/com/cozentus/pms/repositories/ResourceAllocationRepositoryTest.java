
package com.cozentus.pms.repositories;

import com.cozentus.pms.dto.*;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.helpers.Roles;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ResourceAllocationRepositoryTest {

    @Mock
    private ResourceAllocationRepository resourceAllocationRepository;

    @Test
    void testFindAllResourceAllocationsFlat() {
        when(resourceAllocationRepository.findAllResourceAllocationsFlat(any(Roles.class), any(Integer.class)))
                .thenReturn(Collections.singletonList(new ResourceAllocationsFlatDTO("empId", "name", "designation", BigDecimal.ONE, BigDecimal.ONE, new ProjectAllocationDetailsDTO("projectCode", "projectName", true, LocalDate.now(), LocalDate.now(), "role", BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 1L))));

        List<ResourceAllocationsFlatDTO> result = resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.DELIVERY_MANAGER,2);
        assertEquals(1, result.size());
    }

    // Add similar test methods for other methods in the repository

}
