package com.cozentus.pms.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.LevelCount;
import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.dto.UserSkillDetailsWithNameDTO;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.SkillRepository;

@Service
public class SkillServiceImpl {
	private final SkillRepository skillRepository;
	
	public SkillServiceImpl(SkillRepository skillRepository) {
		this.skillRepository = skillRepository;
	}
	
	
	public List<SkillCountDTO> getSkillCounts(Roles role, String empId) {
		List<UserSkillDetailsWithNameDTO> userSkillDetailsWithNameDTOs;
		if (role.equals(Roles.DELIVERY_MANAGER)) {
			userSkillDetailsWithNameDTOs = skillRepository.findAllSkillsWithNames();
		}
		else {
			String managerEmpId = empId; 
			userSkillDetailsWithNameDTOs = skillRepository.findAllSkillsWithNamesForPM(managerEmpId); // Replace "empId" with actual employee ID
		}
	    
	    
	    Map<String, Set<Integer>> totalUsersBySkill = new HashMap<>();
	    Map<String, Map<String, Set<Integer>>> levelWiseUsersBySkill = new HashMap<>();

	    for (UserSkillDetailsWithNameDTO entry : userSkillDetailsWithNameDTOs) {
	        String skill = entry.skillName();
	        String level = entry.level() != null ? entry.level() : "";
	        Integer userId = entry.userId();

	        // Total unique users per skill
	        totalUsersBySkill
	            .computeIfAbsent(skill, k -> new HashSet<>())
	            .add(userId);

	        // Level-based unique users per skill
	        if (!level.isBlank()) {
	            levelWiseUsersBySkill
	                .computeIfAbsent(skill, k -> new HashMap<>())
	                .computeIfAbsent(level, l -> new HashSet<>())
	                .add(userId);
	        }
	    }

	    List<SkillCountDTO> skillCounts = new ArrayList<>();

	    for (Map.Entry<String, Set<Integer>> skillEntry : totalUsersBySkill.entrySet()) {
	        String skillName = skillEntry.getKey();
	        int totalCount = skillEntry.getValue().size();

	        Map<String, Set<Integer>> levelMap = levelWiseUsersBySkill.getOrDefault(skillName, Map.of());
	        List<LevelCount> levels = levelMap.entrySet().stream()
	            .map(e -> new LevelCount(e.getKey(), e.getValue().size()))
	            .sorted(Comparator.comparing(LevelCount::level)) // optional: sort by level name
	            .toList();

	        skillCounts.add(new SkillCountDTO(skillName, totalCount, levels));
	    }

	    return skillCounts;
	}


	public void createNewSkill(String skillName) {
		Skill skill = new Skill();
		skill.setSkillName(skillName);
		skillRepository.save(skill);
		
	}
	
}
