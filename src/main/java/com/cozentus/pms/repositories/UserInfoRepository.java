package com.cozentus.pms.repositories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.DMResourceStatsDTO;
import com.cozentus.pms.dto.DMResourceStatsPartialDTO;
import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.ProjectManagerFlatDTO;
import com.cozentus.pms.dto.ProjectTimesheetForEmailDTO;
import com.cozentus.pms.dto.ReportingManagerDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.ResourceBasics;
import com.cozentus.pms.dto.ResourceFlatDTO;
import com.cozentus.pms.dto.UserInfoIdentifierDTO;
import com.cozentus.pms.dto.UserProjectTimesheetReminderDTO;
import com.cozentus.pms.dto.UserSingleSkillDTO;
import com.cozentus.pms.dto.UserSkillDetailsDTO;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.helpers.ApprovalStatus;
import com.cozentus.pms.helpers.Roles;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
	
	@Query("SELECT u FROM UserInfo u LEFT JOIN u.credential c WHERE c.enabled = true AND c.username = :username")
	Optional<UserInfo> findByUsernameAndEnabledTrue(String username);
	
	@Query("SELECT u.name FROM UserInfo u LEFT JOIN u.credential c WHERE c.enabled = true AND c.username = :username")
	Optional<String> findNameByUsername(String username);
	
	List<UserInfo> findByEmpIdIn(Set<String> empIds);

	// Custom query methods can be defined here if needed
	// For example:
	// List<UserInfo> findByUsername(String username);
	// Optional<UserInfo> findById(Integer id);

	// Additional methods can be added as per requirements

//	@Query("""
//		    SELECT u FROM UserInfo u
//		    LEFT JOIN FETCH u.credential c
//		    LEFT JOIN FETCH u.managedProjects
//		    WHERE u.enabled = true AND c.role = :role
//		""")
//		List<UserInfo> findAllEnabledManagersWithProjects(Roles role);


	@Query("""
			    SELECT u.empId AS empId, u.name AS name, u.emailId AS emailId, p.projectName AS projectName, p.projectType AS projectType
			    FROM UserInfo u
			    LEFT JOIN u.credential c
			    LEFT JOIN u.managedProjects p
			    WHERE u.enabled = true AND c.role = :role
			""")
	List<ProjectManagerFlatDTO> findAllProjectManagersAndProjectNames(Roles role);
	


	Optional<UserInfo> findByEmpId(String empId);
	@Query("""
		    SELECT new com.cozentus.pms.dto.ResourceFlatDTO(
		        e.empId,
		        e.name,
		        e.emailId,
		        e.phoneNo,
		        e.designation,
		        e.expInYears,
		        e.role,
		        rm.empId,
		        rm.name,
		        new com.cozentus.pms.dto.ProjectAllocationDTO(
		            p.projectCode,
		            p.projectName,
		            p.projectDescription,
		            a.allocationStartDate,
		            a.allocationEndDate,
		            pm.name,
		            pt.projectType
		        )
		    )
		    FROM UserInfo e
		    LEFT JOIN e.credential c
		    LEFT JOIN e.reportingManager rm
		    LEFT JOIN e.allocations a 
		        ON a.allocationCompleted = false AND a.enabled = true
		    LEFT JOIN a.project p 
		        ON p.id <> 1
		    LEFT JOIN p.projectManager pm 
		        ON pm.enabled = true
		    LEFT JOIN p.projectType pt
		    WHERE e.enabled = true 
		      AND c.role = :role
		      AND (
		            :search IS NULL OR
		            LOWER(e.empId) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(e.emailId) LIKE LOWER(CONCAT('%', :search, '%')) OR 
		            LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(e.designation) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            CAST(e.expInYears AS string) LIKE CONCAT('%', :search, '%') OR
		            LOWER(rm.empId) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(rm.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(p.projectName) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(p.projectDescription) LIKE LOWER(CONCAT('%', :search, '%')) OR
		            LOWER(pm.name) LIKE LOWER(CONCAT('%', :search, '%'))
		      )
		""")
		List<ResourceFlatDTO> findAllResourcesWithAllocations(String search, Roles role);







	List<ReportingManagerDTO> findAllByEnabledTrue();

	boolean existsByEmpId(String empId);

	@Query("SELECT u.id FROM UserInfo u WHERE u.empId = :empId AND u.enabled = true")
	Optional<Long> findIdByEmpId(String empId);

	@Query("SELECT new com.cozentus.pms.dto.UserInfoIdentifierDTO(u.id, u.name, u.emailId, c.role) FROM UserInfo u LEFT JOIN credential c WHERE u.empId = :empId AND u.enabled = true")
	Optional<UserInfoIdentifierDTO> findBasicsByEmpId(String empId);
	
	
	@Transactional
	@Modifying
	@Query("UPDATE UserInfo u SET u.role = :role WHERE u.empId = :empId")
	int updateSkillsByEmpId(String empId, String role);
	
