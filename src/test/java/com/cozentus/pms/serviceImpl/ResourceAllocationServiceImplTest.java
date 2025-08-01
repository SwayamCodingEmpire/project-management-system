package com.cozentus.pms.serviceImpl;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.*;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.serviceImpl.ResourceAllocationServiceImpl;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.EmailService;
import com.cozentus.pms.services.GptSkillNormalizerService;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ResourceAllocationServiceImplTest {

    @Mock
    private ResourceAllocationRepository resourceAllocationRepository;
    @Mock
    private UserInfoRepository userInfoRepository;
    @Mock
    private ProjectDetailsRepository projectDetailsRepository;
    @Mock
    private GptSkillNormalizerService gptSkillNormalizerService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private ResourceAllocationServiceImpl resourceAllocationServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceAllocationServiceImpl = new ResourceAllocationServiceImpl(
                resourceAllocationRepository,
                userInfoRepository,
                projectDetailsRepository,
                gptSkillNormalizerService,
                emailService,
                authenticationService
        );
    }

//    @Test
//    void testGetAllResourceAllocations_DeliveryManager_Success() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(101, "dm@cozentus.com", "EMP001");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);
//
//        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
//                "EMP001", "John Doe", "Developer", new BigDecimal("5.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ001", "Project X", true, null, null, "Dev", new BigDecimal("80"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO);
//
//        UserSkillDetailsDTO skill = new UserSkillDetailsDTO("EMP001", "Java", new BigDecimal("3.0"), SkillPriority.PRIMARY, "Expert");
//        List<UserSkillDetailsDTO> skills = List.of(skill);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 101)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP001"))).thenReturn(skills);
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        ResourceAllocationsDTO dto = result.get(0);
//        assertEquals("EMP001", dto.id());
//        assertEquals("John Doe", dto.name());
//        assertEquals(1, dto.primarySkill().size());
//        assertEquals("Java", dto.primarySkill().get(0).skillName());
//        assertEquals(0, dto.secondarySkill().size());
//        assertEquals("Developer", dto.designation());
//        assertEquals(new BigDecimal("5.0"), dto.experience());
//        assertEquals(1, dto.currentAllocation().size());
//    }

//    @Test
//    void testGetAllResourceAllocations_ProjectManager_Success() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(201, "pm@cozentus.com", "EMP002");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.PROJECT_MANAGER, userAuthDetails);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(userInfoRepository.findIdByDeliveryManagerId(201)).thenReturn(Optional.of(301));
//
//        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
//                "EMP002", "Jane Smith", "Tester", new BigDecimal("4.0"), new BigDecimal("7.5"),
//                new ProjectAllocationDetailsDTO("PRJ002", "Project Y", false, null, null, "QA", new BigDecimal("70"), new BigDecimal("7.5"), new BigDecimal("7.5"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO);
//
//        UserSkillDetailsDTO skill = new UserSkillDetailsDTO("EMP002", "Selenium", new BigDecimal("2.0"), SkillPriority.SECONDARY, "Intermediate");
//        List<UserSkillDetailsDTO> skills = List.of(skill);
//
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 301)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP002"))).thenReturn(skills);
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        ResourceAllocationsDTO dto = result.get(0);
//        assertEquals("EMP002", dto.id());
//        assertEquals("Jane Smith", dto.name());
//        assertEquals(0, dto.primarySkill().size());
//        assertEquals(1, dto.secondarySkill().size());
//        assertEquals("Selenium", dto.secondarySkill().get(0).skillName());
//    }

//    @Test
//    void testGetAllResourceAllocations_SkillMapping() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(101, "dm@cozentus.com", "EMP003");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);
//
//        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
//                "EMP003", "Alice", "Lead", new BigDecimal("6.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ003", "Project Z", true, null, null, "Lead", new BigDecimal("90"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO);
//
//        UserSkillDetailsDTO skill1 = new UserSkillDetailsDTO("EMP003", "Python", new BigDecimal("4.0"), SkillPriority.PRIMARY, "Advanced");
//        UserSkillDetailsDTO skill2 = new UserSkillDetailsDTO("EMP003", "Docker", new BigDecimal("2.0"), SkillPriority.SECONDARY, "Intermediate");
//        List<UserSkillDetailsDTO> skills = List.of(skill1, skill2);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 101)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP003"))).thenReturn(skills);
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        ResourceAllocationsDTO dto = result.get(0);
//        assertEquals(1, dto.primarySkill().size());
//        assertEquals("Python", dto.primarySkill().get(0).skillName());
//        assertEquals(1, dto.secondarySkill().size());
//        assertEquals("Docker", dto.secondarySkill().get(0).skillName());
//    }

    @Test
    void testGetAllResourceAllocations_ProjectManager_NoDeliveryManager() {
        UserAuthDetails userAuthDetails = new UserAuthDetails(202, "pm2@cozentus.com", "EMP004");
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.PROJECT_MANAGER, userAuthDetails);

        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        when(userInfoRepository.findIdByDeliveryManagerId(202)).thenReturn(Optional.empty());

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () ->
                resourceAllocationServiceImpl.getAllResourceAllocations()
        );
        assertEquals("No delivery manager found for the project manager", ex.getMessage());
    }

    @Test
    void testGetAllResourceAllocations_NoResourceAllocations() {
        UserAuthDetails userAuthDetails = new UserAuthDetails(101, "dm@cozentus.com", "EMP005");
        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);

        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 101)).thenReturn(Collections.emptyList());

        RecordNotFoundException ex = assertThrows(RecordNotFoundException.class, () ->
                resourceAllocationServiceImpl.getAllResourceAllocations()
        );
        assertEquals("No resource allocations found", ex.getMessage());
    }

