
package com.cozentus.pms.repositories;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.SkillExperienceDTO;
import com.cozentus.pms.dto.UserSkillDetailsWithNameDTO;
import com.cozentus.pms.entites.Skill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class SkillRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    public void testFindBySkillName() {
        Skill skill = new Skill();
        skill.setSkillName("Java");
        entityManager.persist(skill);
        entityManager.flush();

        List<Skill> foundSkills = skillRepository.findBySkillName(skill.getSkillName());

        assertThat(foundSkills).isNotEmpty();
        assertThat(foundSkills.get(0).getSkillName()).isEqualTo(skill.getSkillName());
    }

    @Test
    public void testFindBySkillNameIn() {
        Skill skill1 = new Skill();
        skill1.setSkillName("Java");
        entityManager.persist(skill1);

        Skill skill2 = new Skill();
        skill2.setSkillName("Python");
        entityManager.persist(skill2);
        entityManager.flush();

        List<Skill> foundSkills = skillRepository.findBySkillNameIn(Set.of(skill1.getSkillName(), skill2.getSkillName()));

        assertThat(foundSkills).hasSize(2);
        assertThat(foundSkills).extracting("skillName").containsExactlyInAnyOrder(skill1.getSkillName(), skill2.getSkillName());
    }

    // Similarly, you can write tests for other methods
}
