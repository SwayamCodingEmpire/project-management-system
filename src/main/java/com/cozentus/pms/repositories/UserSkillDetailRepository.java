package com.cozentus.pms.repositories;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.helpers.SkillPriority;

public interface UserSkillDetailRepository extends JpaRepository<UserSkillDetail, Integer> {
	@Transactional
	@Modifying
	@Query("""
	    UPDATE UserSkillDetail usd
	    SET usd.level = :level,
	        usd.experienceInYears = :experience
	    WHERE usd.user.empId = :empId
	    AND usd.priority = :skillPriority
	      AND usd.skill.skillName = :skillName
	""")
	int updateLevelAndExperienceByEmpIdAndSkillName(
	    @Param("level") String level,
	    @Param("experience") BigDecimal experience,
	    @Param("empId") String empId,
	    @Param("skillName") String skillName,
	    @Param("skillPriority") SkillPriority skillPriority
	);
	
	@Query("""
		    SELECT COUNT(usd.id) > 0
		    FROM UserSkillDetail usd
		    WHERE usd.user.empId = :empId
		      AND usd.skill.skillName = :skillName
		""")
		boolean existsByEmpIdAndSkillName(@Param("empId") String empId, @Param("skillName") String skillName);
	
	@Query("""
	    SELECT u.id FROM UserInfo u WHERE u.empId = :empId
	""")
	Optional<Integer> findUserIdIdByEmpId(String empId);
	
	@Query("""
	    SELECT s.id FROM Skill s WHERE s.skillName = :skillName
	""")
	Optional<Integer> findSkillIdBySkillName(String skillName);


}


