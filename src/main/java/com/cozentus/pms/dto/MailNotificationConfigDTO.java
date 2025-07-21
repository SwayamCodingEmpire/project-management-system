package com.cozentus.pms.dto;

import jakarta.validation.constraints.NotBlank;

public record MailNotificationConfigDTO(
		@NotBlank
		String timesheetSummaryDay,
		@NotBlank
	    String timesheetReminderDay,
	    @NotBlank
	    String timesheetWarningDay1,
	    @NotBlank
	    String timesheetWarningDay2
		) {

}
