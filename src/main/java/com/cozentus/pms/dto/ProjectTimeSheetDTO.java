package com.cozentus.pms.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.cozentus.pms.helpers.ApprovalStatus;
	
	public record ProjectTimeSheetDTO(
			   LocalDate date,
			   ApprovalStatus approvalStatus,
			   Boolean attendanceStatus,
			    BigDecimal hoursWorked) {
	
	}
