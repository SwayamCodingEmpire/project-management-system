package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectAllocationDetailsDTO(
    String projectCode,
    String projectName,
    Boolean isCustomer,
    LocalDate from,
    LocalDate to,
    String role,
    BigDecimal billability,
    BigDecimal plannedUtil,
    BigDecimal actualUtil, 
    Long daysWithEntries
) {}
