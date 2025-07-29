
package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.dto.*;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.services.GptSkillNormalizerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class SkillServiceImplTest {

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private GptSkillNormalizerService gptSkillNormalizerService;

    @InjectMocks
    private SkillServiceImpl skillService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetSkillCounts() {
        when(skillRepository.findAllSkillsWithNames()).thenReturn(Arrays.asList(new UserSkillDetailsWithNameDTO("Java", "1", "Beginner")));
        List<SkillCountDTO> result = skillService.getSkillCounts(Roles.DELIVERY_MANAGER, "1", "Java");
        assertEquals(1, result.size());
    }

    @Test
    public void testCreateNewSkill() {
        doNothing().when(skillRepository).save(any());
        skillService.createNewSkill("Java");
        verify(skillRepository, times(1)).save(any());
    }

    @Test
    public void testGetSkillCountsBySearch() {
        List<SkillCountDTO> result = skillService.getSkillCountsBySearch(Roles.DELIVERY_MANAGER, "1", "Java");
        assertEquals(0, result.size());
    }

    @Test
    public void testUpdateSkill() {
        when(skillRepository.updateSkillNameByName(anyString(), anyString())).thenReturn(1);
        assertDoesNotThrow(() -> skillService.updateSkill("OldSkill", "NewSkill"));
    }

    @Test
    public void testUpdateSkillFailure() {
        when(skillRepository.updateSkillNameByName(anyString(), anyString())).thenReturn(0);
        assertThrows(RecordNotFoundException.class, () -> skillService.updateSkill("OldSkill", "NewSkill"));
    }

    @Test
    public void testDeleteSkill() {
        when(skillRepository.deleteBySkillName(anyString())).thenReturn(1);
        assertDoesNotThrow(() -> skillService.deleteSkill("Java"));
    }

    @Test
    public void testDeleteSkillFailure() {
        when(skillRepository.deleteBySkillName(anyString())).thenReturn(0);
        assertThrows(RecordNotFoundException.class, () -> skillService.deleteSkill("Java"));
    }
}
