package com.cozentus.pms.services;

import java.util.List;

import com.cozentus.pms.dto.BenchResourceDTO;
import com.cozentus.pms.dto.DMResourceStatsDTO;
import com.cozentus.pms.dto.UtilizationBreakdownDTO;

public interface DMDashboardService {
	DMResourceStatsDTO getResourceBillabilityStats();
	UtilizationBreakdownDTO computeUtilizationBreakdown();
	DMResourceStatsDTO getResourceBillabilityStatsModified();
	List<BenchResourceDTO> getNonUnitilizedResources();

}
