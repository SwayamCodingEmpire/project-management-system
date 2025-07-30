package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.*;
import com.cozentus.pms.entites.Client;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.*;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.EmailService;
import jakarta.persistence.EntityManager;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@SpringBootTest
class ProjectDetailsServiceImplTest {

    @Mock
    private ProjectDetailsRepository projectDetailsRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private CredentialRepository credentialRepository;
    @Mock
    private ProjectTypeRepository projectTypeRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillDetailRepository userSkillDetailRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private ProjectDetailsServiceImpl projectDetailsService;

    @Captor
    private ArgumentCaptor<ProjectDetails> projectDetailsCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        projectDetailsService = new ProjectDetailsServiceImpl(
                projectDetailsRepository,
                clientRepository,
                userInfoRepository,
                emailService,
                credentialRepository,
                projectTypeRepository,
                skillRepository,
                userSkillDetailRepository,
                authenticationService
        );
    }

//    @Test
//    void testCreateProjectDetails_Success() throws Exception {
//        ProjectTypeDTO projectTypeDTO = new ProjectTypeDTO(true, 2);
//        ClientDTO clientDTO = new ClientDTO(3, "ClientName", "Legal", "BU");
//        ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO("P001", "Project1", "Desc", null, null, "USD", "T&M", "Monthly");
//        ProjectDTO projectDTO = new ProjectDTO(projectDetailsDTO, projectTypeDTO, clientDTO, "MGR001");
//
//        when(projectTypeRepository.existsById(2)).thenReturn(true);
//        ProjectType projectType = mock(ProjectType.class);
//        when(entityManager.getReference(ProjectType.class, 2)).thenReturn(projectType);
//        when(clientRepository.existsById(3)).thenReturn(true);
//        Client client = mock(Client.class);
//        when(entityManager.getReference(Client.class, 3)).thenReturn(client);
//        UserInfoIdentifierDTO managerDTO = new UserInfoIdentifierDTO(5, "ManagerName", "manager@cozentus.com", Roles.PROJECT_MANAGER);
//        when(userInfoRepository.findBasicsByEmpId("MGR001")).thenReturn(Optional.of(managerDTO));
//        UserInfo manager = mock(UserInfo.class);
//        when(entityManager.getReference(UserInfo.class, 5)).thenReturn(manager);
//        when(authenticationService.getCurrentUserDetails()).thenReturn(Pair.of(Roles.DELIVERY_MANAGER, new com.cozentus.pms.config.UserAuthDetails(10, "email@email.com", "empId")));
//        when(projectDetailsRepository.findByProjectCode("P001")).thenReturn(Optional.empty());
////        when(projectDetailsRepository.existsByProjectCode("P001")).thenReturn(false);
////        when(projectDetailsRepository.existsByProjectName("Project1")).thenReturn(false);
//        when(projectTypeRepository.findById(2)).thenReturn(Optional.of(projectType));
//        when(clientRepository.findById(3)).thenReturn(Optional.of(client));
//        UserInfo deliveryManager = mock(UserInfo.class);
//        when(entityManager.getReference(UserInfo.class, 10)).thenReturn(deliveryManager);
//        when(projectDetailsRepository.saveAndFlush(any(ProjectDetails.class))).thenReturn(mock(ProjectDetails.class));
//        when(credentialRepository.updateRoleByUserId(5, Roles.PROJECT_MANAGER)).thenReturn(1);
//        when(emailService.sendProjectCreationEmailToManager(anyString(), eq(projectDTO), anyString())).thenReturn(CompletableFuture.completedFuture(null));
//
//        assertDoesNotThrow(() -> projectDetailsService.createProjectDetails(projectDTO));
//        verify(projectDetailsRepository).saveAndFlush(any(ProjectDetails.class));
//        verify(emailService).sendProjectCreationEmailToManager(anyString(), eq(projectDTO), anyString());
//    }

    @Test
    void testFetchAllProjectsForDeliveryManager_Success() {
        List<ProjectDetailsForProjectListDTO> projectList = List.of(
                new ProjectDetailsForProjectListDTO("P001", "Project1", "Client1", "USD", null, null, "Manager1", "Type1")
        );
        Page<ProjectDetailsForProjectListDTO> page = new PageImpl<>(projectList);
        when(projectDetailsRepository.findAllProjectsForDeliveryManager(1, "search", Pageable.unpaged())).thenReturn(page);

        Page<ProjectDetailsForProjectListDTO> result = projectDetailsService.fetchAllProjectsForDeliveryManager("search", Pageable.unpaged(), 1);

        assertEquals(1, result.getTotalElements());
        assertEquals("P001", result.getContent().get(0).projectCode());
    }

