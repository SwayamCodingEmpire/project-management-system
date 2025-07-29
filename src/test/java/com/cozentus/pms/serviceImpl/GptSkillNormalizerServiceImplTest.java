
package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.EmbeddingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GptSkillNormalizerServiceImplTest {

    @MockBean
    private EmbeddingService embeddingService;

    @MockBean
    private ChatClient chatClient;

    @MockBean
    private UserInfoRepository userInfoRepository;

    private GptSkillNormalizerServiceImpl gptSkillNormalizerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        gptSkillNormalizerService = new GptSkillNormalizerServiceImpl(chatClient, embeddingService, userInfoRepository);
    }

    @Test
    public void testNormalizeSkillBulk() {
        List<UserSkillDTO> records = Arrays.asList(new UserSkillDTO("1", Arrays.asList("Java", "Python")));
        gptSkillNormalizerService.normalizeSkillBulk(records);
        verify(embeddingService, times(1)).createSkillEmbeddings(anyList());
    }

    @Test
    public void testNormalizeSkillBulkWithEmptyRecords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gptSkillNormalizerService.normalizeSkillBulk(Arrays.asList());
        });
        assertEquals("Records list cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testNormalizeSkillSingleOnly() {
        List<UserSkillDTO> records = Arrays.asList(new UserSkillDTO("1", Arrays.asList("Java", "Python")));
        gptSkillNormalizerService.normalizeSkillSingleOnly(records);
        verify(embeddingService, times(1)).deleteSkillEmbedding(anyString());
        verify(embeddingService, times(1)).addSkillEmbedding(any());
    }

    @Test
    public void testNormalizeSkillSingleOnlyWithEmptyRecords() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gptSkillNormalizerService.normalizeSkillSingleOnly(Arrays.asList());
        });
        assertEquals("Records list cannot be null or empty", exception.getMessage());
    }

    @Test
    public void testParseSkillsResponseSafe() {
        List<UserSkillDTO> result = gptSkillNormalizerService.parseSkillsResponseSafe("1\nJava Python");
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).empId());
        assertEquals(Arrays.asList("Java", "Python"), result.get(0).skills());
    }

    @Test
    public void testParseSkillsResponseSafeWithEmptyContent() {
        List<UserSkillDTO> result = gptSkillNormalizerService.parseSkillsResponseSafe("");
        assertEquals(0, result.size());
    }

    // Add more tests for other methods in the service class
}
