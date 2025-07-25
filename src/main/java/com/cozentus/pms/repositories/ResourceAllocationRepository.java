package com.cozentus.pms.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.PlannedAllocationProjection;
import com.cozentus.pms.dto.ProjectAllocationsViewFlatDTO;
import com.cozentus.pms.dto.ResourceAllocationSummaryDTO;
import com.cozentus.pms.dto.ResourceAllocationsFlatDTO;
import com.cozentus.pms.dto.ResourceDetailsDTO;
import com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO;
import com.cozentus.pms.dto.ResourceWeeklySummaryDTO;
import com.cozentus.pms.dto.UtilizationPairDTO;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.helpers.Roles;

public interface ResourceAllocationRepository extends JpaRepository<ResourceAllocation, Integer> {

	@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceAllocationsFlatDTO(
			        u.empId,
			        u.name,
			        u.designation,
			        u.expInYears,
			        u.dailyWorkingHours,
			        new com.cozentus.pms.dto.ProjectAllocationDetailsDTO(
			            p.projectCode,
			            p.projectName,
			            pt.isCustomerProject,
			            a.allocationStartDate,
			            COALESCE(a.actualAllocationEndDate, a.allocationEndDate),
			            a.role,
			            a.billabilityPercent,
			            a.plannedHours,
			            SUM(CASE WHEN ts.approval = true THEN ts.hours ELSE 0 END),
			            COUNT(CASE WHEN ts.approval = true THEN ts.id ELSE null END)
			        )
			    )
			    FROM UserInfo u
			    
			    LEFT JOIN u.allocations a ON a.enabled = true
			    LEFT JOIN a.project p
			    ON p.id <> 1
			    LEFT JOIN a.timeSheets ts ON ts.enabled = true
			    LEFT JOIN p.projectType pt
			    WHERE u.enabled = true
			        AND u.credential.role = :role
			        AND u.credential.enabled = true
			        

			    GROUP BY
			        u.empId, u.name, u.designation, u.expInYears,
			        u.dailyWorkingHours,
			        p.projectCode, p.projectName, a.allocationStartDate, a.allocationEndDate,
			        a.actualAllocationEndDate, a.role, a.billabilityPercent, a.plannedHours, pt.isCustomerProject
			""")
	List<ResourceAllocationsFlatDTO> findAllResourceAllocationsFlat(Roles role);

	@Query("""
			      SELECT new com.cozentus.pms.dto.ResourceAllocationsFlatDTO(
			        u.empId,
			        u.name,
			        u.designation,
			        u.expInYears,
			        u.dailyWorkingHours,
			        new com.cozentus.pms.dto.ProjectAllocationDetailsDTO(
			            p.projectCode,
			            p.projectName,
			            pt.isCustomerProject,
			            a.allocationStartDate,
			            COALESCE(a.actualAllocationEndDate, a.allocationEndDate),
			            a.role,
			            a.billabilityPercent,
			            a.plannedHours,
			            SUM(CASE WHEN ts.approval = true THEN ts.hours ELSE 0 END),
			            COUNT(CASE WHEN ts.approval = true THEN ts.id ELSE null END)
			        )
			    )
			    FROM UserInfo u
			  
			    LEFT JOIN u.allocations a ON a.enabled = true
			    LEFT JOIN a.project p
			    ON p.id <> 1
			    LEFT JOIN a.timeSheets ts ON ts.enabled = true
			    LEFT JOIN p.projectType pt
			    WHERE u.enabled = true
			        AND u.credential.role = :role
			        AND u.credential.enabled = true
				    AND (:empIds IS NULL OR u.empId IN :empIds)
				    AND (:designations IS NULL OR u.designation IN :designations)
				    AND (:experience IS NULL OR u.expInYears >= :experience)
				    
			   GROUP BY
			        u.empId, u.name, u.designation, u.expInYears,
			        u.dailyWorkingHours,
			        p.projectCode, p.projectName, a.allocationStartDate, a.allocationEndDate,
			        a.actualAllocationEndDate, a.role, a.billabilityPercent, a.plannedHours, pt.isCustomerProject
			        ORDER BY u.expInYears, u.designation ASC
			""")
	List<ResourceAllocationsFlatDTO> searchResourceAllocations(Roles role, List<String> empIds,
			List<String> designations, BigDecimal experience);

	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(ra.id, p.projectCode) " + "FROM ResourceAllocation ra "
			+ "LEFT JOIN ra.project p " + "LEFT JOIN ra.resource r "
			+ "WHERE p.projectCode IN :projectCode AND r.empId = :resourceId AND ra.enabled = true AND ra.allocationCompleted = false")
	List<IdAndCodeDTO> fetchAllocationsIdForProjectCodeAndResourceId(List<String> projectCode, String resourceId);

	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(ra.id, p.projectCode) " + "FROM ResourceAllocation ra "
			+ "LEFT JOIN ra.project p " + "LEFT JOIN ra.resource r "
			+ "WHERE p.projectCode = :projectCode AND r.empId = :resourceId AND ra.enabled = true AND ra.allocationCompleted = false")
	IdAndCodeDTO fetchAllocationsIdForSingleProjectCodeAndResourceId(String projectCode, String resourceId);

	@Query("""
			    SELECT new com.cozentus.pms.dto.ProjectAllocationsViewFlatDTO(
			        p.projectCode,
			        p.projectName,
			        c.name,
			        new com.cozentus.pms.dto.ProjectResourceAllocationsWithoutSkillsDTO(
			        r.empId,
			        r.emailId,
			        r.name,
			        r.designation,
			        r.dailyWorkingHours,
			        COUNT(CASE WHEN ts.approval = true THEN ts.id ELSE null END),
			        a.allocationStartDate,
			        a.allocationEndDate,
			        COALESCE(a.actualAllocationEndDate, a.allocationEndDate),
			        a.role,
			        r.expInYears,
			        a.billabilityPercent,
			        a.plannedHours,
			        SUM(CASE WHEN ts.approval = true THEN ts.hours ELSE 0 END)
			        )
			    )
			    FROM ProjectDetails p
			    LEFT JOIN p.customer c
			    LEFT JOIN p.allocations a ON a.enabled = true
			    LEFT JOIN a.resource r
			    LEFT JOIN a.timeSheets ts ON ts.enabled = true
			    WHERE r.enabled = true
			        AND r.credential.role = :role
			        AND r.credential.enabled = true
			        AND p.projectCode = :projectCode

