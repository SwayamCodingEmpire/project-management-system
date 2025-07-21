package com.cozentus.pms;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.ClientDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectDetailsDTO;
import com.cozentus.pms.dto.ProjectTypeDTO;
import com.cozentus.pms.entites.Client;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.repositories.ClientRepository;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.serviceImpl.ProjectDetailsServiceImpl;

@SpringBootTest
@Transactional
@Rollback
public class ProjectDetailsTest {

    @Autowired
    private ProjectDetailsServiceImpl projectDetailsService;

    @Autowired
    private ProjectDetailsRepository projectDetailsRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    private ProjectDTO getSampleProjectDTO(int clientId, String managerId) {
        return new ProjectDTO(
                new ProjectDetailsDTO(
                        "PX001", "Integration Test Project", "Description for testing",
                        LocalDate.now(), LocalDate.now().plusMonths(6),
                        "INR", "CLIENT","T&M"
                ),
                new ProjectTypeDTO(false, 4),
                new ClientDTO(clientId, "TestClient", "TestLE", "BU1"),
                managerId
        );
    }

    @Test
    void testCreateProjectDetails_ShouldPersistSuccessfully() {
        // Arrange: Add client and manager in DB
        Client client = new Client();
        client.setName("TestClient");
        client.setLegalEntity("TestLE");
        client.setBusinessUnit("BU1");
        client = clientRepository.save(client);

        UserInfo manager = new UserInfo();
        manager.setEmpId("PMX001");
        manager.setEmailId("pm@test.com");
        manager.setName("Test Manager");
        userInfoRepository.save(manager);

        ProjectDTO projectDTO = getSampleProjectDTO(client.getId(), "PMX001");

        // Act
        projectDetailsService.createProjectDetails(projectDTO);

        // Assert
        Optional<ProjectDetails> projectDetails = projectDetailsRepository.findByProjectCode("PX001");
        Assertions.assertTrue(projectDetails.isPresent());
        Assertions.assertEquals("Integration Test Project", projectDetails.get().getProjectName());

        // 
        
    }

    @Test
    void testUpdateProjectDetails_ShouldUpdateSuccessfully() {
        // Setup initial project
        Client client = new Client("TestClient", "TestLE", "BU1");
        client = clientRepository.save(client);

        UserInfo manager = new UserInfo();
        manager.setEmpId("PMX002");
        manager.setEmailId("pm2@test.com");
        manager.setName("Manager Two");
        userInfoRepository.save(manager);

        ProjectDetails project = new ProjectDetails();
        project.setProjectCode("PX002");
        project.setProjectName("Old Project");
        project.setCustomer(client);
        project.setProjectManager(manager);
        projectDetailsRepository.save(project);

        // Updated data
        ProjectDTO updateDTO = new ProjectDTO(
                new ProjectDetailsDTO("PX002", "Updated Name", "Updated Description",
                        LocalDate.now(), LocalDate.now().plusMonths(3),
                        "USD","CLIENT", "Fixed"),
                new ProjectTypeDTO(false, 4),
                new ClientDTO(client.getId(), client.getName(), client.getLegalEntity(), client.getBusinessUnit()),
                manager.getEmpId()
        );

        // Act
        projectDetailsService.updateProjectDetails(updateDTO, "PX002");

        // Assert
        ProjectDetails updated = projectDetailsRepository.findByProjectCode("PX002").get();
        Assertions.assertEquals("Updated Name", updated.getProjectName());
        Assertions.assertEquals("Fixed", updated.getContractType());

        
    }

//    @Test
//    void testFetchAllProjectsForDeliveryManager() {
//        // Assuming test data already exists in the database for DM with ID 32
//        List<ProjectDetailsForProjectListDTO> projects = projectDetailsService.fetchAllProjectsForDeliveryManager();
//        Assertions.assertNotNull(projects);
//    }
    
    @Test
    void testCreateProjectDetails_ClientNotFound_ShouldThrow() {
        // No client saved intentionally
        // But a valid manager is required
        UserInfo manager = new UserInfo();
        manager.setEmpId("PM404");
        manager.setEmailId("pm404@test.com");
        manager.setName("Ghost PM");
        userInfoRepository.save(manager);

        ProjectDTO dto = getSampleProjectDTO(9999, "PM404"); // Non-existing client ID

        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
            projectDetailsService.createProjectDetails(dto)
        );

        Assertions.assertEquals("Client not found", ex.getMessage());
    }
    
    @Test
    void testUpdateProjectDetails_ProjectNotFound_ShouldThrow() {
        // Arrange
        Client client = clientRepository.save(new Client("TestClient", "LE", "BU"));
        userInfoRepository.save(new UserInfo("PM999", "Missing PM", "pm999@test.com"));

        ProjectDTO dto = getSampleProjectDTO(client.getId(), "PM999");

        RecordNotFoundException ex = Assertions.assertThrows(RecordNotFoundException.class, () ->
            projectDetailsService.updateProjectDetails(dto, "INVALID_CODE")
        );

        Assertions.assertEquals("Project not found", ex.getMessage());
    }


}