//    @Test
//    void testGetAllResourceAllocations_UserWithNoSkills() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(101, "dm@cozentus.com", "EMP006");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);
//
//        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
//                "EMP006", "Bob", "Analyst", new BigDecimal("2.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ004", "Project A", false, null, null, "Analyst", new BigDecimal("60"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 101)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP006"))).thenReturn(Collections.emptyList());
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        ResourceAllocationsDTO dto = result.get(0);
//        assertTrue(dto.primarySkill().isEmpty());
//        assertTrue(dto.secondarySkill().isEmpty());
//    }

//    @Test
//    void testGetAllResourceAllocations_MultipleResourcesMultipleAllocations() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(101, "dm@cozentus.com", "EMP007");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.DELIVERY_MANAGER, userAuthDetails);
//
//        ResourceAllocationsFlatDTO flatDTO1 = new ResourceAllocationsFlatDTO(
//                "EMP007", "Charlie", "Dev", new BigDecimal("3.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ005", "Project B", true, null, null, "Dev", new BigDecimal("75"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        ResourceAllocationsFlatDTO flatDTO2 = new ResourceAllocationsFlatDTO(
//                "EMP007", "Charlie", "Dev", new BigDecimal("3.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ006", "Project C", false, null, null, "Dev", new BigDecimal("80"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        ResourceAllocationsFlatDTO flatDTO3 = new ResourceAllocationsFlatDTO(
//                "EMP008", "Diana", "QA", new BigDecimal("2.0"), new BigDecimal("7.0"),
//                new ProjectAllocationDetailsDTO("PRJ007", "Project D", true, null, null, "QA", new BigDecimal("65"), new BigDecimal("7"), new BigDecimal("7"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO1, flatDTO2, flatDTO3);
//
//        UserSkillDetailsDTO skill1 = new UserSkillDetailsDTO("EMP007", "Spring", new BigDecimal("2.0"), SkillPriority.PRIMARY, "Intermediate");
//        UserSkillDetailsDTO skill2 = new UserSkillDetailsDTO("EMP008", "Jenkins", new BigDecimal("1.0"), SkillPriority.SECONDARY, "Beginner");
//        List<UserSkillDetailsDTO> skills = List.of(skill1, skill2);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 101)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP007", "EMP008"))).thenReturn(skills);
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(2, result.size());
//        ResourceAllocationsDTO charlie = result.stream().filter(dto -> dto.id().equals("EMP007")).findFirst().orElse(null);
//        ResourceAllocationsDTO diana = result.stream().filter(dto -> dto.id().equals("EMP008")).findFirst().orElse(null);
//
//        assertNotNull(charlie);
//        assertEquals(2, charlie.currentAllocation().size());
//        assertEquals(1, charlie.primarySkill().size());
//        assertEquals("Spring", charlie.primarySkill().get(0).skillName());
//
//        assertNotNull(diana);
//        assertEquals(1, diana.currentAllocation().size());
//        assertEquals(1, diana.secondarySkill().size());
//        assertEquals("Jenkins", diana.secondarySkill().get(0).skillName());
//    }

//    @Test
//    void testGetAllResourceAllocations_UnsupportedRole() {
//        UserAuthDetails userAuthDetails = new UserAuthDetails(999, "other@cozentus.com", "EMP999");
//        Pair<Roles, UserAuthDetails> authPair = Pair.of(Roles.RESOURCE, userAuthDetails);
//
//        ResourceAllocationsFlatDTO flatDTO = new ResourceAllocationsFlatDTO(
//                "EMP999", "Eve", "Support", new BigDecimal("1.0"), new BigDecimal("8.0"),
//                new ProjectAllocationDetailsDTO("PRJ008", "Project E", false, null, null, "Support", new BigDecimal("50"), new BigDecimal("8"), new BigDecimal("8"), 1L)
//        );
//        List<ResourceAllocationsFlatDTO> flatList = List.of(flatDTO);
//
//        when(authenticationService.getCurrentUserDetails()).thenReturn(authPair);
//        when(resourceAllocationRepository.findAllResourceAllocationsFlat(Roles.RESOURCE, 999)).thenReturn(flatList);
//        when(userInfoRepository.fetchFlatUserSkillsByEmpIdIn(List.of("EMP999"))).thenReturn(Collections.emptyList());
//
//        List<ResourceAllocationsDTO> result = resourceAllocationServiceImpl.getAllResourceAllocations();
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals("EMP999", result.get(0).id());
//    }
}