package com.cozentus.pms;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.annotation.Rollback;

import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.services.UserInfoService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback
class PmsApplicationTests {
	@Autowired
	private UserInfoService userInfoService;

	@Test
	void testGetAllManagersData() {
		List<ProjectManagerDTO> projectManagers = userInfoService.getAllProjectManagersWithProjects();
		ProjectManagerDTO projectManagerDTO = new ProjectManagerDTO();
		projectManagerDTO.setValues("EMP1003", "Heather Anderson", "user3@example.com",
				List.of("GoldenRod Project", "Fuchsia Project", "PapayaWhip Project"));
		ProjectManagerDTO projectManagerDTO1 = new ProjectManagerDTO();
		projectManagerDTO1.setValues("EMP1007", "Elizabeth Green", "user7@example.com",
				List.of("DarkGoldenRod Project", "DarkRed Project", "SlateGray Project", "Linen Project"));
		List<ProjectManagerDTO> projectManagersList = List.of(projectManagerDTO, projectManagerDTO1);
		assertEquals(projectManagersList.toString(), projectManagers.toString());
	}
//
//	@Mock
//	private UserInfoRepository userInfoRepository;
//
//	@InjectMocks
//	private UserInfoServiceImpl projectManagerService;
//
//	@Test
//	void testGetAllProjectManagersWithProjects() {
//		// Given
//		UserInfo user = new UserInfo();
//		user.setEmpId("EMP101");
//		user.setName("John Doe");
//		user.setEmailId("john.doe@example.com");
//
//		ProjectDetails project1 = new ProjectDetails();
//		project1.setProjectName("Alpha");
//
//		ProjectDetails project2 = new ProjectDetails();
//		project2.setProjectName("Beta");
//
//		user.setManagedProjects(List.of(project1, project2));
//
//		when(userInfoRepository.findAllEnabledManagersWithProjects(Roles.PROJECT_MANAGER)).thenReturn(List.of(user));
//
//		// When
//		List<ProjectManagerDTO> result = projectManagerService.getAllProjectManagersWithProjects();
//
//		// Then
//		assertThat(result).hasSize(1);
//
//		ProjectManagerDTO dto = result.get(0);
//		assertThat(dto.getId()).isEqualTo("EMP101");
//		assertThat(dto.getName()).isEqualTo("John Doe");
//		assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
//		assertThat(dto.getProjects()).containsExactly("Alpha", "Beta");
//
//		verify(userInfoRepository, times(1)).findAllEnabledManagersWithProjects(Roles.PROJECT_MANAGER);
//	}

}
