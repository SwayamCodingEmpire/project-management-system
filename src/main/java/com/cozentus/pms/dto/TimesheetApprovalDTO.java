package com.cozentus.pms.dto;

import java.time.LocalDate;

public record TimesheetApprovalDTO(
    String resourceId,
    String projectId,
    LocalDate startDate,
    LocalDate endDate,
    boolean approve
) {}
