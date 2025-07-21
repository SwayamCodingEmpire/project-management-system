package com.cozentus.pms.serviceImpl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.ProjectManagerDetailsForTImesheetEmailDTO;
import com.cozentus.pms.dto.TimesheetSummaryToDMAndPMDTO;
import com.cozentus.pms.dto.UserCreationEmailDTO;
import com.cozentus.pms.services.EmailProcessingService;
@Service
public class EmailProcessingServiceImpl implements EmailProcessingService {
	private final SingleEmailServiceImpl singleEmailService;
	
	public EmailProcessingServiceImpl(SingleEmailServiceImpl singleEmailService) {
		this.singleEmailService = singleEmailService;
	}
	
	@Override
	@Async
	public CompletableFuture<Void> sendTimesheetSummaryToManagers(List<TimesheetSummaryToDMAndPMDTO> timesheetSummaryToManagers) {
	    for (TimesheetSummaryToDMAndPMDTO dmDto : timesheetSummaryToManagers) {
	       singleEmailService.sendTimesheetSummaryToDM(dmDto); 
	        for (ProjectManagerDetailsForTImesheetEmailDTO pmDto : dmDto.projectManagerDetailsForTImesheetEmailDTO()) {
	            singleEmailService.sendTimesheetSummaryToPM(pmDto); 
	        }
	    }
	    return CompletableFuture.completedFuture(null);
	}
	
//	@Async
//	public CompletableFuture<Void> senduserCreationEmail(List<UserCreationEmailDTO> userCreationEmailDTOs) {
//	}

}
