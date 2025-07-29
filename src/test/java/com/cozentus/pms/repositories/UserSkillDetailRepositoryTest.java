
package com.cozentus.pms.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.helpers.SkillPriority;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserSkillDetailRepositoryTest {

    @MockBean
    private UserSkillDetailRepository userSkillDetailRepository;

    @Test
    public void testUpdateLevelAndExperienceByEmpIdAndSkillName() {
        when(userSkillDetailRepository.updateLevelAndExperienceByEmpIdAndSkillName(any(String.class), any(BigDecimal.class), any(String.class), any(String.class), any(SkillPriority.class))).thenReturn(1);
        int result = userSkillDetailRepository.updateLevelAndExperienceByEmpIdAndSkillName("level", BigDecimal.ONE, "empId", "skillName", SkillPriority.PRIMARY);
        assertEquals(1, result);
    }

    @Test
    public void testExistsByEmpIdAndSkillName() {
        when(userSkillDetailRepository.existsByEmpIdAndSkillName(any(String.class), any(String.class))).thenReturn(true);
        boolean result = userSkillDetailRepository.existsByEmpIdAndSkillName("empId", "skillName");
        assertEquals(true, result);
    }

    @Test
    public void testFindUserIdIdByEmpId() {
        when(userSkillDetailRepository.findUserIdIdByEmpId(any(String.class))).thenReturn(Optional.of(1));
        Optional<Integer> result = userSkillDetailRepository.findUserIdIdByEmpId("empId");
        assertEquals(Optional.of(1), result);
    }

    @Test
    public void testFindSkillIdBySkillName() {
        when(userSkillDetailRepository.findSkillIdBySkillName(any(String.class))).thenReturn(Optional.of(1));
        Optional<Integer> result = userSkillDetailRepository.findSkillIdBySkillName("skillName");
        assertEquals(Optional.of(1), result);
    }
}
