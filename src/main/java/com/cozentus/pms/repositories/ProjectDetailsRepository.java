package com.cozentus.pms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.MailNotificationConfigDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ProjectManagerProjectCountDTO;
import com.cozentus.pms.dto.ProjectMinimalDataDTO;
import com.cozentus.pms.dto.ProjectResourceSummaryCountDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownDTO;
import com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO;
import com.cozentus.pms.dto.TimesheetSubmissionEmailDTO;
import com.cozentus.pms.entites.ProjectDetails;

public interface ProjectDetailsRepository extends JpaRepository<ProjectDetails, Integer> {

	// Custom query methods can be defined here if needed
	// For example:
	// List<ProjectDetails> findByProjectName(String projectName);
	// Optional<ProjectDetails> findById(Integer id);
	
	// Additional methods can be added as per requirements
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectDetailsForProjectListDTO(
		        p.projectCode,
		        p.projectName,
		        c.name,
		        p.currency,
		        p.startDate,
		        p.endDate,
		        pm.name,
		        pt.projectType
		    )
		    FROM ProjectDetails p
		    LEFT JOIN p.projectType pt
		    LEFT JOIN p.customer c
		    LEFT JOIN p.deliveryManager u
		    LEFT JOIN p.projectManager pm
		    WHERE u.id = :dmId
		      AND p.enabled = true
		      AND (
		          :search IS NULL OR :search = '' OR (
		              LOWER(p.projectCode) LIKE %:search%
		              OR LOWER(p.projectName) LIKE %:search%
		              OR LOWER(c.name) LIKE %:search%
		              OR LOWER(p.currency) LIKE %:search%
		              OR LOWER(u.name) LIKE %:search%
		              OR LOWER(FUNCTION('DATE_FORMAT', p.startDate, '%b %d, %Y')) LIKE %:search%
              OR LOWER(FUNCTION('DATE_FORMAT', p.endDate, '%b %d, %Y')) LIKE %:search%
		          )
		      )
		""")
		Page<ProjectDetailsForProjectListDTO> findAllProjectsForDeliveryManager(
		    @Param("dmId") int dmId,
		    @Param("search") String search,
		    Pageable pageable
		);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectDetailsForProjectListDTO(
		        p.projectCode,
		        p.projectName,
		        c.name,
		        p.currency,
		        p.startDate,
		        p.endDate,
		        dm.name,
		        pt.projectType
		    )
		    FROM ProjectDetails p
		    LEFT JOIN p.projectType pt
		    LEFT JOIN p.customer c
		    LEFT JOIN p.projectManager u
		    LEFT JOIN p.deliveryManager dm
		    WHERE u.id = :pmId
		      AND p.enabled = true
		      AND (
		          :search IS NULL OR :search = '' OR (
		              LOWER(p.projectCode) LIKE %:search%
		              OR LOWER(p.projectName) LIKE %:search%
		              OR LOWER(c.name) LIKE %:search%
		              OR LOWER(p.currency) LIKE %:search%
		              OR LOWER(u.name) LIKE %:search%
		              OR LOWER(FUNCTION('DATE_FORMAT', p.startDate, '%b %d, %Y')) LIKE %:search%
              OR LOWER(FUNCTION('DATE_FORMAT', p.endDate, '%b %d, %Y')) LIKE %:search%
		          )
		      )
		""")
		Page<ProjectDetailsForProjectListDTO> findAllProjectsForProjectManager(
		    int pmId,
		    @Param("search") String search,
		    Pageable pageable
		);


	
	@Query("" +
			"SELECT new com.cozentus.pms.dto.ProjectDetailsForProjectListDTO(" +
			"p.projectCode, p.projectName, c.name, p.currency, p.startDate, p.endDate, u.name, pt.projectType) " +
			"FROM ProjectDetails p " +
			"LEFT JOIN p.projectType pt " +
			"LEFT JOIN p.customer c " +
			"LEFT JOIN p.projectManager u " +
			"WHERE u.id = :pmId AND p.enabled = true")
	List<ProjectDetailsForProjectListDTO> findAllProjectsForProjectManager(int pmId);
	
	
	@Query("" +
			"SELECT new com.cozentus.pms.dto.ProjectDTO(" +
			"new com.cozentus.pms.dto.ProjectDetailsDTO(p.projectCode, p.projectName, p.projectDescription, p.startDate, p.endDate,p.currency, p.contractType, p.billingFrequency), " +
			"new com.cozentus.pms.dto.ProjectTypeDTO(pt.isCustomerProject, pt.id), " +
			"new com.cozentus.pms.dto.ClientDTO(c.id, c.name, c.legalEntity, c.businessUnit), "+
			"pm.empId) " +
			"FROM ProjectDetails p " +
			"LEFT JOIN p.projectType pt " +
			"LEFT JOIN p.customer c " + 
			"LEFT JOIN p.projectManager pm " +
			"WHERE p.projectCode = :code AND p.enabled = true")
	Optional<ProjectDTO> findByProjectCodeForEditForm(String code);
	
	
	Optional<ProjectDetails> findByProjectCode(String projectCode);
	
	@Query("SELECT p.id FROM ProjectDetails p WHERE p.projectCode = :projectCode AND p.enabled = true")
	Optional<Long> findIdByProjectCode(String projectCode);
	
	@Query("SELECT new com.cozentus.pms.dto.TimesheetSubmissionEmailDTO( " +
			"p.projectCode, p.projectName, u.name, u.empId, u.emailId) " +
			"FROM ProjectDetails p " +
			"LEFT JOIN p.projectManager u " +
			"WHERE p.enabled = true AND p.projectCode IN :projectCode")
	List<TimesheetSubmissionEmailDTO> findTimesheetSubmissionEmailDetailsByProjectCode(@Param("projectCode") List<String> projectCode);
	
	@Query("SELECT p.projectCode FROM ProjectDetails p WHERE p.timesheetSummaryDay = :day AND p.enabled = true")
	List<String> findAllProjectCodesByTimesheetSummaryDay(String day);
	
	@Query("SELECT p.projectCode FROM ProjectDetails p WHERE p.timesheetWarningDay1 = :day AND p.enabled = true")
	List<String> findAllProjectCodesByWarningMailDay1(String day);
	
	@Query("SELECT p.projectCode FROM ProjectDetails p WHERE p.timesheetWarningDay2 = :day AND p.enabled = true")
	List<String> findAllProjectCodesByWarningMailDay2(String day);
	
	Optional<MailNotificationConfigDTO> findProjectDetailsByProjectCode(String projectCode);
	
	@Transactional
	@Modifying
	@Query("UPDATE ProjectDetails p " +
			"SET p.timesheetSummaryDay = :timesheetSummaryDay, " +
			"p.timesheetWarningDay1 = :timesheetWarningDay1, " +
			"p.timesheetWarningDay2 = :timesheetWarningDay2, " +
			"p.timesheetReminderDay = :timesheetReminderDay " +
			"WHERE p.projectCode = :projectCode AND p.enabled = true")
	int updateMailConfigByProjectCode(String projectCode, String timesheetSummaryDay, String timesheetWarningDay1, String timesheetWarningDay2, String timesheetReminderDay);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE ProjectDetails p " +
			"SET p.timesheetSummaryDay = :timesheetSummaryDay, " +
			"p.timesheetWarningDay1 = :timesheetWarningDay1, " +
			"p.timesheetWarningDay2 = :timesheetWarningDay2, " +
			"p.timesheetReminderDay = :timesheetReminderDay " +
			"WHERE p.enabled = true")
	int updateDefaultMailConfig(String timesheetSummaryDay, String timesheetWarningDay1, String timesheetWarningDay2, String timesheetReminderDay);
	
	@Query("SELECT new com.cozentus.pms.dto.ProjectTypeDropdownDTO(pt.id, pt.projectType, pt.isCustomerProject) " +
			"FROM ProjectType pt  WHERE pt.id <> 1" +
			"ORDER BY pt.projectType")
	List<ProjectTypeDropdownDTO> findAllProjectTypes();
	
	
	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(p.id, p.projectCode) " +
			"FROM ProjectDetails p " +
			"WHERE p.projectCode = :projectCode AND p.enabled = true")
	Optional<IdAndCodeDTO> findIdAndCodeByProjectCode(String projectCode);
	
	
	@Query(value = """
		    SELECT
		        p.project_code AS code,
		        p.project_name AS name,
		        c.name AS customer,
		        pm.name AS manager,
		        AVG(ra.billability_percent) AS billability,
		        COUNT(DISTINCT ra.resource_id) AS totalResources,
		        AVG(
		            CASE
		                WHEN e.daily_working_duration_in_hrs IS NULL OR ra.planned_hours IS NULL OR e.daily_working_duration_in_hrs = 0 THEN 0
		                ELSE (ra.planned_hours / e.daily_working_duration_in_hrs) * 100
		            END
		        ) AS plannedUtilization,
		        AVG(
		            CASE
		                WHEN e.daily_working_duration_in_hrs IS NULL OR e.daily_working_duration_in_hrs = 0 OR ra.id IS NULL THEN 0
		                ELSE (
		                    (
		                        SELECT COALESCE(SUM(ts.hours),0)*100
		                        FROM time_sheet ts
		                        WHERE ts.resource_alloc_id = ra.id
		                    )
		                    /
		                    (
		                        e.daily_working_duration_in_hrs *
		                        COALESCE(
		                            (SELECT COUNT(*) FROM time_sheet ts2 WHERE ts2.resource_alloc_id = ra.id),
		                            1
		                        )
		                    )
		                )
		            END
		        ) AS actualUtilization
		    FROM
		        proj_details p
		        JOIN client c ON p.customer_id = c.id
		        JOIN user_info dm ON p.delivery_manager_id = dm.id
		        JOIN user_info pm ON p.project_manager_id = pm.id
		        LEFT JOIN resource_allocation ra ON ra.project_id = p.id
		        LEFT JOIN user_info e ON ra.resource_id = e.id
		    WHERE dm.id = :dmId
		    GROUP BY
		        p.project_code, p.project_name, c.name, dm.id, pm.name
		    ORDER BY
		        p.project_code
		    """, nativeQuery = true)
		List<ProjectDashboardDTO> findAllDashboardData(@Param("dmId") Integer dmId);
	
	
	@Query(
		    value = """
		        SELECT
		            p.project_code AS code,
		            p.project_name AS name,
		            c.name AS customer,
		            dm.name AS manager,
		            AVG(ra.billability_percent) AS billability,
		            COUNT(DISTINCT ra.resource_id) AS totalResources,
		            AVG(
		                CASE
		                    WHEN e.daily_working_duration_in_hrs IS NULL OR ra.planned_hours IS NULL OR e.daily_working_duration_in_hrs = 0 THEN 0
		                    ELSE (ra.planned_hours / e.daily_working_duration_in_hrs) * 100
		                END
		            ) AS plannedUtilization,
		            AVG(
		                CASE
		                    WHEN e.daily_working_duration_in_hrs IS NULL OR e.daily_working_duration_in_hrs = 0 OR ra.id IS NULL THEN 0
		                    ELSE (
		                        (
		                            SELECT COALESCE(SUM(ts.hours), 0) * 100
		                            FROM time_sheet ts
		                            WHERE ts.resource_alloc_id = ra.id
		                        )
		                        /
		                        (
		                            e.daily_working_duration_in_hrs *
		                            COALESCE(
		                                (SELECT COUNT(*) FROM time_sheet ts2 WHERE ts2.resource_alloc_id = ra.id),
		                                1
		                            )
		                        )
		                    )
		                END
		            ) AS actualUtilization
		        FROM
		            proj_details p
		            JOIN client c ON p.customer_id = c.id
		            JOIN user_info dm ON p.delivery_manager_id = dm.id
		            LEFT JOIN resource_allocation ra ON ra.project_id = p.id
		            LEFT JOIN user_info e ON ra.resource_id = e.id
		        WHERE
		            p.project_manager_id = :projectManagerId
		        GROUP BY
		            p.project_code, p.project_name, c.name, dm.name
		        ORDER BY
		            p.project_code
		        """,
		    nativeQuery = true
		)
		List<ProjectDashboardDTO> getPmDashboardProjectListByManager(@Param("projectManagerId") Integer projectManagerId);
	
	@Query("""
		    SELECT 
		        pm.empId AS empId,
		        pm.name AS name,
		        COUNT(p.id) AS projectCount
		    FROM ProjectDetails p
		    JOIN p.projectManager pm
		    WHERE p.deliveryManager.empId = :dmEmpId
		    GROUP BY pm.empId, pm.name
		    ORDER BY pm.name
		""")
		List<ProjectManagerProjectCountDTO> findProjectManagersByDeliveryManager(@Param("dmEmpId") String dmEmpId);
	
	
	@Query("""
		    SELECT 
		        p.projectCode AS code,
		        p.projectName AS name,
		        p.customer.name AS customer,
		        COUNT(DISTINCT ra.resource.id) AS resources
		    FROM ProjectDetails p
		    LEFT JOIN p.allocations ra
		    WHERE p.projectManager.empId = :empId
		    AND p.deliveryManager.empId = :dmEmpId
		    GROUP BY p.projectCode, p.projectName, p.customer.name
		    ORDER BY p.projectCode
		""")
		List<ProjectMinimalDataDTO> findProjectMinimalDataByDeliveryManager(@Param("empId") String empId, String dmEmpId);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectResourceSummaryCountDTO(
		        p.projectCode,
		        p.projectName,
		        p.customer.name,
		        COUNT(DISTINCT ra.resource.id),
		        pm.empId
		    )
		    FROM ProjectDetails p
		    JOIN p.projectManager pm
		    LEFT JOIN p.allocations ra
		    WHERE pm.empId = :projectManagerId
		    GROUP BY p.projectCode, p.projectName, p.customer.name, pm.empId
		    ORDER BY p.projectCode
		""")
		List<ProjectResourceSummaryCountDTO> getProjectResourceSummaryByManager(@Param("projectManagerId") String projectManagerId);
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO(
		        e.empId,
		        e.name,
		        ra.role,
		        (ra.plannedHours/e.dailyWorkingHours) * 100
		    )
		    FROM ProjectDetails p
		    JOIN p.allocations ra
		    JOIN ra.resource e
		    WHERE p.projectCode = :projectCode AND p.enabled = true
		""")
	List<ResourceProjectMinimalDashboardDTO> findResourceProjectMinimalDashboardByProjectCode(String projectCode);

	
	@Query("""
			SELECT new com.cozentus.pms.dto.ProjectDetailsForProjectListDTO(
				p.projectCode,
				p.projectName,
				c.name,
				p.currency,
				p.startDate,
				p.endDate,
				u.name,
				pt.projectType
			)
			FROM ResourceAllocation ra
			JOIN ra.project p
			JOIN p.projectType pt
			JOIN p.customer c
			JOIN p.projectManager u
			WHERE ra.resource.id = :resourceId AND p.enabled = true	AND ra.allocationCompleted = false		
			""")
	List<ProjectDetailsForProjectListDTO> findAllProjectsForResource(Integer resourceId);







}
