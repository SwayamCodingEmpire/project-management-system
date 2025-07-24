package com.cozentus.pms.services;

import com.cozentus.pms.dto.DMResourceStatsDTO;
import com.cozentus.pms.dto.UtilizationBreakdownDTO;

public interface DMDashboardService {
	DMResourceStatsDTO getResourceBillabilityStats();
	UtilizationBreakdownDTO computeUtilizationBreakdown();
	DMResourceStatsDTO getResourceBillabilityStatsModified();

}
