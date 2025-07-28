package com.cozentus.pms.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.SkillCountByNameDTO;
import com.cozentus.pms.dto.SkillExperienceDTO;
import com.cozentus.pms.dto.UserSkillDetailsWithNameDTO;
import com.cozentus.pms.entites.Skill;

public interface SkillRepository extends JpaRepository<Skill, Integer> {
	
	// Custom query to find skills by name
	List<Skill> findBySkillName(String skillName);
	
	// Custom query to find skills by a list of names
	List<Skill> findBySkillNameIn(Set<String> skillNames);
	
	// Custom query to find all distinct skill names
	@Query("SELECT DISTINCT s.skillName FROM Skill s")
	List<String> findAllDistinctSkillNames();
	
	@Query("SELECT new com.cozentus.pms.dto.IdAndCodeDTO(s.id, s.skillName) FROM Skill s WHERE s.skillName = :skillName")
	Optional<IdAndCodeDTO> findIdAndNameBySkillsName(String skillName);
	
	@Query("""
		    SELECT DISTINCT new com.cozentus.pms.dto.UserSkillDetailsWithNameDTO(
		        s.skillName, u.empId, usd.level
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    JOIN u.allocations a
		    WHERE usd.level IS NOT NULL
		      AND a.allocationCompleted = false
		""")
		List<UserSkillDetailsWithNameDTO> findAllSkillsWithNames();
	
	
	@Query("""
		    SELECT DISTINCT new com.cozentus.pms.dto.UserSkillDetailsWithNameDTO(
		        s.skillName, u.empId, usd.level
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    JOIN u.allocations a
		    WHERE usd.level IS NOT NULL
		      AND a.project.projectManager.empId = :empId
		      AND a.allocationCompleted = false
		""")

		List<UserSkillDetailsWithNameDTO> findAllSkillsWithNamesForPM(String empId);
	
	@Query("""
		    SELECT DISTINCT new com.cozentus.pms.dto.UserSkillDetailsWithNameDTO(
		        s.skillName, u.empId, usd.level
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    JOIN u.allocations a
		    WHERE usd.level IS NOT NULL
		      AND a.project.projectManager.empId = :empId
		      AND a.allocationCompleted = false
		      AND u.empId IN :empIds
		""")

		List<UserSkillDetailsWithNameDTO> findAllSkillsWithNamesForPMForCertainEmpIds(String empId, List<String> empIds);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.SkillExperienceDTO(
		        usd.user.empId, usd.experienceInYears
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    WHERE s.skillName = :skillName
		      AND usd.level = :level
		""")
		List<SkillExperienceDTO> findExperienceBySkillAndLevel(String skillName, String level);
	
	@Query("SELECT DISTINCT s.skillName FROM Skill s")
	List<String> findAllSkills();
	
	
	List<Skill> findAllBySkillNameIn(Set<String> uppercasedNames);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM UserSkillDetail usd WHERE usd.user.empId = :empId AND usd.skill.skillName = :skillName")
	void deleteSkillFromUserDetailSkill(String empId, String skillName);
	
	
//	@Query("SELECT new com.cozentus.pms.dto.UserSkillDetailsWithNameDTO(s.skillName, u.empId, usd.level) " +
//			   "FROM UserSkillDetail usd JOIN usd.skill s JOIN usd.user u WHERE usd.level IS NOT NULL AND u.empId IN :empIds")
//		List<UserSkillDetailsWithNameDTO> findAllSkillsWithNamesWithCertainEmpIds(List<String> empIds);
	
	@Query("""
		    SELECT DISTINCT new com.cozentus.pms.dto.UserSkillDetailsWithNameDTO(
		        s.skillName, u.empId, usd.level
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    JOIN u.allocations a
		    WHERE usd.level IS NOT NULL
		      AND a.allocationCompleted = false
		      AND u.empId IN :empIds
		""")

		List<UserSkillDetailsWithNameDTO> findAllSkillsWithNamesWithCertainEmpIds(List<String> empIds);
	
	
	
	@Transactional
	@Modifying
	@Query("UPDATE Skill s SET s.skillName = :newSkillName WHERE s.skillName = :oldSkillName")
	int updateSkillNameByName(String oldSkillName, String newSkillName);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM Skill s WHERE s.skillName = :skillName")
	int deleteBySkillName(String skillName);
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.SkillCountByNameDTO(
		        s.skillName, COUNT(DISTINCT u.empId)
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    WHERE u.deliveryManager.empId = :dmEimpId 
		    AND usd.level IS NOT NULL 
		    AND u.enabled = true
		    GROUP BY s.skillName
		    ORDER BY COUNT(DISTINCT u.empId) DESC
		""")
		List<SkillCountByNameDTO> findSkillCountByName(String dmEimpId);
	
	
	
	@Query("""
		    SELECT new com.cozentus.pms.dto.SkillCountByNameDTO(
		        s.skillName, COUNT(DISTINCT u.empId)
		    )
		    FROM UserSkillDetail usd
		    JOIN usd.skill s
		    JOIN usd.user u
		    WHERE u.deliveryManager.empId = (SELECT u.deliveryManager.empId FROM UserInfo u WHERE u.empId = :pmEMpId)
		    AND usd.level IS NOT NULL 
		    AND u.enabled = true
		    GROUP BY s.skillName
		    ORDER BY COUNT(DISTINCT u.empId) DESC
		""")
		List<SkillCountByNameDTO> findSkillCountByNameForPM(String pmEMpId);

	



}
