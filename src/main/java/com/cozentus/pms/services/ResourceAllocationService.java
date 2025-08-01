package com.cozentus.pms.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cozentus.pms.dto.ProjectAllocationViewDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceAllocationsFlatDTO;
import com.cozentus.pms.dto.ResourceFilterDTO;
import com.cozentus.pms.dto.ResourceWeeklySummaryDTO;
import com.cozentus.pms.dto.UserSkillDetailsDTO;
import com.cozentus.pms.dto.UtilizationPairDTO;

public interface ResourceAllocationService {
	List<ResourceAllocationsDTO> getAllResourceAllocations();
	void allocateResources(ProjectResourceAllocationDTO projectResourceAllocationDTO);
	List<ResourceAllocationsDTO> searchAmongResources(ResourceFilterDTO resourceFilterDTO);
	 List<ResourceAllocationsDTO> toResourceAllocationsDTO(List<ResourceAllocationsFlatDTO> resourceAllocationsFlatDTO,  Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId);
	 ProjectAllocationViewDTO getProjectAllocationsViewDTO(String projectCode, Pageable pageable);
	 ResourceWeeklySummaryDTO getResourceProjectCountAndWeeklyHours();
	 List<UtilizationPairDTO> getResourceDashboardUtilStats(String empId, LocalDate startDate, LocalDate endDate);
	 void dellocateResource(String projectId, String empId);
	 void deallocateResourceFromDM(String resourceEmpId, Integer deliveryManagerId);
	 void allocateToDM(String resourceEmpId, Integer deliveryManagerId);
}