//    @Test
//    void testUpdateProjectDetails_Success() throws Exception {
//        ProjectTypeDTO projectTypeDTO = new ProjectTypeDTO(true, 2);
//        ClientDTO clientDTO = new ClientDTO(3, "ClientName", "Legal", "BU");
//        ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO("P001", "Project1", "Desc", null, null, "USD", "T&M", "Monthly");
//        ProjectDTO projectDTO = new ProjectDTO(projectDetailsDTO, projectTypeDTO, clientDTO, "MGR001");
//
//        ProjectDetails projectDetails = mock(ProjectDetails.class);
//        when(authenticationService.getCurrentUserDetails()).thenReturn(Pair.of(Roles.DELIVERY_MANAGER, new com.cozentus.pms.config.UserAuthDetails(10, "email@email.com", "empId")));
//        when(projectDetailsRepository.findByProjectCode("P001")).thenReturn(Optional.of(projectDetails));
//        when(projectTypeRepository.existsById(2)).thenReturn(true);
//        ProjectType projectType = mock(ProjectType.class);
//        when(entityManager.getReference(ProjectType.class, 2)).thenReturn(projectType);
//        when(clientRepository.existsById(3)).thenReturn(true);
//        Client client = mock(Client.class);
//        when(entityManager.getReference(Client.class, 3)).thenReturn(client);
//        UserInfoIdentifierDTO managerDTO = new UserInfoIdentifierDTO(5, "ManagerName", "manager@cozentus.com", Roles.PROJECT_MANAGER);
//        when(userInfoRepository.findBasicsByEmpId("MGR001")).thenReturn(Optional.of(managerDTO));
//        UserInfo manager = mock(UserInfo.class);
//        when(entityManager.getReference(UserInfo.class, 5)).thenReturn(manager);
//        UserInfo deliveryManager = mock(UserInfo.class);
//        when(entityManager.getReference(UserInfo.class, 10)).thenReturn(deliveryManager);
//        when(credentialRepository.updateRoleByUserId(5, Roles.PROJECT_MANAGER)).thenReturn(1);
//        when(projectDetailsRepository.saveAndFlush(projectDetails)).thenReturn(projectDetails);
//        when(emailService.sendProjectEditEmailToManager(anyString(), eq(projectDTO), anyString())).thenReturn(CompletableFuture.completedFuture(null));
//
//        assertDoesNotThrow(() -> projectDetailsService.updateProjectDetails(projectDTO, "P001"));
//        verify(projectDetailsRepository).saveAndFlush(projectDetails);
//        verify(emailService).sendProjectEditEmailToManager(anyString(), eq(projectDTO), anyString());
//    }

    @Test
    void testCreateProjectDetails_ProjectTypeNotFound() {
        ProjectTypeDTO projectTypeDTO = new ProjectTypeDTO(true, 99);
        ClientDTO clientDTO = new ClientDTO(3, "ClientName", "Legal", "BU");
        ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO("P001", "Project1", "Desc", null, null, "USD", "T&M", "Monthly");
        ProjectDTO projectDTO = new ProjectDTO(projectDetailsDTO, projectTypeDTO, clientDTO, "MGR001");

        when(projectTypeRepository.existsById(99)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.createProjectDetails(projectDTO));
    }

    @Test
    void testUpdateProjectDetails_ClientNotFound() {
        ProjectTypeDTO projectTypeDTO = new ProjectTypeDTO(true, 2);
        ClientDTO clientDTO = new ClientDTO(3, "ClientName", "Legal", "BU");
        ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO("P001", "Project1", "Desc", null, null, "USD", "T&M", "Monthly");
        ProjectDTO projectDTO = new ProjectDTO(projectDetailsDTO, projectTypeDTO, clientDTO, "MGR001");

        ProjectDetails projectDetails = mock(ProjectDetails.class);
        when(authenticationService.getCurrentUserDetails()).thenReturn(Pair.of(Roles.DELIVERY_MANAGER, new com.cozentus.pms.config.UserAuthDetails(10, "email@email.com", "empId")));
        when(projectDetailsRepository.findByProjectCode("P001")).thenReturn(Optional.of(projectDetails));
        when(projectTypeRepository.existsById(2)).thenReturn(true);
        when(clientRepository.existsById(3)).thenReturn(false);

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.updateProjectDetails(projectDTO, "P001"));
    }

    @Test
    void testGetProjectMailConfig_ProjectCodeNotFound() {
        when(projectDetailsRepository.findProjectDetailsByProjectCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.getProjectMailConfig("INVALID"));
    }

    @Test
    void testGetAllProjectTypes_GroupedSuccess() {
        List<ProjectTypeDropdownDTO> flatList = Arrays.asList(
                new ProjectTypeDropdownDTO(1, "TypeA", true),
                new ProjectTypeDropdownDTO(2, "TypeB", false)
        );
        when(projectDetailsRepository.findAllProjectTypes()).thenReturn(flatList);

        List<ProjectTypeDropdownGroupDTO> result = projectDetailsService.getAllProjectTypes();

        assertFalse(result.isEmpty());
        assertEquals(2, result.stream().map(ProjectTypeDropdownGroupDTO::options).map(List::size).reduce(0, Integer::sum));
    }

    @Test
    void testUpdateDefaultProjectMailConfig_NoRecordsUpdated() {
        MailNotificationConfigDTO mailConfig = new MailNotificationConfigDTO("Mon", "Tue", "Wed", "Thu");
        when(projectDetailsRepository.updateDefaultMailConfig("Mon", "Wed", "Thu", "Tue")).thenReturn(0);

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.updateDefaultProjectMailConfig(mailConfig));
    }

