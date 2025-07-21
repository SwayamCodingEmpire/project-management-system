package com.cozentus.pms.dto;

import java.time.LocalDate;
import java.util.List;

public record SingularTimesheetPayload(
	    LocalDate date,
	    List<DayWiseTimesheet> dayTimeSheet
	) {}