//package com.cozentus.pms.controllers;
//
//
//
//import java.util.List;
//import java.util.Set;
//
//import org.apache.commons.lang3.tuple.Pair;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.cozentus.pms.dto.UserSkillDTO;
//import com.cozentus.pms.dto.ZohoEmployeeDTO;
//import com.cozentus.pms.serviceImpl.BulkResourceUpsertServiceImpl;
//import com.cozentus.pms.serviceImpl.EmbeddingServiceImpl;
//import com.cozentus.pms.serviceImpl.ZohoScheduleUpsert;
//import com.cozentus.pms.serviceImpl.ZohoServiceImpl;
//import com.cozentus.pms.services.GptSkillNormalizerService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping("/poc")
//@Slf4j
//public class POCController {
//	private final BulkResourceUpsertServiceImpl bulkResourceUpsertServiceImpl;
//    private final EmbeddingServiceImpl embeddingServiceImpl;
//	private final GptSkillNormalizerService gptSkillNormalizerService;
//	private final ZohoScheduleUpsert zohoScheduleUpsert;
//	public POCController(GptSkillNormalizerService gptSkillNormalizerService, EmbeddingServiceImpl embeddingServiceImpl, BulkResourceUpsertServiceImpl bulkResourceUpsertServiceImpl, ZohoScheduleUpsert zohoScheduleUpsert) {
//		this.gptSkillNormalizerService = gptSkillNormalizerService;
//		this.embeddingServiceImpl = embeddingServiceImpl;
//		this.bulkResourceUpsertServiceImpl = bulkResourceUpsertServiceImpl;
//		this.zohoScheduleUpsert = zohoScheduleUpsert;
//	}
//	
////	@GetMapping("/normalize-skills")
////	public ResponseEntity<List<UserSkillDTO>> normalizeSkills() {
////		return ResponseEntity.ok(gptSkillNormalizerService.normalizeSkillsBatch(List.of(
////			new UserSkillDTO("emp1", "Java-script / ReactJS, html", "css3, Node-Js"),
////			new UserSkillDTO("emp2", "Python/Django", "Flask, SQL")
////		)));
////	}
//	
//	@GetMapping("/normalize-bulk")
//	public void normalizeSkillsAsync() {
//		bulkResourceUpsertServiceImpl.syncResourcesWithDB();
//
//	}
////	
////	@GetMapping("/search")
////	public void searchSkills() {
////		 gptSkillNormalizerService.normalizeSkillSingle(new UserSkillDTO("GBH", "database", "frontend"), 100);
////	}
//	
//	@GetMapping("/test-zoho-cred")
//	public void testZohoCred() {
//		bulkResourceUpsertServiceImpl.insertResources();
//	}
//	
//	
//	@GetMapping("/zoho-sync")
//	public void syncZohoData() {
//		zohoScheduleUpsert.syncResourcesWithDB();
//	}
//
//}