//    @Test
//    void testAddSkillsToResources_Success() {
//        String empId = "EMP001";
//        SkillDTO skillDTO = new SkillDTO("Java", BigDecimal.valueOf(3), "ADVANCED");
//        SkillPriority skillPriority = SkillPriority.PRIMARY;
//        IdAndCodeDTO userIdAndEmpId = new IdAndCodeDTO(1, empId);
//        IdAndCodeDTO skillIdAndName = new IdAndCodeDTO(2, "Java");
//        UserInfo userInfo = mock(UserInfo.class);
//        Skill skill = mock(Skill.class);
//
//        when(userInfoRepository.findIdAndEmpIdByEmpId(empId)).thenReturn(Optional.of(userIdAndEmpId));
//        when(skillRepository.findIdAndNameBySkillsName("Java")).thenReturn(Optional.of(skillIdAndName));
//        when(entityManager.getReference(UserInfo.class, 1)).thenReturn(userInfo);
//        when(entityManager.getReference(Skill.class, 2)).thenReturn(skill);
//        when(userSkillDetailRepository.save(any(UserSkillDetail.class))).thenReturn(new UserSkillDetail());
//
//        assertDoesNotThrow(() -> projectDetailsService.addSkillsToResources(empId, skillDTO, skillPriority));
//        verify(userSkillDetailRepository).save(any(UserSkillDetail.class));
//    }

    @Test
    void testAddSkillsToResources_SkillNotFound() {
        String empId = "EMP001";
        SkillDTO skillDTO = new SkillDTO("Python", BigDecimal.valueOf(2), "Beginner");
        SkillPriority skillPriority = SkillPriority.SECONDARY;
        IdAndCodeDTO userIdAndEmpId = new IdAndCodeDTO(1, empId);

        when(userInfoRepository.findIdAndEmpIdByEmpId(empId)).thenReturn(Optional.of(userIdAndEmpId));
        when(skillRepository.findIdAndNameBySkillsName("Python")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.addSkillsToResources(empId, skillDTO, skillPriority));
    }

    @Test
    void testFetchAllProjectsForProjectManager_Success() {
        List<ProjectDetailsForProjectListDTO> projectList = List.of(
                new ProjectDetailsForProjectListDTO("P002", "Project2", "Client2", "EUR", null, null, "Manager2", "Type2")
        );
        Page<ProjectDetailsForProjectListDTO> page = new PageImpl<>(projectList);
        when(projectDetailsRepository.findAllProjectsForProjectManager(2, "search", Pageable.unpaged())).thenReturn(page);

        Page<ProjectDetailsForProjectListDTO> result = projectDetailsService.fetchAllProjectsForProjectManager("search", Pageable.unpaged(), 2);

        assertEquals(1, result.getTotalElements());
        assertEquals("P002", result.getContent().get(0).projectCode());
    }

    @Test
    void testUpdateProjectMailConfig_ProjectCodeNotFound() {
        MailNotificationConfigDTO mailConfig = new MailNotificationConfigDTO("Mon", "Tue", "Wed", "Thu");
        when(projectDetailsRepository.updateMailConfigByProjectCode("P003", "Mon", "Wed", "Thu", "Tue")).thenReturn(0);

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.updateProjectMailConfig(mailConfig, "P003"));
    }

//    @Test
//    void testGetDashboardData_Success() {
//        List<ProjectDashboardDTO> dashboardData = List.of(
//                new ProjectDashboardDTO("P001", "Project1", "Client1", "Manager1", 80.0, 5L, 90.0, 85.0)
//        );
//        when(projectDetailsRepository.findAllDashboardData(1)).thenReturn(dashboardData);
//
//        List<ProjectDashboardDTO> result = projectDetailsService.getDashboardData(1, Roles.DELIVERY_MANAGER);
//
//        assertEquals(1, result.size());
//        assertEquals("P001", result.get(0).code());
//    }

    @Test
    void testGetDashboardData_NoDataFound() {
        when(projectDetailsRepository.findAllDashboardData(1)).thenReturn(Collections.emptyList());

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.getDashboardData(1, Roles.DELIVERY_MANAGER));
    }



    @Test
    void testGetAllProjectTypes_NoProjectTypesFound() {
        when(projectDetailsRepository.findAllProjectTypes()).thenReturn(Collections.emptyList());

        assertThrows(RecordNotFoundException.class, () -> projectDetailsService.getAllProjectTypes());
    }

    // Helper class for mocking UserAuthDetails (since it's not in the provided context)
    static class UserAuthDetails {
        private final Integer userId;
        public UserAuthDetails(Integer userId) { this.userId = userId; }
        public Integer userId() { return userId; }
    }
}