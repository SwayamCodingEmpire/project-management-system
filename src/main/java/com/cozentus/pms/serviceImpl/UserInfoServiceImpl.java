package com.cozentus.pms.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.ConvertedSkills;
import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.ProjectAllocationDTO;
import com.cozentus.pms.dto.ProjectManagerDTO;
import com.cozentus.pms.dto.ProjectManagerFlatDTO;
import com.cozentus.pms.dto.ReportingManagerDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.ResourceBasics;
import com.cozentus.pms.dto.ResourceDTO;
import com.cozentus.pms.dto.ResourceEditDTO;
import com.cozentus.pms.dto.ResourceFlatDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.dto.SkillExperienceDTO;
import com.cozentus.pms.dto.SkillUpsertDTO;
import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.dto.UserSkillDetailsDTO;
import com.cozentus.pms.entites.Credential;
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
import com.cozentus.pms.services.UserInfoService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {
	@PersistenceContext
	private EntityManager entityManager;
	private final UserInfoRepository userInfoRepository;
	private final SkillRepository skillRepository;
	private final UserSkillDetailRepository userSkillDetailRepository;
	private final GptSkillNormalizerService gptSkillNormalizerService;
	private final AuthenticationService authenticationService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public UserInfoServiceImpl(UserInfoRepository userInfoRepository, SkillRepository skillRepository, 
			UserSkillDetailRepository userSkillDetailRepository, GptSkillNormalizerService gptSkillNormalizerService, AuthenticationService authenticationService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userInfoRepository = userInfoRepository;
		this.skillRepository = skillRepository;
		this.userSkillDetailRepository = userSkillDetailRepository;
		this.gptSkillNormalizerService= gptSkillNormalizerService;
		this.authenticationService = authenticationService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
//	@Override
//	@Cacheable(value = "projectManagers", key = "'allProjectManagersWithProjects'")
//	public List<ProjectManagerDTO> getAllProjectManagersWithProjects() {
//		// TODO Auto-generated method stub
//		List<UserInfo> manager =  userInfoRepository.findAllEnabledManagersWithProjects(Roles.PROJECT_MANAGER);
//		return manager.stream().map(user -> {
//			ProjectManagerDTO projectManagerDTO = new ProjectManagerDTO();
//			projectManagerDTO.setId(user.getEmpId());
//			projectManagerDTO.setName(user.getName());
//			projectManagerDTO.setEmail(user.getEmailId());
//			
//			List<String> projects = user.getManagedProjects().stream()
//					.map(project -> project.getProjectName())
//					.toList();
//			projectManagerDTO.setProjects(projects);
//			return projectManagerDTO;
//		}).toList();
//	}

	@Override
	@Cacheable(value = "projectManagers", key = "'allProjectManagersWithProjects'")
	public List<ProjectManagerDTO> getAllProjectManagersWithProjects() {
		List<ProjectManagerFlatDTO> projectManagersFlatDTO = userInfoRepository
				.findAllProjectManagersAndProjectNames(Roles.PROJECT_MANAGER);

		Map<String, ProjectManagerDTO> projectManagerMap = new LinkedHashMap<>();

		for (ProjectManagerFlatDTO row : projectManagersFlatDTO) {
			projectManagerMap.computeIfAbsent(row.empId(), empId -> {
				ProjectManagerDTO projectManagerDTO = new ProjectManagerDTO();
				projectManagerDTO.setValues(empId, row.name(), row.emailId(), new ArrayList<>());
				return projectManagerDTO;
			}).getProjects().add(row.projectName());
		}

		return new ArrayList<>(projectManagerMap.values());
	}
	
	@Override
	@Cacheable(value = "resourceAndAssociatedProjects", key = "'allProjectManagersWithProjects'")
	public List<ProjectManagerDTO> getAllProjectResourcesWithAssociatedProjectsProjects() {
		List<ProjectManagerFlatDTO> projectManagersFlatDTO = userInfoRepository
				.findAllResourcesWithProjectNames();

		Map<String, ProjectManagerDTO> projectManagerMap = new LinkedHashMap<>();

		for (ProjectManagerFlatDTO row : projectManagersFlatDTO) {
			projectManagerMap.computeIfAbsent(row.empId(), empId -> {
				ProjectManagerDTO projectManagerDTO = new ProjectManagerDTO();
				projectManagerDTO.setValues(empId, row.name(), row.emailId(), new ArrayList<>());
				return projectManagerDTO;
			});

			if (row.projectName() != null) {
				projectManagerMap.get(row.empId()).getProjects().add(row.projectName());
			}
		}

		return new ArrayList<>(projectManagerMap.values());
	}

	@Override
//	@Cacheable(value = "resources", key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
	public Page<ResourceDTO> getAllResourcesWithAllocations(String search, Pageable pageable) {
	    // Get ALL matching records without pagination first
	    List<ResourceFlatDTO> resourceFlatDTOLists = userInfoRepository.findAllResourcesWithAllocations(search, Roles.RESOURCE);
	    List<UserSkillDetailsDTO> userSingleSkillDTOs = userInfoRepository.fetchFlatUserSkillsByEmpIdIn(resourceFlatDTOLists.stream()
	            .map(ResourceFlatDTO::id)
	            .distinct()
	            .toList());
	    Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId = userSingleSkillDTOs.stream()
	            .collect(Collectors.groupingBy(UserSkillDetailsDTO::empId));
	    
	    log.info("Total resources found: {}", resourceFlatDTOLists);
	    Map<String, ResourceDTO> grouped = new LinkedHashMap<>();

	    for (ResourceFlatDTO resourceFlatDTO : resourceFlatDTOLists) {
	        ResourceDTO resource = grouped.computeIfAbsent(resourceFlatDTO.id(),
	                id -> {
	                    // FIX: Handle null skills list
	                    List<UserSkillDetailsDTO> employeeSkills = skillMapByEmpId.get(id);
	                    ConvertedSkills skills = convertToSkillLists(employeeSkills != null ? employeeSkills : Collections.emptyList());
	                    
	                    return new ResourceDTO(resourceFlatDTO.id(), resourceFlatDTO.name(), resourceFlatDTO.emailId(),
	                            resourceFlatDTO.phoneNumber(), skills.primarySkill(), skills.secondarySkill(),
	                            resourceFlatDTO.designation(),
	                            resourceFlatDTO.experience().doubleValue(), resourceFlatDTO.role(),
	                            resourceFlatDTO.reportingManagerId(), resourceFlatDTO.reportingManagerName(),
	                            resourceFlatDTO.deliveryManagerEmpId(), resourceFlatDTO.deliveryManagerName(),
	                            resourceFlatDTO.resourceRole(),
	                            new ArrayList<>());
	                });
	        
	        ProjectAllocationDTO projectAllocationDTO = resourceFlatDTO.allocation();
	        if (isValidAllocation(projectAllocationDTO)) {
	            resource.allocation().add(projectAllocationDTO);
	        }
	    }

	    List<ResourceDTO> resourceDTO = new ArrayList<>(grouped.values());

	    // Apply pagination AFTER grouping
	    int start = (int) pageable.getOffset();
	    int end = Math.min(start + pageable.getPageSize(), resourceDTO.size());

	    List<ResourceDTO> pagedResourceDTO = resourceDTO.subList(start, end);

	    return new PageImpl<>(pagedResourceDTO, pageable, resourceDTO.size());
	}
	
	private boolean isValidAllocation(ProjectAllocationDTO projectAllocationDTO) {
	    return projectAllocationDTO != null &&
	           projectAllocationDTO.projectCode() != null &&
	           projectAllocationDTO.projectName() != null &&
	           projectAllocationDTO.startDate() != null &&
	           projectAllocationDTO.endDate() != null;
	}


	@Transactional
	@Caching(evict = { @CacheEvict(value = "resources", allEntries = true),
			@CacheEvict(value = "reportingManagers", key = "'allReportingManagers'") })
	public void addResource(ResourceDTO resourceDTO) {
		if (userInfoRepository.existsByEmpId(resourceDTO.id())) {
			throw new IllegalArgumentException("Invalid Resource Data");
		}
		UserInfo userInfo = new UserInfo(resourceDTO);
		long managerId = userInfoRepository.findIdByEmpId(resourceDTO.reportingManagerId())
				.orElseThrow(() -> new RecordNotFoundException(
						"Reporting Manager not found with empId: " + resourceDTO.reportingManagerId()));
		UserInfo reportingManager = entityManager.getReference(UserInfo.class, managerId);
		userInfo.setReportingManager(reportingManager);
		Credential credential = new Credential();
		credential.setUsername(resourceDTO.emailId());
		credential.setPassword(bCryptPasswordEncoder.encode("C0Z1234")); // Set a default password or handle it as needed
		credential.setRole(resourceDTO.resourceRole());
		credential.setEnabled(true);
		userInfoRepository.save(userInfo);
	}

	@Transactional
	@Caching(evict = { @CacheEvict(value = "resources", allEntries = true),
	@CacheEvict(value = "reportingManagers", key = "'allReportingManagers'") })
	public void updateResource(ResourceEditDTO resourceEditDTO) {
		IdAndCodeDTO idAndCodeDTO = userInfoRepository.findIdAndEmpIdByEmpId(resourceEditDTO.reportingManager()).orElseThrow(() -> new RecordNotFoundException("Resource not found with empId: " + resourceEditDTO.id()));
		UserInfo userInfo = entityManager.getReference(UserInfo.class, idAndCodeDTO.id()); 
		int updateCount = userInfoRepository.updateResourceByEmpId(resourceEditDTO.id(), resourceEditDTO.role(), resourceEditDTO.designation(), 
				resourceEditDTO.experience(),userInfo);
		

		if (updateCount == 0) {
			throw new RecordNotFoundException("Resource not found with empId: " + resourceEditDTO.id());
		}
	}

	@Override
	@Cacheable(value = "reportingManagers", key = "'allReportingManagers'")
	public List<ReportingManagerDTO> getAllReportingManagers() {
		// TODO Auto-generated method stub
		return userInfoRepository.findAllByEnabledTrue();
	}
	

	private ConvertedSkills convertToSkillLists(List<UserSkillDetailsDTO> userSkills) {
	    if (userSkills == null || userSkills.isEmpty()) {
	        return new ConvertedSkills(Collections.emptyList(), Collections.emptyList());
	    }
	    
	    List<SkillDTO> primarySkills = userSkills.stream()
	            .filter(skill -> skill.priority() == SkillPriority.PRIMARY)
	            .map(skill -> new SkillDTO(skill.skillName(), skill.skillExperience(), skill.level()))
	            .toList();

	    List<SkillDTO> secondarySkills = userSkills.stream()
	            .filter(skill -> skill.priority() == SkillPriority.SECONDARY)
	            .map(skill -> new SkillDTO(skill.skillName(), skill.skillExperience(), skill.level()))
	            .toList();

	    return new ConvertedSkills(primarySkills, secondarySkills);
	}
	
	
	public List<String> getAllDesignations(){
		return userInfoRepository.findAllDesignations();
	}
	
	
	public List<ResourceBasicDTO> getAllResourcesAccordingToSkillsAndLevels(String skillName, String level, String search) {
		String empId = authenticationService.getCurrentUserDetails().getRight().empId();
		Roles role = authenticationService.getCurrentUserDetails().getLeft();
		log.info("Fetching resources with skill: {}, level: {}, search: {}", skillName, level, search);
		List<ResourceBasicDTO> resourceList = new ArrayList<>();
			if (search != null && !search.isBlank()) {
				List<String> empIds = gptSkillNormalizerService
						.normalizeSkillSingle(new UserSkillDTO("EMP123", List.of(search)), 20);
				resourceList = userInfoRepository.findAllResourcesWithSkillsAndLevelsByEmpId(skillName, level, empIds);
				log.info(resourceList.toString());	
			}
			else {
				resourceList = userInfoRepository.findAllResourcesWithSkillsAndLevels(skillName, level);
				
			}
	    

		
	    if (resourceList.isEmpty()) {
	        throw new RecordNotFoundException("No resources found with skill: " + skillName + " and level: " + level);
	    }

	    List<SkillExperienceDTO> experienceList = skillRepository.findExperienceBySkillAndLevel(skillName, level);
	    Map<String, BigDecimal> empIdToExperience = experienceList.stream()
	    	    .collect(Collectors.toMap(
	    	        SkillExperienceDTO::empId,
	    	        SkillExperienceDTO::experience,
	    	        (v1, v2) -> v1.max(v2)
	    	    ));


	    return resourceList.stream()
	        .map(res -> {
	            BigDecimal exp = empIdToExperience.getOrDefault(res.employeeId(), BigDecimal.ZERO);
	            BigDecimal utilization = res.utilization().divide(res.dailyWorkingHours(), 2, RoundingMode.HALF_UP);
	            return new ResourceBasicDTO(res.name(), res.employeeId(), res.designation(), exp, utilization, res.dailyWorkingHours());
	        })
	        .collect(Collectors.toList());
	}

	@Override
	@Caching(evict = { @CacheEvict(value = "resources", allEntries = true),
			@CacheEvict(value = "reportingManagers", key = "'allReportingManagers'") })
	public void updateResourceSkills(String empId, String skillName, SkillUpsertDTO skillUpsertDTO) {
		log.info("Updating skills for empId: {}, skillName: {}", empId, skillName);
		log.info("SkillUpsertDTO: {}", skillUpsertDTO);
		int updatedRows = userSkillDetailRepository.updateLevelAndExperienceByEmpIdAndSkillName(
				skillUpsertDTO.skillLevel(), 
				skillUpsertDTO.experience(), 
				empId, 
				skillName, 
				skillUpsertDTO.skillPriority()
		);
		
		if (updatedRows == 0) {
			throw new RecordNotFoundException("No skills found for empId: " + empId + " and skillName: " + skillName);
		}
		
	}
	@Caching(evict = { @CacheEvict(value = "resources", allEntries = true),
			@CacheEvict(value = "reportingManagers", key = "'allReportingManagers'") })
	public void addSkillToResources(String empId, String skillName, SkillUpsertDTO skillUpsertDTO) {
		log.info("Adding skill for empId: {}, skillName: {}", empId, skillName);
		log.info("SkillUpsertDTO: {}", skillUpsertDTO);
		
		if (userSkillDetailRepository.existsByEmpIdAndSkillName(empId, skillName)) {
			throw new IllegalArgumentException("Skill already exists for empId: " + empId + " and skillName: " + skillName);
		}
		
		Integer userId = userSkillDetailRepository.findUserIdIdByEmpId(empId)
				.orElseThrow(() -> new RecordNotFoundException("User not found with empId: " + empId));
		Integer skillId = userSkillDetailRepository.findSkillIdBySkillName(skillName)
				.orElseThrow(() -> new RecordNotFoundException("Skill not found with name: " + skillName));
		
		UserInfo userInfo = entityManager.getReference(UserInfo.class, userId);
		Skill skill = entityManager.getReference(Skill.class, skillId);
		UserSkillDetail userSkillDetails = new UserSkillDetail();
		userSkillDetails.setUser(userInfo);
		userSkillDetails.setSkill(skill);
		userSkillDetails.setLevel(skillUpsertDTO.skillLevel());
		userSkillDetails.setExperienceInYears(skillUpsertDTO.experience().setScale(2, RoundingMode.HALF_UP));
		userSkillDetails.setPriority(skillUpsertDTO.skillPriority());
		userSkillDetailRepository.save(userSkillDetails);
		log.info("Skill added successfully for empId: {}, skillName: {}", empId, skillName);
		gptSkillNormalizerService.populateQuadrantVectorDBForSingleUser(empId);
	}

	@Override
	public List<String> getAllSkills() {
		// TODO Auto-generated method stub
		return skillRepository.findAllSkills();
	}

	@Override
	public void deleteSkillFromResource(String empId, String skillName) {
		skillRepository.deleteSkillFromUserDetailSkill(empId, skillName);
		gptSkillNormalizerService.populateQuadrantVectorDBForSingleUser(empId);
		
	}
	
//	@Override
//	public Set<ResourceBasics> getAllResourceSkillLevel() {
//	    return userInfoRepository.findAllResourceSkillLevel();
//	}
	
	@Override
	public Set<ResourceBasics> getSkillsForDM(String dmEmpId) {
        return userInfoRepository.findAllResourceSkillLevel();
 
	}
	@Override
	public Set<ResourceBasics> getSkillsForPM(String pmEmpId) {
        return userInfoRepository.findAllResourceSkillLevelForPM(pmEmpId);
 
	}

}
