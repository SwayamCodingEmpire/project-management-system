package com.cozentus.pms.serviceImpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.LevelCount;
import com.cozentus.pms.dto.SkillCountByNameDTO;
import com.cozentus.pms.dto.SkillCountDTO;
import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.dto.UserSkillDetailsWithNameDTO;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.services.GptSkillNormalizerService;
import com.cozentus.pms.services.SkillService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SkillServiceImpl implements SkillService {
	private final SkillRepository skillRepository;
	private final GptSkillNormalizerService gptSkillNormalizerService;
	
	
	public SkillServiceImpl(SkillRepository skillRepository, GptSkillNormalizerService gptSkillNormalizerService) {
		this.skillRepository = skillRepository;
		this.gptSkillNormalizerService = gptSkillNormalizerService;
	}
	
	
	public List<SkillCountDTO> getSkillCounts(Roles role, String search, String dmEmpId) {
	    List<UserSkillDetailsWithNameDTO> userSkillDetailsWithNameDTOs;
	    Map<String, Integer> empIdOrderMap;
	    Map<String, String> skillToFirstEmpId = new HashMap<>();
	    List<SkillCountByNameDTO> skillCountByNameDTOs = new ArrayList<>();
	        boolean isSearchEmpty = search == null || search.isBlank();
	        if (isSearchEmpty) {
	            userSkillDetailsWithNameDTOs = skillRepository.findAllSkillsWithNames();
	            empIdOrderMap = null;
	            if(role.equals(Roles.DELIVERY_MANAGER)) {
	                log.info("Fetching skill counts for DM: {}", dmEmpId);
	                skillCountByNameDTOs = skillRepository.findSkillCountByName(dmEmpId);
	            } else {
	                log.info("Fetching skill counts for PM: {}", dmEmpId);
		            skillCountByNameDTOs = skillRepository.findSkillCountByNameForPM(dmEmpId);
	            }
	                // Assuming PMs can also fetch skill counts by DM empId

	        } else {
	            log.info("Searching for skills with search term: {}", search);
	            List<String> empIds = gptSkillNormalizerService
	                .normalizeSkillSingle(new UserSkillDTO("EMP124", List.of(search)), 20);
	            log.info("Normalized employee IDs: {}", empIds);

	            empIdOrderMap = IntStream.range(0, empIds.size())
	                .boxed()
	                .collect(Collectors.toMap(empIds::get, i -> i));

	            userSkillDetailsWithNameDTOs = skillRepository.findAllSkillsWithNamesWithCertainEmpIds(empIds);
	        }

	    Map<String, Set<String>> totalUsersBySkill = new HashMap<>();
	    Map<String, Map<String, Set<String>>> levelWiseUsersBySkill = new HashMap<>();

	    for (UserSkillDetailsWithNameDTO entry : userSkillDetailsWithNameDTOs) {
	        String skill = entry.skillName();
	        String level = entry.level() != null ? entry.level() : "";
	        String userId = entry.userId(); // which is empId in your DTO

	        skillToFirstEmpId.putIfAbsent(skill, userId);

	        totalUsersBySkill.computeIfAbsent(skill, k -> new HashSet<>()).add(userId);

	        if (!level.isBlank()) {
	            levelWiseUsersBySkill
	                .computeIfAbsent(skill, k -> new HashMap<>())
	                .computeIfAbsent(level, l -> new HashSet<>())
	                .add(userId);
	        }
	    }

	    List<SkillCountDTO> skillCounts = totalUsersBySkill.entrySet().stream()
	        .map(skillEntry -> {
	            String skillName = skillEntry.getKey();
	            int totalCount = skillEntry.getValue().size();

	            Map<String, Set<String>> levelMap = levelWiseUsersBySkill.getOrDefault(skillName, Map.of());
	            List<LevelCount> levels = levelMap.entrySet().stream()
	                .map(e -> new LevelCount(e.getKey(), e.getValue().size()))
	                .sorted(Comparator.comparing(LevelCount::level))
	                .toList();

	            return new SkillCountDTO(skillName, totalCount, levels);
	        })
	        .collect(Collectors.toList());

	    // Sort by order of first contributing empId, if applicable
	    if (empIdOrderMap != null) {
	        skillCounts.sort(Comparator.comparingInt(dto -> {
	            String firstEmpId = skillToFirstEmpId.get(dto.name());
	            return empIdOrderMap.getOrDefault(firstEmpId, Integer.MAX_VALUE);
	        }));
	    }
	    
	    else {
	        Map<String, Integer> countMap = Optional.ofNullable(skillCountByNameDTOs)
	            .orElse(List.of())
	            .stream()
	            .filter(dto -> dto != null && dto.name() != null && !dto.name().isBlank())
	            .collect(Collectors.toMap(
	                SkillCountByNameDTO::name,
	                dto -> (int) dto.totalCount(),
	                (a, b) -> Math.max(a, b)
	            ));

	        skillCounts.sort(Comparator
	            .comparingInt((SkillCountDTO dto) ->
	                countMap.getOrDefault(dto.name(), Integer.MIN_VALUE)
	            ).reversed());
	    }


	    return skillCounts;
	}




	public void createNewSkill(String skillName) {
		Skill skill = new Skill();
		skill.setSkillName(skillName);
		skillRepository.save(skill);
		
	}


	public List<SkillCountDTO> getSkillCountsBySearch(Roles role, String empId, String search) {

		return List.of(); // Placeholder for actual implementation	
	}
	
	public void updateSkill(String oldSkillName, String newSkillName) {
		if(skillRepository.updateSkillNameByName(oldSkillName, newSkillName)>0) {
			log.info("Skill name updated from {} to {}", oldSkillName, newSkillName);
		}
		else {
			log.error("Failed to update skill name from {} to {}", oldSkillName, newSkillName);
			throw new RecordNotFoundException("Failed to update skill name");
		}
	}


	@Override
	public void deleteSkill(String skillName) {
		if(skillRepository.deleteBySkillName(skillName)>0) {
			log.info("Skill {} deleted successfully", skillName);
		}
		else {
			log.error("Failed to delete skill {}", skillName);
			throw new RecordNotFoundException("Failed to delete skill name");
		}
		
	}
	
}
