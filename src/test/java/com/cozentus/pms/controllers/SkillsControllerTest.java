
package com.cozentus.pms.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.cozentus.pms.services.SkillService;

@SpringBootTest
public class SkillsControllerTest {

    @InjectMocks
    private SkillsController skillsController;

    @MockBean
    private SkillService skillService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateNewSkill() {
        String skillName = "Java";
        ResponseEntity<?> response = skillsController.createNewSkill(skillName);
        verify(skillService, times(1)).createNewSkill(skillName.toUpperCase());
        assertEquals("Skill updated successfully.", response.getBody());
    }

    @Test
    public void testCreateNewSkillWithEmptyName() {
        String skillName = "";
        ResponseEntity<?> response = skillsController.createNewSkill(skillName);
        assertEquals("Skill name must not be blank.", response.getBody());
    }

    @Test
    public void testUpdateSkill() {
        String oldSkillName = "Java";
        String newSkillName = "Python";
        ResponseEntity<?> response = skillsController.updateSkill(oldSkillName, newSkillName);
        verify(skillService, times(1)).updateSkill(oldSkillName, newSkillName.toUpperCase());
        assertEquals("Skill updated successfully.", response.getBody());
    }

    @Test
    public void testUpdateSkillWithSameOldAndNewName() {
        String oldSkillName = "Java";
        String newSkillName = "Java";
        ResponseEntity<?> response = skillsController.updateSkill(oldSkillName, newSkillName);
        assertEquals("New skill name must be different from the old skill name.", response.getBody());
    }

    @Test
    public void testDeleteSkill() {
        String skillName = "Java";
        ResponseEntity<?> response = skillsController.deleteSkill(skillName);
        verify(skillService, times(1)).deleteSkill(skillName);
        assertEquals("Skill deleted successfully.", response.getBody());
    }

    @Test
    public void testDeleteSkillWithEmptyName() {
        String skillName = "";
        ResponseEntity<?> response = skillsController.deleteSkill(skillName);
        assertEquals("Skill name must not be blank.", response.getBody());
    }
}
