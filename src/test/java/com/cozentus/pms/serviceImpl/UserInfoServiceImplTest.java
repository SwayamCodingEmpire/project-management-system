package com.cozentus.pms.serviceImpl;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.ProjectAllocationDTO;
import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.dto.ProjectManagerFlatDTO;
import com.cozentus.pms.dto.ReportingManagerDTO;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.dto.ResourceEditDTO;
import com.cozentus.pms.dto.ResourceFlatDTO;
import com.cozentus.pms.dto.SkillUpsertDTO;
import com.cozentus.pms.dto.UserSkillDetailsDTO;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.repositories.UserSkillDetailRepository;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.GptSkillNormalizerService;

import jakarta.persistence.EntityManager;

@SpringBootTest
class UserInfoServiceImplTest {

    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private SkillRepository skillRepository;
    @Mock
    private UserSkillDetailRepository userSkillDetailRepository;
    @Mock
    private GptSkillNormalizerService gptSkillNormalizerService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private UserInfoServiceImpl userInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Remove this manual instantiation - let @InjectMocks handle it
        // userInfoService = new UserInfoServiceImpl(
        //         userInfoRepository,
        //         skillRepository,
        //         userSkillDetailRepository,
        //         gptSkillNormalizerService,
        //         authenticationService,
        //         bCryptPasswordEncoder
        // );
    }

    // Rest of your test methods remain the same...

//    @Test
//    void testAddResource_withValidData_persistsResource() {
//        ResourceDTO resourceDTO = new ResourceDTO("EMP1", "John Doe", "john@coz.com", "1234567890", null, null, "Developer", 5.0, "RESOURCE", "RM1", "Manager", null, null, Roles.RESOURCE, new ArrayList<>());
//        when(userInfoRepository.existsByEmpId("EMP1")).thenReturn(false);
//        when(userInfoRepository.findIdByEmpId("RM1")).thenReturn(Optional.of(100L));
//        UserInfo reportingManager = new UserInfo();
//        when(entityManager.getReference(UserInfo.class, 100L)).thenReturn(reportingManager);
//        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("hashedPwd");
//
//        userInfoService.addResource(resourceDTO);
//
//        verify(userInfoRepository).save(any(UserInfo.class));
//    }

    @Test
    void testAddResource_withDuplicateEmpId_throwsException() {
        ResourceDTO resourceDTO = new ResourceDTO("EMP1", "John Doe", "john@coz.com", "1234567890", null, null, "Developer", 5.0, "RESOURCE", "RM1", "Manager", null, null, Roles.RESOURCE, new ArrayList<>());
        when(userInfoRepository.existsByEmpId("EMP1")).thenReturn(true);

        assertThrows(RecordNotFoundException.class, () -> userInfoService.addResource(resourceDTO));
    }

    @Test
    void testUpdateResource_withInvalidReportingManager_throwsRecordNotFoundException() {
        ResourceEditDTO editDTO = new ResourceEditDTO("EMP2", "Developer", "Senior Developer", BigDecimal.valueOf(7), "RM2");
        when(userInfoRepository.findIdAndEmpIdByEmpId("RM2")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userInfoService.updateResource(editDTO));
    }

    @Test
    void testUpdateResourceSkills_withNonexistentUserSkill_throwsRecordNotFoundException() {
        SkillUpsertDTO skillUpsertDTO = new SkillUpsertDTO(BigDecimal.valueOf(3),"BEGINNER",  SkillPriority.PRIMARY);
        when(userSkillDetailRepository.updateLevelAndExperienceByEmpIdAndSkillName("Expert", BigDecimal.valueOf(3), "EMP3", "Java", SkillPriority.PRIMARY)).thenReturn(0);

        assertThrows(RecordNotFoundException.class, () -> userInfoService.updateResourceSkills("EMP3", "Java", skillUpsertDTO));
    }