//	@Query("""
//		    SELECT new com.cozentus.pms.dto.IdAndCodeDTO(u.id, u.empId)
//		    FROM UserInfo u
//		    WHERE u.empId IN :empIds AND u.enabled = true
//		""")
//		List<IdAndCodeDTO> findAllByEmpIdIn(List<String> empIds);
//	
//	
//	List<UserSkillDTO> findAllUserInfoByEnabledTrueAndPrimarySkillNotNullAndSecondarySkillNotNullOrderByEmpId();
	
	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(u.id, u.empId) FROM UserInfo u WHERE u.empId = :empId AND u.enabled = true")
	Optional<IdAndCodeDTO> findIdAndEmpIdByEmpId(String empId);
	
	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(u.id, u.empId) FROM UserInfo u WHERE u.empId IN :empId AND u.enabled = true")
	List<IdAndCodeDTO> findIdAndEmpIdByEmpIdIn(List<String> empId);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserSingleSkillDTO(u.empId, s.skillName)
		    FROM UserSkillDetail usd
		    JOIN usd.user u
		    JOIN usd.skill s
		""")
		List<UserSingleSkillDTO> fetchFlatUserSkills();
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserSingleSkillDTO(u.empId, s.skillName)
		    FROM UserSkillDetail usd
		    JOIN usd.user u
		    JOIN usd.skill s
		    WHERE u.empId = :empId
		""")
		List<UserSingleSkillDTO> fetchFlatUserSkillsByEmpID(String empId);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserSingleSkillDTO(u.empId, s.skillName)
		    FROM UserSkillDetail usd
		    JOIN usd.user u
		    JOIN usd.skill s
		    WHERE u.empId IN :empId
		""")
		List<UserSingleSkillDTO> fetchFlatUserSkillsByEmpIDIn(List<String> empId);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserSkillDetailsDTO(u.empId, s.skillName, usd.experienceInYears,usd.priority,  usd.level)
		    FROM UserSkillDetail usd
		    JOIN usd.user u
		    JOIN usd.skill s
		    WHERE ((u.empId IS NULL) OR (u.empId IN :empId))
		""")
		List<UserSkillDetailsDTO> fetchFlatUserSkillsByEmpIdIn(List<String> empId);




	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserProjectTimesheetReminderDTO(
		    u.empId,
		    u.name,
		    u.emailId,
		    p.projectCode,
		    p.projectName,
		    pm.name,
		    pm.emailId,
		    pm.phoneNo,
		    pm.empId
		    ) 
		    FROM UserInfo u
		    JOIN u.credential c
		    JOIN u.allocations a
		    JOIN a.project p
		    JOIN p.projectManager pm 
		    WHERE a.allocationCompleted = false AND a.enabled = true AND u.enabled = true AND c.role = :role
		    AND c.enabled = true 
		    AND p.timesheetReminderDay = :day
		""")
	List<UserProjectTimesheetReminderDTO> findAllResourcesWithPendingAllocations(Roles role, String day);
	
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectTimesheetForEmailDTO(
		        dm.name,
		        dm.empId,
		        dm.emailId,
		        pm.name,
		        pm.empId,
		        pm.emailId,
		        dp.projectCode, 
		        dp.projectName,
		        r.empId,
		        r.emailId,
		        r.name,
		        r.role,
		        a.role,
		        a.allocationStartDate,
		        a.allocationEndDate
		    )
		    FROM UserInfo dm
		    JOIN dm.deliveredProjects dp
		    JOIN dp.projectManager pm
		    JOIN dp.allocations a
		    JOIN a.resource r
		    JOIN a.timeSheets ts
		    WHERE r.enabled = true
		      AND dm.credential.role = :role
		      AND dm.credential.enabled = true
		      AND a.enabled = true
		      AND ts.date BETWEEN :startDate AND :endDate
		      AND ts.approvalStatus = :status
		      AND dp.projectCode IN :projectCodes
		""")

	List<ProjectTimesheetForEmailDTO> findAllProjectTimesheetForEmailByResourceIdAndProjectCode(Roles role, ApprovalStatus status, LocalDate startDate, LocalDate endDate, List<String> projectCodes);

	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserProjectTimesheetReminderDTO(
		    u.empId,
		    u.name,
		    u.emailId,
		    p.projectCode,
		    p.projectName,
		    pm.name,
		    pm.emailId,
		    pm.phoneNo,
		    pm.empId
		    ) 
		    FROM UserInfo u
		    JOIN u.credential c
		    JOIN u.allocations a
		    JOIN a.project p
		    JOIN p.projectManager pm 
		    LEFT JOIN a.timeSheets ts
		    WHERE a.allocationCompleted = false AND a.enabled = true AND u.enabled = true AND c.role = :role
		    AND c.enabled = true 
		    AND p.timesheetWarningDay1 = :day
		    AND p.projectCode IN :projectCodes
			AND (
            ts IS NULL
            OR (
              ts.date BETWEEN :startDate AND :endDate
              AND ts.approvalStatus = :status
              AND ts.enabled = true
             )
           )
		    
		""")
	List<UserProjectTimesheetReminderDTO> findAllResourcesWithPendingAllocationsFor1stWarning(Roles role, String day, List<String> projectCodes, LocalDate startDate, LocalDate endDate, ApprovalStatus status);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.UserProjectTimesheetReminderDTO(
		    u.empId,
		    u.name,
		    u.emailId,
		    p.projectCode,
		    p.projectName,
		    pm.name,
		    pm.emailId,
		    pm.phoneNo,
		    pm.empId
		    ) 
		    FROM UserInfo u
		    JOIN u.credential c
		    JOIN u.allocations a
		    JOIN a.project p
		    JOIN p.projectManager pm 
		    LEFT JOIN a.timeSheets ts
		    WHERE a.allocationCompleted = false AND a.enabled = true AND u.enabled = true AND c.role = :role
		    AND c.enabled = true 
		    AND p.timesheetWarningDay2 = :day
		    AND p.projectCode IN :projectCodes
			AND (
            ts IS NULL
            OR (
              ts.date BETWEEN :startDate AND :endDate
              AND ts.approvalStatus = :status
              AND ts.enabled = true
             )
           )
		""")
	List<UserProjectTimesheetReminderDTO> findAllResourcesWithPendingAllocationsFor2ndWarning(Roles role, String day, List<String> projectCodes, LocalDate startDate, LocalDate endDate, ApprovalStatus status);


	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectManagerFlatDTO(
		        u.empId, u.name, u.emailId, p.projectName, p.projectCode
		    )
		    FROM UserInfo u
		    LEFT JOIN u.managedProjects p
		    WHERE u.enabled = true
		""")
		List<ProjectManagerFlatDTO> findFromManaged();


	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectManagerFlatDTO(
		        u.empId, u.name, u.emailId, dp.projectName, dp.projectCode
		    )
		    FROM UserInfo u
		    LEFT JOIN u.deliveredProjects dp
		    WHERE u.enabled = true
		""")
		List<ProjectManagerFlatDTO> findFromDelivered();

	@Query("""
		    SELECT new com.cozentus.pms.dto.ProjectManagerFlatDTO(
		        u.empId, u.name, u.emailId, rp.projectName, rp.projectCode
		    )
		    FROM UserInfo u
		    JOIN u.allocations a
		    LEFT JOIN a.project rp
		    WHERE u.enabled = true AND a.allocationCompleted = true
		""")
		List<ProjectManagerFlatDTO> findFromAllocated();


		
		default List<ProjectManagerFlatDTO> findAllResourcesWithProjectNames() {
		    List<ProjectManagerFlatDTO> combined = new ArrayList<>();
		    combined.addAll(findFromManaged());
		    combined.addAll(findFromDelivered());
		    combined.addAll(findFromAllocated());
		    return combined;
		}
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.DMResourceStatsDTO(
			        SUM(COALESCE(a.billabilityPercent, 0))/100,
			        COUNT(DISTINCT u.id),
			        COUNT(DISTINCT CASE 
			            WHEN (a.id IS NULL OR COALESCE(a.billabilityPercent, 0) = 0) THEN u .id
			            ELSE NULL 
			        END),
			        COUNT(DISTINCT CASE 
			            WHEN a.id IS NULL THEN u.id
			            WHEN COALESCE(a.plannedHours, 0) = 0 THEN u.id
			            ELSE NULL
			        END)
			    )
			    FROM UserInfo u
			    JOIN u.allocations a
			    WHERE u.credential.role = :resourceRole AND u.enabled = true AND u.credential.enabled = true
			    AND a.project.deliveryManager.empId = :empId AND a.project.id <> 1
			""")
			DMResourceStatsDTO getResourceStatsCombined(Roles resourceRole, String empId);
		
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.DMResourceStatsDTO(
			        SUM(COALESCE(a.billabilityPercent, 0))/100,
			        COUNT(DISTINCT u.id),
			        COUNT(DISTINCT CASE 
			            WHEN (a.id IS NULL OR COALESCE(a.billabilityPercent, 0) = 0) THEN u .id
			            ELSE NULL 
			        END),
			        COUNT(DISTINCT CASE 
			            WHEN a.id IS NULL THEN u.id
			            WHEN COALESCE(a.plannedHours, 0) = 0 THEN u.id
			            ELSE NULL
			        END)
			    )
			    FROM UserInfo u
			    LEFT JOIN u.allocations a
			    WHERE u.credential.role = :resourceRole AND u.enabled = true AND u.credential.enabled = true
			    AND a.project.projectManager.empId = :empId
			""")
			DMResourceStatsDTO getResourceStatsCombinedForPM(Roles resourceRole, String empId);
		
		
		

		@Query("SELECT DISTINCT u.designation FROM UserInfo u")
		List<String> findAllDesignations();
		
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceBasicDTO(
			        u.name, u.empId, u.designation, u.expInYears, CAST(AVG(COALESCE(a.plannedHours,0)) AS BigDecimal), u.dailyWorkingHours 
			    )
			    FROM UserInfo u 
			    LEFT JOIN u.allocations a 
			    WHERE EXISTS ( 
			        SELECT 1 
			        FROM UserSkillDetail usd 
			        JOIN usd.skill s 
			        WHERE usd.user.id = u.id 
			          AND s.skillName = :skillName 
			          AND usd.level = :level 
			    )  
			    GROUP BY u.name, u.empId, u.designation, u.expInYears, u.dailyWorkingHours  
			""") 
			List<ResourceBasicDTO> findAllResourcesWithSkillsAndLevels(String skillName, String level);
		
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceBasicDTO(
			        u.name, u.empId, u.designation, u.expInYears, CAST(AVG(COALESCE(a.plannedHours,0)) AS BigDecimal), u.dailyWorkingHours 
			    )
			    FROM UserInfo u 
			    LEFT JOIN u.allocations a 
			    WHERE EXISTS ( 
			        SELECT 1 
			        FROM UserSkillDetail usd 
			        JOIN usd.skill s 
			        WHERE usd.user.id = u.id 
			          AND s.skillName = :skillName 
			          AND usd.level = :level 
			    ) AND a.project.projectManager.empId = :empId
			    GROUP BY u.name, u.empId, u.designation, u.expInYears, u.dailyWorkingHours  
			""") 
			List<ResourceBasicDTO> findAllResourcesWithSkillsAndLevelsforPM(String skillName, String level, String empId);
		
		@Query("SELECT u FROM UserInfo u LEFT JOIN FETCH u.reportingManager")
		List<UserInfo> findAllWithManagers();
		
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceBasicDTO(
			        u.name, u.empId, u.designation, u.expInYears, CAST(AVG(COALESCE(a.plannedHours,0)) AS BigDecimal), u.dailyWorkingHours 
			    )
			    FROM UserInfo u 
			    LEFT JOIN u.allocations a 
			    WHERE EXISTS ( 
			        SELECT 1 
			        FROM UserSkillDetail usd 
			        JOIN usd.skill s 
			        WHERE usd.user.id = u.id 
			          AND s.skillName = :skillName 
			          AND usd.level = :level 
			          AND usd.user.empId IN :empId
			    )  
			    GROUP BY u.name, u.empId, u.designation, u.expInYears, u.dailyWorkingHours  
			""") 
			List<ResourceBasicDTO> findAllResourcesWithSkillsAndLevelsByEmpId(String skillName, String level, List<String> empId) ;
		
		
		
		@Query("""
			    SELECT new com.cozentus.pms.dto.ResourceBasicDTO(
			        u.name, u.empId, u.designation, u.expInYears, CAST(AVG(COALESCE(a.plannedHours,0)) AS BigDecimal), u.dailyWorkingHours 
			    )
			    FROM UserInfo u 
			    LEFT JOIN u.allocations a 
			    WHERE EXISTS ( 
			        SELECT 1 
			        FROM UserSkillDetail usd 
			        JOIN usd.skill s 
			        WHERE usd.user.id = u.id 
			          AND s.skillName = :skillName 
			          AND usd.level = :level 
			          AND usd.user.empId IN :empIdList
			    ) AND a.project.projectManager.empId = :empId
			    GROUP BY u.name, u.empId, u.designation, u.expInYears, u.dailyWorkingHours  
			""") 
			List<ResourceBasicDTO> findAllResourcesWithSkillsAndLevelsforPMByEmpIdIn(String skillName, String level, String empId, List<String> empIdList) ;

		@Query("SELECT new com.cozentus.pms.dto.ResourceBasics(u.empId, u.name, s.skillName, usd.level) " +
			       "FROM UserSkillDetail usd " +
			       "JOIN usd.user u " +
			       "JOIN usd.skill s")
			Set<ResourceBasics> findAllResourceSkillLevel();
		
		
		@Query("""
				SELECT new com.cozentus.pms.dto.DMResourceStatsPartialDTO(
				    SUM(COALESCE(a.billabilityPercent, 0))/100,
				    COUNT(DISTINCT u.id),
				    COUNT(DISTINCT CASE
				        WHEN (a.id IS NULL OR COALESCE(a.billabilityPercent, 0) = 0) THEN u.id
				        ELSE NULL
				    END)
				)
				FROM UserInfo u
				LEFT JOIN u.allocations a
				WHERE u.credential.role = :resourceRole AND u.enabled = true AND u.credential.enabled = true
				AND a.project.deliveryManager.empId = :empId
				""")
				DMResourceStatsPartialDTO getResourceStatsDMSpecific(Roles resourceRole, String empId);

		
		@Query("""
				SELECT COUNT(DISTINCT CASE
				    WHEN a.id IS NULL THEN u.id
				    WHEN COALESCE(a.plannedHours, 0) = 0 THEN u.id
				    WHEN (a.project.id = 1 AND a.allocationCompleted = false) THEN u.id
				    ELSE NULL
				END)
				FROM UserInfo u
				LEFT JOIN u.allocations a
				WHERE u.credential.role = :resourceRole AND u.enabled = true AND u.credential.enabled = true
				""")
				Long getGlobalZeroPlannedUtilizationCount(Roles resourceRole);
		
		
		@Query("""
				SELECT new com.cozentus.pms.dto.DMResourceStatsPartialDTO(
				    SUM(COALESCE(a.billabilityPercent, 0))/100,
				    COUNT(DISTINCT u.id),
				    COUNT(DISTINCT CASE
				        WHEN (a.id IS NULL OR COALESCE(a.billabilityPercent, 0) = 0) THEN u.id
				        ELSE NULL
				    END)
				)
				FROM UserInfo u
				LEFT JOIN u.allocations a
				WHERE u.credential.role = :resourceRole AND u.enabled = true AND u.credential.enabled = true
				AND a.project.projectManager.empId = :empId
				""")
				DMResourceStatsPartialDTO getResourceStatsPMSpecific(Roles resourceRole, String empId);
		
		@Query("SELECT new com.cozentus.pms.dto.ResourceBasics(u.empId, u.name, s.skillName, usd.level) " +
			       "FROM UserSkillDetail usd " +
			       "JOIN usd.user u " +
			       "JOIN usd.skill s " +
			       "WHERE u.reportingManager.empId = :dmEmpId")
			Set<ResourceBasics> findAllResourceSkillLevelForDM(String dmEmpId);
	 
			// 2. PM ke resources ke liye
			@Query("SELECT new com.cozentus.pms.dto.ResourceBasics(u.empId, u.name, s.skillName, usd.level) " +
			       "FROM UserSkillDetail usd " +
			       "JOIN usd.user u " +
			       "JOIN usd.skill s " +
			       "JOIN u.allocations ra " +
			       "JOIN ra.project p " +
			       "WHERE p.projectManager.empId = :pmEmpId")
			Set<ResourceBasics> findAllResourceSkillLevelForPM(String pmEmpId);

}
