package com.cozentus.pms.services;

import java.util.List;
import java.util.Set;

import com.cozentus.pms.dto.UserSkillDTO;

public interface GptSkillNormalizerService {
	
//	public List<UserSkillDTO> normalizeSkillsBatch(List<UserSkillDTO> userSkillDTO);
	void populateQuadrantVectorDB();
	List<String> normalizeSkillSingle(UserSkillDTO record, int topK);
	void normalizeSkillSingleOnly(List<UserSkillDTO> records);
	void populateQuadrantVectorDBForSingleUser(String empId);

}
