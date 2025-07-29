//
//package com.cozentus.pms.serviceImpl;
//
//import com.cozentus.pms.dto.UserSkillDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.SearchRequest;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class EmbeddingServiceImplTest {
//
//    @MockBean
//    private VectorStore vectorStore;
//
//    private EmbeddingServiceImpl embeddingService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        embeddingService = new EmbeddingServiceImpl(vectorStore);
//    }
//
//    @Test
//    public void testCreateSkillEmbeddings() {
//        List<UserSkillDTO> skillsData = Arrays.asList(
//                new UserSkillDTO("emp1", Arrays.asList("Java", "Spring")),
//                new UserSkillDTO("emp2", Arrays.asList("Python", "Django"))
//        );
//
//        embeddingService.createSkillEmbeddings(skillsData);
//
//        verify(vectorStore, times(1)).add(any());
//    }
//
//    @Test
//    public void testSearchSkillEmbeddings() {
//        String skill = "Java";
//        int topK = 5;
//
//        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(Arrays.asList(new Document()));
//
//        embeddingService.searchSkillEmbeddings(skill, topK);
//
//        verify(vectorStore, times(1)).similaritySearch(any(SearchRequest.class));
//    }
//
//    @Test
//    public void testAddSkillEmbedding() {
//        UserSkillDTO dto = new UserSkillDTO("emp1", Arrays.asList("Java", "Spring"));
//
//        embeddingService.addSkillEmbedding(dto);
//
//        verify(vectorStore, times(1)).add(any());
//    }
//
//    @Test
//    public void testDeleteSkillEmbedding() {
//        String empId = "emp1";
//
//        embeddingService.deleteSkillEmbedding(empId);
//
//        verify(vectorStore, times(1)).delete(any());
//    }
//}
