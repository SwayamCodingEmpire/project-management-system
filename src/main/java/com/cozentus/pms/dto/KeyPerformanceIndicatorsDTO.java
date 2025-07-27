package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

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

	    System.out.println(totalResources);
	    BigDecimal billed = dmStats.totalBillability()
	        .setScale(2, RoundingMode.HALF_UP); // Ensures billed is 2-decimal rounded

	    System.out.println(billed);
	    int notBilled = totalResources - billed.setScale(0, RoundingMode.HALF_UP).intValue(); // for integer diff

	    System.out.println(notBilled);
	    // Helper for rounding util values
	    Function<BigDecimal, BigDecimal> round = val ->
	        val == null ? null : val.setScale(2, RoundingMode.HALF_UP);

	    return new KeyPerformanceIndicatorsDTO(
	        totalResources,
	        new BilledNotBilledDTO(billed, notBilled),
	        round.apply(utilStats.actualUtilCustomer()),
	        round.apply(utilStats.plannedUtilCustomer()),
	        round.apply(utilStats.plannedUtilInternal()),
	        round.apply(utilStats.actualUtilInternal()),
	        dmStats.zeroOrNoPlannedUtilisation().intValue()
	    );
	}


}
