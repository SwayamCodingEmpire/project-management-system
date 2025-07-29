//
//package com.cozentus.pms.repositories;
//
//import com.cozentus.pms.dto.ProjectTypeDropdownDTO;
//import com.cozentus.pms.dto.ProjectTypeOptionsDTO;
//import com.cozentus.pms.entites.ProjectType;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//public class ProjectTypeRepositoryTest {
//
//    @Autowired
//    private TestEntityManager entityManager;
//
//    @Autowired
//    private ProjectTypeRepository projectTypeRepository;
//
//    @Test
//    public void testFindAllProjectTypes() {
//        ProjectType projectType = new ProjectType();
//        projectType.setId(1);
//        projectType.setProjectType("Test Project Type");
////        projectType.setProjectCategory(true);
//        entityManager.persist(projectType);
//        entityManager.flush();
//
//        List<ProjectTypeDropdownDTO> projectTypeDropdownDTOS = projectTypeRepository.findAllProjectTypes();
//
//        assertThat(projectTypeDropdownDTOS).isNotEmpty();
//        assertThat(projectTypeDropdownDTOS.get(0).id()).isEqualTo(projectType.getId());
//        assertThat(projectTypeDropdownDTOS.get(0).projectType()).isEqualTo(projectType.getProjectType());
//        assertThat(projectTypeDropdownDTOS.get(0).isCustomerProject()).isEqualTo(projectType.isProjectCategory());
//    }
//
//    @Test
//    public void testFindAllProjectTypeOptions() {
//        ProjectType projectType = new ProjectType();
//        projectType.setId(1);
//        projectType.setProjectType("Test Project Type");
//        projectType.setProjectCategory(true);
//        entityManager.persist(projectType);
//        entityManager.flush();
//
////        List<ProjectTypeOptionsDTO> projectTypeOptionsDTOS = projectTypeRepository.findAllProjectTypeOptions();
//
////        assertThat(projectTypeOptionsDTOS).isNotEmpty();
////        assertThat(projectTypeOptionsDTOS.get(0).id()).isEqualTo(projectType.getId());
////        assertThat(projectTypeOptionsDTOS.get(0).value()).isEqualTo(projectType.getProjectType());
////        assertThat(projectTypeOptionsDTOS.get(0).label()).isEqualTo(projectType.isProjectCategory());
//    }
//
//    @Test
//    public void testFindById() {
//        ProjectType projectType = new ProjectType();
//        projectType.setId(1);
//        projectType.setProjectType("Test Project Type");
//        entityManager.persist(projectType);
//        entityManager.flush();
//
//        Optional<ProjectType> found = projectTypeRepository.findById(projectType.getId());
//
//        assertThat(found).isPresent();
//        assertThat(found.get().getId()).isEqualTo(projectType.getId());
//    }
//}