			    GROUP BY
			        r.empId, r.name, r.designation, r.expInYears,
			        r.dailyWorkingHours,
			        p.projectCode, p.projectName, a.allocationStartDate, a.allocationEndDate, c.name,
			        a.actualAllocationEndDate, a.role, a.billabilityPercent, a.plannedHours, r.emailId
			""")
	List<ProjectAllocationsViewFlatDTO> findAllResourceAllocationsForProject(Roles role, String projectCode);

	@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceAllocationSummaryDTO(

			        p.projectCode,
			        p.projectName,
			        r.empId,	
			        r.emailId,
			        r.name,
			        a.allocationStartDate,
			        a.allocationEndDate,
			        r.designation,
			        r.expInYears,
			        a.billabilityPercent,
			        r.dailyWorkingHours,
			        a.plannedHours
			    )
			    FROM ResourceAllocation a
			    LEFT JOIN a.resource r
			    LEFT JOIN a.project p
			    LEFT JOIN p.projectManager m
			    WHERE p.projectCode = :projectCode AND r.empId IN :empId AND a.enabled = true AND a.allocationCompleted = false AND a.actualAllocationEndDate IS NULL
			""")
	List<ResourceAllocationSummaryDTO> findResourceAllocationSummaryByProjectCodeAndEmpId(String projectCode,
			List<String> empId);
	


	@Query("SELECT new com.cozentus.pms.dto.ResourceDetailsDTO(r.name, r.emailId, p.projectName) FROM ResourceAllocation ra "
			+ "LEFT JOIN ra.resource r " + "LEFT JOIN ra.project p " + "WHERE ra.id = :allocationId")
	ResourceDetailsDTO findDetailsFromAllocationId(Integer allocationId);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO(
		        u.empId,
		        p.projectCode,
		        pt.isCustomerProject,
		        SUM(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.hours ELSE 0 END),
		        COUNT(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.id ELSE null END),
		        a.billabilityPercent,
		        a.plannedHours,
		        u.dailyWorkingHours
		    )
		    FROM UserInfo u
		    LEFT JOIN u.allocations a ON a.enabled = true
		    LEFT JOIN a.project p
		    LEFT JOIN p.projectType pt
		    LEFT JOIN a.timeSheets ts
		    WHERE u.enabled = true
		      AND u.credential.role = :role
		      AND u.credential.enabled = true 
		      AND p.projectManager.empId = :empId
		    GROUP BY u.empId, p.projectCode, pt.isCustomerProject, a.billabilityPercent, a.plannedHours, u.dailyWorkingHours
		""")
		List<ResourceProjectUtilizationSummaryDTO> findResourceUtilizationSummaryForPm(Roles role, String empId);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO(
		        u.empId,
		        p.projectCode,
		        pt.isCustomerProject,
		        SUM(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.hours ELSE 0 END),
		        COUNT(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.id ELSE null END),
		        a.billabilityPercent,
		        a.plannedHours,
		        u.dailyWorkingHours
		    )
		    FROM UserInfo u
		    LEFT JOIN u.allocations a ON a.enabled = true
		    LEFT JOIN a.project p
		    LEFT JOIN p.projectType pt
		    LEFT JOIN a.timeSheets ts
		    WHERE u.enabled = true
		      AND u.credential.role = :role
		      AND u.credential.enabled = true 
		      AND p.deliveryManager.empId = :empId
		    GROUP BY u.empId, p.projectCode, pt.isCustomerProject, a.billabilityPercent, a.plannedHours, u.dailyWorkingHours
		""")
		List<ResourceProjectUtilizationSummaryDTO> findResourceUtilizationSummaryForDM(Roles role, String empId);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO(
		        u.empId,
		        p.projectCode,
		        pt.isCustomerProject,
		        SUM(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.hours ELSE 0 END),
		        COUNT(CASE WHEN ts.enabled = true AND ts.approval = true THEN ts.id ELSE null END),
		        a.billabilityPercent,
		        a.plannedHours,
		        u.dailyWorkingHours
		    )
		    FROM UserInfo u
		    LEFT JOIN u.allocations a ON a.enabled = true
		    LEFT JOIN a.project p
		    LEFT JOIN p.projectType pt
		    LEFT JOIN a.timeSheets ts
		    WHERE u.enabled = true
		      AND u.credential.role = :role
		      AND u.credential.enabled = true 
		      AND p.projectManager.empId = :empId
		    GROUP BY u.empId, p.projectCode, pt.isCustomerProject, a.billabilityPercent, a.plannedHours, u.dailyWorkingHours
		""")
		List<ResourceProjectUtilizationSummaryDTO> findResourceUtilizationSummaryForPM(Roles role, String empId);
	
	@Query("""
		    SELECT COUNT(u)
		    FROM UserInfo u
		    LEFT JOIN u.allocations a ON a.enabled = true
		    WHERE u.enabled = true
		      AND u.credential.enabled = true
		      AND u.credential.role = :role
		    GROUP BY u.empId
		    HAVING SUM(COALESCE(a.plannedHours, 0)) = 0
		""")
		Long countUsersWithNoOrZeroPlannedAllocations(Roles role);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceWeeklySummaryDTO(
		        COUNT(DISTINCT ra.project.id),
		        COALESCE(SUM(ts.hours), 0)
		    )
		    FROM ResourceAllocation ra
		    LEFT JOIN TimeSheet ts ON ts.resourceAllocation.id = ra.id
		                             AND ts.date BETWEEN :startOfWeek AND :endOfWeek
		                             AND ts.enabled = true
		    WHERE ra.resource.id = :userId
		    AND ra.allocationCompleted = false
		""")
		ResourceWeeklySummaryDTO getWeeklySummaryForUser(@Param("userId") Integer userId,
		                                               @Param("startOfWeek") LocalDate startOfWeek,
		                                               @Param("endOfWeek") LocalDate endOfWeek);
	
	
	@Query("""
		    SELECT 
		           ra.plannedHours AS plannedHours,
		           ra.resource.dailyWorkingHours AS dailyWorkingHours
		    FROM ResourceAllocation ra
		    WHERE ra.resource.id = :resourceId
		      AND ra.enabled = true
		      AND ra.allocationCompleted = false
		""")
		List<PlannedAllocationProjection> findPlannedAllocationsWithWorkingHours(
		    @Param("endDate") LocalDate endDate
		);
	
	
	@Query(value = """
		    SELECT 
		        new com.cozentus.pms.dto.UtilizationPairDTO(
		            ts.date,
		            COALESCE(SUM(a.plannedHours) / NULLIF(u.dailyWorkingHours, 0), 0) * 100,
		            COALESCE(SUM(CASE WHEN ts.enabled = true THEN ts.hours ELSE 0 END) / NULLIF(u.dailyWorkingHours, 0), 0) * 100
		        )
		    FROM UserInfo u
		    JOIN u.allocations a ON a.enabled = true
		    LEFT JOIN a.timeSheets ts ON ts.enabled = true AND ts.date BETWEEN :startDate AND :endDate
		    WHERE u.empId = :empId AND u.enabled = true AND u.credential.enabled = true AND a.project.id <> 1
		    GROUP BY ts.date, u.dailyWorkingHours
		    ORDER BY ts.date
		    """)
		List<UtilizationPairDTO> fetchDailyUtilization(@Param("empId") String empId,
		                                                @Param("startDate") LocalDate startDate,
		                                                @Param("endDate") LocalDate endDate);

	@Transactional
	@Modifying
	@Query("UPDATE ResourceAllocation ra "
			+ "SET ra.allocationCompleted = true "
			+ "WHERE ra.resource.empId IN :empId AND ra.project.id = 1")
	int markAllocationsAsCompletedForResourceInBench(@Param("empId") List<String> empId);
	
	
	@Modifying
	@Query("UPDATE ResourceAllocation ra "
			+ "SET ra.allocationCompleted = true, ra.actualAllocationEndDate = :allocationEndDate "
			+ "WHERE ra.resource.empId = :empId AND ra.project.projectCode = :projectCode")
	int markAllocationsAsCompletedForResource(@Param("empId") String empId, @Param("projectCode") String projectCode, LocalDate allocationEndDate);
	
	
	@Query("""
		    SELECT COUNT(ra) FROM ResourceAllocation ra
		    WHERE ra.resource.empId = :empId
		    AND ra.project.id <> :excludedProjectId
		    AND ra.allocationCompleted = false
		    AND ra.enabled = true
		""")
		long countActiveAllocationsExcludingProject(String empId, Integer excludedProjectId);
	
	@Modifying
	@Query("""
	    UPDATE ResourceAllocation ra
	    SET ra.allocationCompleted = true
	    WHERE ra.resource.empId = :empId
	    AND ra.project.id = :targetProjectId
	""")
	int markAllocationAsCompleted(String empId, Integer targetProjectId);


	
	

	
	






}
