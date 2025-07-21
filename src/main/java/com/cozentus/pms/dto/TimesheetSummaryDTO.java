package com.cozentus.pms.dto;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.ApprovalStatus;

public record TimesheetSummaryDTO(
    String resourceId,
    String resourceName,
    String role,
    String projectCode,
    String projectName,
    ApprovalStatus status,
    BigDecimal totalHours
) {}

