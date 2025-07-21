package com.cozentus.pms.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.TimesheetFlatDTO;
import com.cozentus.pms.dto.TimesheetForManagerFlatDTO;
import com.cozentus.pms.dto.TimesheetSummaryDTO;
import com.cozentus.pms.entites.TimeSheet;
import com.cozentus.pms.helpers.ApprovalStatus;

public interface TimeSheetRepository extends JpaRepository<TimeSheet, Integer> {

	// Custom query methods can be defined here if needed
	// For example:
	// List<TimeSheet> findByUserId(Integer userId);
	// Optional<TimeSheet> findById(Integer id);
	
	// Additional methods can be added as per requirements
	
	@Query("SELECT new com.cozentus.pms.dto.TimesheetFlatDTO("
			+ "p.projectCode, "
			+ "p.projectName, "
			+ "new com.cozentus.pms.dto.ProjectTimeSheetDTO("
			+ "ts.date, "
			+ "ts.approvalStatus, "
			+ "ts.attendanceStatus, "
			+ "ts.hours "
			+ ")) " +
		   "FROM ResourceAllocation r " +
		   "LEFT JOIN r.timeSheets ts " +
		   "LEFT JOIN r.project p " +
		   "WHERE r.resource.empId = :empId AND "
		   + "(:projectCode IS NULL OR :projectCode = '' OR p.projectCode = :projectCode) ")
	List<TimesheetFlatDTO> findAllTimesheetByEmpIdAndDateBetween(String empId, LocalDate startDate, LocalDate endDate, String projectCode);
	
	
	@Query("SELECT ts " +
		   "FROM TimeSheet ts " +
		   "WHERE ts.resourceAllocation.id IN :resourceAllocationIds AND ts.date = :date AND ts.enabled = true")
	List<TimeSheet> findAllByResourceAllocationIdAndDate(List<Integer> resourceAllocationIds, LocalDate date);
	
	List<TimeSheet> findByResourceAllocation_IdInAndDateAndEnabledTrue(List<Integer> ids, LocalDate date);
	
	
	@Query("SELECT new com.cozentus.pms.dto.TimesheetSummaryDTO("
			+ "r.empId, "
			+ "r.name, "
			+ "r.role, "
			+ "p.projectCode, "
			+ "p.projectName, "
			+ "ts.approvalStatus, "
			+ "SUM(ts.hours) "
			+ ") "
			+ "FROM TimeSheet ts "
			+ "LEFT JOIN ts.resourceAllocation ra "
			+ "LEFT JOIN ra.project p "
			+ "LEFT JOIN p.projectManager pm "
			+ "LEFT JOIN ra.resource r "
			+ "WHERE pm.empId = :projectManagerId AND ts.date BETWEEN :startDate AND :endDate "
			+ "GROUP BY p.projectCode, r.empId, r.name, r.role, ts.approvalStatus ")
	List<TimesheetSummaryDTO> findTimeSheetSummaryByProjectManagerIdAndDateBetween(
			String projectManagerId, LocalDate startDate, LocalDate endDate);
	
	
	
	
	@Query("SELECT new com.cozentus.pms.dto.TimesheetForManagerFlatDTO("
			+ "r.empId, "
			+ "p.projectCode, "
			+ "p.projectName, "
			+ "new com.cozentus.pms.dto.ProjectTimeSheetDTO("
			+ "ts.date, "
			+ "ts.approvalStatus, "
			+ "ts.attendanceStatus, "
			+ "ts.hours "
			+ ")) " +
		   "FROM ResourceAllocation ra " +
		   "LEFT JOIN ra.timeSheets ts " +
		   "LEFT JOIN ra.project p " +
		   "LEFT JOIN ra.resource r " +
		   "WHERE r.empId = :resourceId AND p.projectManager.empId = :managerId AND ts.date BETWEEN :startDate AND :endDate AND ts.enabled = true AND "
		   + "(:projectCode IS NULL OR :projectCode = '' OR p.projectCode = :projectCode) ")
	List<TimesheetForManagerFlatDTO> findAllTimesheetByEmpIdAndProjectCodeAndManagerIdDateBetween(String resourceId, String managerId, LocalDate startDate, LocalDate endDate, String projectCode);
	
	List<TimeSheet> findByResourceAllocation_IdInAndDateBetweenAndEnabledTrue(List<Integer> ids, LocalDate startDate, LocalDate endDate);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE TimeSheet ts "
			+ "SET ts.approval = :approval, ts.approvalStatus = :approvalStatus, ts.updatedBy = :updatedBy "
			+ "WHERE ts.resourceAllocation.id = :resourceAllocationId AND ts.date BETWEEN :startDate AND :endDate AND ts.enabled = true")
	int approvetimesheetByAllocationIdAndDateAndEnabledTrue(Integer resourceAllocationId, LocalDate startDate, LocalDate endDate, boolean approval, ApprovalStatus approvalStatus, String updatedBy);

	
	

	
	
	


}