//    @Test
//    void testAddSkillToResources_withValidInput_addsSkillAndUpdatesVectorDB() {
//        SkillUpsertDTO skillUpsertDTO = new SkillUpsertDTO(BigDecimal.valueOf(3),"BEGINNER",  SkillPriority.PRIMARY);
//        when(userSkillDetailRepository.existsByEmpIdAndSkillName("EMP4", "Python")).thenReturn(false);
//        when(userSkillDetailRepository.findUserIdIdByEmpId("EMP4")).thenReturn(Optional.of(300));
//        when(userSkillDetailRepository.findSkillIdBySkillName("Python")).thenReturn(Optional.of(400));
//        UserInfo userInfo = new UserInfo();
//        Skill skill = new Skill();
//        when(entityManager.getReference(UserInfo.class, 300)).thenReturn(userInfo);
//        when(entityManager.getReference(Skill.class, 400)).thenReturn(skill);
//
//        userInfoService.addSkillToResources("EMP4", "Python", skillUpsertDTO);
//
//        verify(userSkillDetailRepository).save(any(UserSkillDetail.class));
//        verify(gptSkillNormalizerService).populateQuadrantVectorDBForSingleUser("EMP4");
//    }

    @Test
    void testAddSkillToResources_withExistingSkill_throwsIllegalArgumentException() {
        SkillUpsertDTO skillUpsertDTO = new SkillUpsertDTO(BigDecimal.valueOf(3),"BEGINNER",  SkillPriority.PRIMARY);
        when(userSkillDetailRepository.existsByEmpIdAndSkillName("EMP4", "Python")).thenReturn(true);

        assertThrows(RecordNotFoundException.class, () -> userInfoService.addSkillToResources("EMP4", "Python", skillUpsertDTO));
    }

//    @Test
//    void testGetAllResourcesWithAllocations_withValidSearchAndPagination_returnsPagedResources() {
//        ResourceFlatDTO resource1 = mock(ResourceFlatDTO.class);
//        when(resource1.id()).thenReturn("EMP5");
//        when(resource1.name()).thenReturn("Jane");
//        when(resource1.emailId()).thenReturn("jane@coz.com");
//        when(resource1.phoneNumber()).thenReturn("1112223333");
//        when(resource1.designation()).thenReturn("QA");
//        when(resource1.experience()).thenReturn(BigDecimal.valueOf(4));
//        when(resource1.role()).thenReturn("RESOURCE");
//        when(resource1.reportingManagerId()).thenReturn("RM3");
//        when(resource1.reportingManagerName()).thenReturn("Manager3");
//        when(resource1.deliveryManagerEmpId()).thenReturn("DM1");
//        when(resource1.deliveryManagerName()).thenReturn("Delivery1");
//        when(resource1.resourceRole()).thenReturn(Roles.RESOURCE);
//        ProjectAllocationDTO allocation = mock(ProjectAllocationDTO.class);
//        when(allocation.projectCode()).thenReturn("P100");
//        when(allocation.projectName()).thenReturn("ProjX");
//        when(allocation.startDate()).thenReturn(LocalDate.now());
//        when(allocation.endDate()).thenReturn(LocalDate.now());
//        when(resource1.allocation()).thenReturn(allocation);
//
//        List<ResourceFlatDTO> resourceFlatDTOList = List.of(resource1);
//        when(userInfoRepository.findAllResourcesWithAllocations(anyString(), eq(Roles.RESOURCE))).thenReturn(resourceFlatDTOList);
//
//        UserSkillDetailsDTO skillDetails = new UserSkillDetailsDTO("EMP5", "Java", BigDecimal.valueOf(2), SkillPriority.PRIMARY, "Advanced");
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(anyList())).thenReturn(List.of(skillDetails));
//
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<ResourceDTO> result = userInfoService.getAllResourcesWithAllocations("Jane", pageable);
//
//        assertEquals(1, result.getTotalElements());
//        assertEquals("EMP5", result.getContent().get(0).id());
//    }

//    @Test
//    void testGetAllResourcesAccordingToSkillsAndLevels_withNoMatchingResources_throwsRecordNotFoundException() {
//        // Mock UserAuthDetails properly
//        UserAuthDetails mockUserAuthDetails = mock(UserAuthDetails.class);
//        when(mockUserAuthDetails.emailId()).thenReturn("email@email.com");
//        when(mockUserAuthDetails.empId()).thenReturn("EMP6");
//        // Add any other method calls that might be used
//        
//        when(authenticationService.getCurrentUserDetails())
//                .thenReturn(Pair.of(Roles.PROJECT_MANAGER, mockUserAuthDetails));
//
//        when(userInfoRepository.findAllResourcesWithSkillsAndLevels("Go", "Expert"))
//                .thenReturn(Collections.emptyList());
//
//        assertThrows(RecordNotFoundException.class, 
//            () -> userInfoService.getAllResourcesAccordingToSkillsAndLevels("Go", "Expert", null));
//    }

