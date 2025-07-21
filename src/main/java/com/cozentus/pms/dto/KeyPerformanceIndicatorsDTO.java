package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record KeyPerformanceIndicatorsDTO(
		 int totalResources,
		    BilledNotBilledDTO billedNotBilled,
		    BigDecimal customerActualUtilization,
		    BigDecimal customerPlannedUtilization,
		    BigDecimal nonCustomerPlannedUtilization,
		    BigDecimal nonCustomerActualUtilization,
		    int nonUtilizedResources
		) {
	 public static KeyPerformanceIndicatorsDTO from(DMResourceStatsDTO dmStats, UtilizationBreakdownDTO utilStats) {
	        int totalResources = dmStats.totalResourceUsers().intValue();

	        BigDecimal billed = dmStats.totalBillability()
	            .multiply(BigDecimal.valueOf(totalResources))
	            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

	        int notBilled = totalResources - billed.setScale(0, RoundingMode.HALF_UP).intValue();

	        return new KeyPerformanceIndicatorsDTO(
	            totalResources,
	            new BilledNotBilledDTO(billed, notBilled),
	            utilStats.actualUtilCustomer(),
	            utilStats.plannedUtilCustomer(),
	            utilStats.plannedUtilInternal(),
	            utilStats.actualUtilInternal(),
	            dmStats.zeroOrNoPlannedUtilisation().intValue()
	        );
	    }

}
