package com.cozentus.pms.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.cozentus.pms.dto.UserSingleSkillDTO;
import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.EmbeddingService;
import com.cozentus.pms.services.GptSkillNormalizerService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class GptSkillNormalizerServiceImpl implements GptSkillNormalizerService {
	private final EmbeddingService embeddingService;
	private final ChatClient chatClient;
	private final UserInfoRepository userInfoRepository;

	public GptSkillNormalizerServiceImpl(ChatClient chatClient, EmbeddingService embeddingService,
			UserInfoRepository userInfoRepository) {
		this.chatClient = chatClient;
		this.embeddingService = embeddingService;
		this.userInfoRepository = userInfoRepository;
	}


	public void normalizeSkillBulk(List<UserSkillDTO> records) {
		if (records == null || records.isEmpty()) {
			throw new IllegalArgumentException("Records list cannot be null or empty");
		}

		StringBuilder systemPrompt = new StringBuilder("""
				    You are a data normalizer that cleans up messy skill text.

				    For each employee record below:
				    - Fix typos and inconsistent casing
				    - Expand abbreviations (e.g., JS → JavaScript, DB → Database)
				    - Map aliases to standard tech terms
				    - Remove symbols like /, |, -, commas, etc.
				    - NA means Not Available, so keep NA as is
				    - If skills are missing or empty, output 'na'
				    - Output each normalized result as exactly 2 lines per employee:
				      1. Line 1: Employee ID (unchanged)
				      2. Line 2: Normalized skills (space-separated, lowercase) or 'na'
				    - DO NOT include code blocks, quotes, labels, or markdown
				    - DO NOT add extra blank lines or reorder entries
				""");

		StringBuilder userPrompt = new StringBuilder();
		for (UserSkillDTO record : records) {
			String skillsRaw = (record.skills() != null && !record.skills().isEmpty())
					? String.join(", ", record.skills())
					: "na";

			userPrompt.append(String.format("""
					EmployeeId: %s
					Skills: %s
					""", record.empId(), skillsRaw));
		}

		ChatResponse response = chatClient.prompt().system(systemPrompt.toString()).user(userPrompt.toString()).call()
				.chatResponse();

		String content = response.getResult().getOutput().getText();
		log.info("GPT normalized bulk:\n{}", content);

		List<UserSkillDTO> parsed = parseSkillsResponseSafe(content);

		if (parsed.size() != records.size()) {
			log.warn("Expected {} entries, but got {} from GPT", records.size(), parsed.size());
			throw new RuntimeException("Normalization failed: Mismatched result count.");
		}

		embeddingService.createSkillEmbeddings(parsed);
	}

	public void normalizeSkillSingleOnly(List<UserSkillDTO> records) {
		if (records == null || records.isEmpty()) {
			throw new IllegalArgumentException("Records list cannot be null or empty");
		}

		StringBuilder systemPrompt = new StringBuilder("""
				    You are a data normalizer that cleans up messy skill text.

				    For each employee record below:
				    - Fix typos and inconsistent casing
				    - Expand abbreviations (e.g., JS → JavaScript, DB → Database)
				    - Map aliases to standard tech terms
				    - Remove symbols like /, |, -, commas, etc.
				    - NA means Not Available, so keep NA as is
				    - If skills are missing or empty, output 'na'
				    - Output each normalized result as exactly 2 lines per employee:
				      1. Line 1: Employee ID (unchanged)
				      2. Line 2: Normalized skills (space-separated, lowercase) or 'na'
				    - DO NOT include code blocks, quotes, labels, or markdown
				    - DO NOT add extra blank lines or reorder entries
				""");

		StringBuilder userPrompt = new StringBuilder();
		for (UserSkillDTO record : records) {
			String skillsRaw = (record.skills() != null && !record.skills().isEmpty())
					? String.join(", ", record.skills())
					: "na";

			userPrompt.append(String.format("""
					EmployeeId: %s
					Skills: %s
					""", record.empId(), skillsRaw));
		}

		ChatResponse response = chatClient.prompt().system(systemPrompt.toString()).user(userPrompt.toString()).call()
				.chatResponse();

		String content = response.getResult().getOutput().getText();
		log.info("GPT normalized bulk:\n{}", content);

		List<UserSkillDTO> parsed = parseSkillsResponseSafe(content);

		if (parsed.size() != records.size()) {
			log.warn("Expected {} entries, but got {} from GPT", records.size(), parsed.size());
			throw new RuntimeException("Normalization failed: Mismatched result count.");
		}

		embeddingService.deleteSkillEmbedding(parsed.get(0).empId());
		embeddingService.addSkillEmbedding(parsed.get(0));
	}

//	
	public List<UserSkillDTO> parseSkillsResponseSafe(String content) {
		if (content == null || content.trim().isEmpty()) {
			return List.of();
		}

		List<String> lines = Arrays.stream(content.split("\\R")).map(String::trim).filter(line -> !line.isEmpty())
				.toList();

		if (lines.size() % 2 != 0) {
			throw new IllegalStateException(
					"Invalid GPT response format: expected multiple of 2 lines (empId + skill line)");
		}

		log.info("Parsing GPT response with {} lines", lines.size());
		log.info(lines.toString());

		List<UserSkillDTO> results = new ArrayList<>();

		for (int i = 0; i < lines.size(); i += 2) {
			String empId = lines.get(i);
			String skillLine = lines.get(i + 1);

			List<String> skills;
			if ("na".equalsIgnoreCase(skillLine)) {
				skills = new ArrayList<>();
			} else {
				skills = Arrays.stream(skillLine.split("\\s+")).map(String::trim).filter(s -> !s.isBlank()).toList();
			}

			results.add(new UserSkillDTO(empId, skills));
		}

		return results;
	}

//
//
//
//
//	
	@Override

	public void populateQuadrantVectorDB() {
		List<UserSingleSkillDTO> flatList = userInfoRepository.fetchFlatUserSkills();

		List<UserSkillDTO> userSkillDTOs = flatList.stream()
				.collect(Collectors.groupingBy(UserSingleSkillDTO::empId,
						Collectors.mapping(UserSingleSkillDTO::skillName, Collectors.toList())))
				.entrySet().stream().map(entry -> new UserSkillDTO(entry.getKey(), entry.getValue())).toList();

		log.info(userSkillDTOs.toString());

		normalizeSkillBulk(userSkillDTOs);
//		embeddingService.createSkillEmbeddings(userSkillDTOs);

	}

	
	public void populateQuadrantVectorDBForSingleUser(String empId) {
		List<UserSingleSkillDTO> flatList = userInfoRepository.fetchFlatUserSkillsByEmpID(empId);

		List<UserSkillDTO> userSkillDTOs = flatList.stream()
				.collect(Collectors.groupingBy(UserSingleSkillDTO::empId,
						Collectors.mapping(UserSingleSkillDTO::skillName, Collectors.toList())))
				.entrySet().stream().map(entry -> new UserSkillDTO(entry.getKey(), entry.getValue())).toList();

		log.info(userSkillDTOs.toString());

		normalizeSkillSingleOnly(userSkillDTOs);
//		embeddingService.createSkillEmbeddings(userSkillDTOs);

	}
	
	public void populateQuadrantVectorDBForMultiUser(List<String> empId) {
		List<UserSingleSkillDTO> flatList = userInfoRepository.fetchFlatUserSkillsByEmpIDIn(empId);

		List<UserSkillDTO> userSkillDTOs = flatList.stream()
				.collect(Collectors.groupingBy(UserSingleSkillDTO::empId,
						Collectors.mapping(UserSingleSkillDTO::skillName, Collectors.toList())))
				.entrySet().stream().map(entry -> new UserSkillDTO(entry.getKey(), entry.getValue())).toList();

		log.info(userSkillDTOs.toString());

		normalizeSkillBulk(userSkillDTOs);
//		embeddingService.createSkillEmbeddings(userSkillDTOs);

	}

//	
//	private void normalizeSkillsInBatches(List<UserSkillDTO> allRecords) {
//	    final int BATCH_SIZE = 20; // Adjust based on your token usage
//	    final long DELAY_BETWEEN_BATCHES_MS = 2000; // 2 seconds delay
//	    
//	    List<List<UserSkillDTO>> batches = Lists.partition(allRecords, BATCH_SIZE);
//	    List<UserSkillDTO> allNormalizedSkills = new ArrayList<>();
//	    
//	    for (int i = 0; i < batches.size(); i++) {
//	        List<UserSkillDTO> batch = batches.get(i);
//	        
//	        try {
//	            log.info("Processing normalization batch {} of {}", i + 1, batches.size());
//	            
//	            List<UserSkillDTO> normalizedBatch = normalizeSkillsBatch(batch);
//	            allNormalizedSkills.addAll(normalizedBatch);
//	            
//	            // Create embeddings for this batch
//	            embeddingService.createSkillEmbeddings(normalizedBatch);
//	            
//	            // Add delay between batches to avoid rate limits
//	            if (i < batches.size() - 1) {
//	                Thread.sleep(DELAY_BETWEEN_BATCHES_MS);
//	            }
//	            
//	        } catch (Exception e) {
//	            log.error("Failed to process normalization batch {}", i + 1, e);
//	            // Continue with next batch or throw exception based on your requirements
//	        }
//	    }
//	    
//	    log.info("Successfully processed all {} batches with {} total records", 
//	             batches.size(), allNormalizedSkills.size());
//	}
//	
//
	public List<String> normalizeSkillSingle(UserSkillDTO record, int topK) {
		StringBuilder systemPrompt = new StringBuilder("""
				You are a data normalizer that cleans up messy skill text.

				For each employee record below:
				- Fix typos and inconsistent casing
				- Expand abbreviations (e.g., JS → JavaScript, DB → Database)
				- Map aliases to standard tech terms
				- Remove symbols like /, |, -, commas, etc.
				- NA means Not Available, so keep NA as is
				- If skills are missing or empty, output 'na'
				- Output each normalized result as exactly 2 lines per employee:
				  1. Line 1: Employee ID (unchanged)
				  2. Line 2: Normalized skills (space-separated, lowercase) or 'na'
				- DO NOT include code blocks, quotes, labels, or markdown
				- DO NOT add extra blank lines or reorder entries
				""");

		String skillsRaw = (record.skills() != null && !record.skills().isEmpty()) ? String.join(", ", record.skills())
				: "na";

		String userPrompt = String.format("""
				EmployeeId: %s
				Skills: %s
				""", record.empId(), skillsRaw);

		ChatResponse response = chatClient.prompt().system(systemPrompt.toString()).user(userPrompt).call()
				.chatResponse();

		String content = response.getResult().getOutput().getText();
		log.info("GPT normalized single: {}", content);

		List<UserSkillDTO> parsed = parseSkillsResponseSafe(content);

		if (parsed.isEmpty()) {
			throw new RuntimeException("Normalization failed: Empty result from LLM.");
		}

		// Use parsed skills for semantic search
		List<String> searches = parsed.get(0).skills();
		log.info("Search results: {}", searches);
		String searchString = String.join(", ", searches);
		return embeddingService.searchSkillEmbeddings(searchString, topK);
	}

}