//    @Test
//    void testDeleteSkillFromResource_withValidEmpIdAndSkillName_deletesSkillAndUpdatesVectorDB() {
//        doNothing().when(skillRepository).deleteSkillFromUserDetailSkill("EMP7", "Scala");
//        doNothing().when(gptSkillNormalizerService).populateQuadrantVectorDBForSingleUser("EMP7");
//
//        userInfoService.deleteSkillFromResource("EMP7", "Scala");
//
//        verify(skillRepository).deleteSkillFromUserDetailSkill("EMP7", "Scala");
//        verify(gptSkillNormalizerService).populateQuadrantVectorDBForSingleUser("EMP7");
//    }

    @Test
    void testAddSkillToResources_withNonexistentUserOrSkill_throwsRecordNotFoundException() {
        SkillUpsertDTO skillUpsertDTO = new SkillUpsertDTO( BigDecimal.valueOf(1), "Beginner",SkillPriority.PRIMARY);
        when(userSkillDetailRepository.existsByEmpIdAndSkillName("EMP8", "Rust")).thenReturn(false);
        when(userSkillDetailRepository.findUserIdIdByEmpId("EMP8")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userInfoService.addSkillToResources("EMP8", "Rust", skillUpsertDTO));

        when(userSkillDetailRepository.findUserIdIdByEmpId("EMP8")).thenReturn(Optional.of(500));
        when(userSkillDetailRepository.findSkillIdBySkillName("Rust")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userInfoService.addSkillToResources("EMP8", "Rust", skillUpsertDTO));
    }

//    @Test
//    void testGetAllReportingManagers_withExistingManagers_returnsList() {
//        ReportingManagerDTO rm1 = new ReportingManagerDTO("RM1", "Manager1", "rm1@coz.com");
//        ReportingManagerDTO rm2 = new ReportingManagerDTO("RM2", "Manager2", "rm2@coz.com");
//        when(userInfoRepository.findAllByEnabledTrue()).thenReturn(List.of(rm1, rm2));
//
//        List<ReportingManagerDTO> result = userInfoService.getAllReportingManagers();
//
//        assertEquals(2, result.size());
//        assertTrue(result.contains(rm1));
//        assertTrue(result.contains(rm2));
//    }

    @Test
    void testUpdateResource_withNonexistentEmpId_throwsRecordNotFoundException() {
        ResourceEditDTO editDTO = new ResourceEditDTO("EMP9", "Developer", "Lead", BigDecimal.valueOf(10), "RM9");
        IdAndCodeDTO idAndCodeDTO = new IdAndCodeDTO(900, "RM9");
        when(userInfoRepository.findIdAndEmpIdByEmpId("RM9")).thenReturn(Optional.of(idAndCodeDTO));
        UserInfo reportingManager = new UserInfo();
        when(entityManager.getReference(UserInfo.class, 900)).thenReturn(reportingManager);
        when(userInfoRepository.updateResourceByEmpId("EMP9", "Developer", "Lead", BigDecimal.valueOf(10), reportingManager)).thenReturn(0);

        assertThrows(RecordNotFoundException.class, () -> userInfoService.updateResource(editDTO));
    }


    @Test
    void testAddResource_withInvalidReportingManagerEmpId_throwsRecordNotFoundException() {
        ResourceDTO resourceDTO = new ResourceDTO("EMP10", "Jane Doe", "jane@coz.com", "9876543210", null, null, "QA", 3.0, "RESOURCE", "RM10", "Manager", null, null, Roles.RESOURCE, new ArrayList<>());
        when(userInfoRepository.existsByEmpId("EMP10")).thenReturn(false);
        when(userInfoRepository.findIdByEmpId("RM10")).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> userInfoService.addResource(resourceDTO));
    }
}