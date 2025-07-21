package com.cozentus.pms.services;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.cozentus.pms.dto.TimesheetSummaryToDMAndPMDTO;

public interface EmailProcessingService {
	CompletableFuture<Void> sendTimesheetSummaryToManagers(List<TimesheetSummaryToDMAndPMDTO> timesheetSummaryToDMAndPMDTO);

}
