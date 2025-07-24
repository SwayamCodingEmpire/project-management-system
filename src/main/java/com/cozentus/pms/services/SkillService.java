package com.cozentus.pms.services;

import java.util.List;

import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.helpers.Roles;

public interface SkillService {
	
	void updateSkill(String oldSkillName, String newSkillName);
	List<SkillCountDTO> getSkillCounts(Roles role, String empId, String search) ;
	void createNewSkill(String skillName) ;
	List<SkillCountDTO> getSkillCountsBySearch(Roles role, String empId, String search);
	void deleteSkill(String skillName);

}
