package com.cozentus.pms.serviceImpl;import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.helpers.DeterministicIdGenerator;
import com.cozentus.pms.services.EmbeddingService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService {
	private final VectorStore vectorStore; // Assuming you have a QdrantVectorStore implementation


	
	
	public EmbeddingServiceImpl(VectorStore vectorStore) {
		this.vectorStore = vectorStore;

	}
	


	@Override
	public void createSkillEmbeddings(List<UserSkillDTO> skillsData) {
	    List<Document> documents = skillsData.stream()
	        .map(dto -> {
	            List<String> skills = dto.skills();

	            if (skills == null || skills.isEmpty()) {
	                return null; // Skip users with no skills
	            }

	            String content = "Skills: " + String.join(" ", skills);
	            log.info(content);
	            String docId = new DeterministicIdGenerator().generateId(dto.empId()); // Deterministic ID per empId

	            return new Document(docId, content, Map.of("empId", dto.empId()));
	        })
	        .filter(Objects::nonNull)
	        .toList();

	    vectorStore.add(documents); // Embed and store in Qdrant
	}

	
	@Override
	@Cacheable(value = "skillEmbeddingsCache", key = "#skill + '-' + #topK")
	public List<String> searchSkillEmbeddings(String skill, int topK) {
	    String query;

	    if (skill == null || skill.isBlank()) {
	        query = "all skills"; // fallback generic query
	    } else {
	        query = "Skills: " + skill;
	    }

	    SearchRequest request = SearchRequest.builder()
	        .query(query)
	        .topK(topK)
	        .build();

	    return vectorStore.similaritySearch(request).stream()
	        .map(doc -> (String) doc.getMetadata().get("empId")) // Extract empId from metadata
	        .toList();
	}


	
//	@Override
//	public void updateSkillEmbedding(UserSkillDTO dto) {
//	    // Delete existing vector
//		String docId = new DeterministicIdGenerator().generateId(dto.empId()); 
//	    vectorStore.delete(List.of(docId));
//
//	    // Recreate only if skill is present
//	    List<String> contentParts = new ArrayList<>();
//
//	    if (dto.primarySkill() != null && !dto.primarySkill().isBlank()) {
//	        contentParts.add("Primary Skill: " + dto.primarySkill());
//	    }
//
//	    if (dto.secondarySkill() != null && !dto.secondarySkill().isBlank()) {
//	        contentParts.add("Secondary Skill: " + dto.secondarySkill());
//	    }
//
//	    if (contentParts.isEmpty()) {
//	        log.warn("Skipping update for empId={} due to missing skills", dto.empId());
//	        return;
//	    }
//	    String content = String.join(". ", contentParts);
//	    Document doc = new Document(docId, content, Map.of("empId", dto.empId()));
//
//	    vectorStore.add(List.of(doc));
//	}
	
	

	public void addSkillEmbedding(UserSkillDTO dto) {
	    List<String> skills = dto.skills();
	    if (skills == null || skills.isEmpty()) {
	        log.warn("Skipping addition for empId={} due to missing skills", dto.empId());
	        return;
	    }

	    String docId = new DeterministicIdGenerator().generateId(dto.empId());
	    String content = "Skills: " + String.join(", ", skills);

	    Document doc = new Document(docId, content, Map.of("empId", dto.empId()));
	    vectorStore.add(List.of(doc));
	}
	
	public void deleteSkillEmbedding(String empId) {
	    String docId = new DeterministicIdGenerator().generateId(empId);
	    vectorStore.delete(List.of(docId));
	}
}
