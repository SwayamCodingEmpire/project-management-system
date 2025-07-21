package com.cozentus.pms.services;

import java.util.List;

import com.cozentus.pms.dto.UserSkillDTO;

public interface EmbeddingService {
	void createSkillEmbeddings(List<UserSkillDTO> skillsData);
	List<String> searchSkillEmbeddings(String skills, int topK) ;
//	void updateSkillEmbedding(UserSkillDTO dto);
	void deleteSkillEmbedding(String empId);
	void addSkillEmbedding(UserSkillDTO dto);

}
