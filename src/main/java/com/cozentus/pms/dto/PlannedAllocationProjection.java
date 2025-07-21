package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface PlannedAllocationProjection {
    BigDecimal getPlannedHours();
    BigDecimal getDailyWorkingHours();  // new field
}
