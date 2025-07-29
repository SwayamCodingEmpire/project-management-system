
package com.cozentus.pms.controllers;

import com.cozentus.pms.dto.ProjectAllocationViewDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceFilterDTO;
import com.cozentus.pms.services.ResourceAllocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
public class ResourceAllocationControllerTest {

    @Autowired
    private ResourceAllocationController resourceAllocationController;

    @MockBean
    private ResourceAllocationService resourceAllocationService;

    @Test
    public void testGetAllResourceAllocations() {
        Mockito.when(resourceAllocationService.getAllResourceAllocations())
                .thenReturn(Collections.singletonList(null));
        ResponseEntity<List<ResourceAllocationsDTO>> response = resourceAllocationController.getAllResourceAllocations();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testSubmitResourceAllocations() {
        Mockito.doNothing().when(resourceAllocationService).allocateResources(any(ProjectResourceAllocationDTO.class));
        ResponseEntity<String> response = resourceAllocationController.submitResourceAllocations(null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Resource allocations submitted successfully", response.getBody());
    }

    @Test
    public void testSearchAmongResources() {
        Mockito.when(resourceAllocationService.searchAmongResources(any(ResourceFilterDTO.class)))
                .thenReturn(Collections.singletonList(null));
        ResponseEntity<List<ResourceAllocationsDTO>> response = resourceAllocationController.seacrhAmongResources(null);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetProjectAllocationsViewDTO() {
        Pageable pageable = PageRequest.of(0, 5);
        Mockito.when(resourceAllocationService.getProjectAllocationsViewDTO(anyString(), any(Pageable.class)))
                .thenReturn(null);
        ResponseEntity<ProjectAllocationViewDTO> response = resourceAllocationController.getProjectAllocationsViewDTO("projectCode", 0, 5);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testDeleteResourceAllocation() {
        Mockito.doNothing().when(resourceAllocationService).dellocateResource(anyString(), anyString());
        ResponseEntity<String> response = resourceAllocationController.deleteResourceAllocation("projectCode", "empId");
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Resource allocation deleted successfully", response.getBody());
    }
}
