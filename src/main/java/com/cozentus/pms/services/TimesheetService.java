package com.cozentus.pms.services;

import java.time.LocalDate;
import java.util.List;

import com.cozentus.pms.dto.SingularTimesheetPayload;
import com.cozentus.pms.dto.TimesheetApprovalDTO;
import com.cozentus.pms.dto.TimesheetDTO;
import com.cozentus.pms.dto.TimesheetSummaryDTO;

public interface TimesheetService {
	List<TimesheetDTO> getTimeSheetByEmpId(String resourceId, LocalDate startDate, LocalDate endDate);
	void saveTimesheet(SingularTimesheetPayload singularTimesheetPayload, String resourceId);
	
	List<TimesheetSummaryDTO> getTimeSheetSummaryByManagerId(String resourceId, LocalDate startDate, LocalDate endDate);
	
	 TimesheetDTO getTimeSheetByManagerIdAndresourceIdAndProjectCode(
		        String resourceId,
		        LocalDate startDate,
		        LocalDate endDate,
		        String projectCode,
		        String managerId
		);
	void submitTimesheet(List<TimesheetDTO> timesheetDTOs, String resourceId);
	
	void approval(TimesheetApprovalDTO timesheetApprovalDTO);

}
