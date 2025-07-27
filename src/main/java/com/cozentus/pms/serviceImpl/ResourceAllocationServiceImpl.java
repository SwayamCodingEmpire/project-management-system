package com.cozentus.pms.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.ProjectAllocationDetailsDTO;
import com.cozentus.pms.dto.ProjectAllocationViewDTO;
import com.cozentus.pms.dto.ProjectAllocationsViewFlatDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationsDTO;
import com.cozentus.pms.dto.ProjectResourceAllocationsWithoutSkillsDTO;
import com.cozentus.pms.dto.ResourceAllocationSummaryDTO;
import com.cozentus.pms.dto.ResourceAllocationsDTO;
import com.cozentus.pms.dto.ResourceAllocationsFlatDTO;
import com.cozentus.pms.dto.ResourceAllocationsSubmitDTO;
import com.cozentus.pms.dto.ResourceFilterDTO;
import com.cozentus.pms.dto.ResourceWeeklySummaryDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.dto.UserSkillDTO;
import com.cozentus.pms.dto.UserSkillDetailsDTO;
import com.cozentus.pms.dto.UtilizationPairDTO;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.ResourceAllocation;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.EmailService;
import com.cozentus.pms.services.GptSkillNormalizerService;
import com.cozentus.pms.services.ResourceAllocationService;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ResourceAllocationServiceImpl implements ResourceAllocationService {
	@Autowired
	private EntityManager entityManager;
	private final UserInfoRepository userInfoRepository;
	private final ResourceAllocationRepository resourceAllocationRepository;
	private final ProjectDetailsRepository projectDetailsRepository;
	private final GptSkillNormalizerService gptSkillNormalizerService;
	private final EmailService emailService;
	private final AuthenticationService authenticationService;

	public ResourceAllocationServiceImpl(ResourceAllocationRepository resourceAllocationRepository,
			UserInfoRepository userInfoRepository, ProjectDetailsRepository projectDetailsRepository,
			GptSkillNormalizerService gptSkillNormalizerService, EmailService emailService,
			AuthenticationService authenticationService) {
		this.resourceAllocationRepository = resourceAllocationRepository;
		this.userInfoRepository = userInfoRepository;
		this.projectDetailsRepository = projectDetailsRepository;
		this.gptSkillNormalizerService = gptSkillNormalizerService;
		this.emailService = emailService;
		this.authenticationService = authenticationService;
	}

	@Override
	public List<ResourceAllocationsDTO> getAllResourceAllocations() {
	    List<ResourceAllocationsFlatDTO> resourceAllocationFlatDTO = resourceAllocationRepository
	            .findAllResourceAllocationsFlat(Roles.RESOURCE);
	    List<UserSkillDetailsDTO> userSingleSkillDTOs = userInfoRepository.fetchFlatUserSkillsByEmpIdIn(
	            resourceAllocationFlatDTO.stream().map(ResourceAllocationsFlatDTO::id).distinct().toList());
	    Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId = userSingleSkillDTOs.stream()
	            .collect(Collectors.groupingBy(UserSkillDetailsDTO::empId));

	    if (resourceAllocationFlatDTO.isEmpty()) {
	        throw new RecordNotFoundException("No resource allocations found");
	    }

	    // Group and convert to DTOs (custom logic here)
	    return toResourceAllocationsDTO(resourceAllocationFlatDTO, skillMapByEmpId);
	}
	
	
	public List<ResourceAllocationsDTO> toResourceAllocationsDTO(
			List<ResourceAllocationsFlatDTO> resourceAllocationsFlatDTO,
			Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId) {
		if (resourceAllocationsFlatDTO == null || resourceAllocationsFlatDTO.isEmpty()) {
			return List.of();
		}

		return resourceAllocationsFlatDTO.stream()
				.collect(Collectors.groupingBy(ResourceAllocationsFlatDTO::id, LinkedHashMap::new, Collectors.toList()))
				.entrySet().stream().map(entry -> {
					String empId = entry.getKey();
					List<ResourceAllocationsFlatDTO> resourceAllocationsTempDTO = entry.getValue();
					ResourceAllocationsFlatDTO first = resourceAllocationsTempDTO.get(0);

					BigDecimal dailyHours = defaultZero(first.dailyWorkingHours());
					List<ProjectAllocationDetailsDTO> projectAllocationsDTO = new ArrayList<>();

					BigDecimal totalBillability = BigDecimal.ZERO;
					BigDecimal totalPlannedUtil = BigDecimal.ZERO;
					BigDecimal totalActualUtil = BigDecimal.ZERO;
					int allocationCount = 0;
					// 🔹 ADD: Skill collection logic
					Set<String> seenSkillKeys = new HashSet<>();
					List<SkillDTO> primarySkills = new ArrayList<>();
					List<SkillDTO> secondarySkills = new ArrayList<>();

					List<UserSkillDetailsDTO> skills = skillMapByEmpId.getOrDefault(empId, List.of());
					//log.info(skills.toString());
					for (UserSkillDetailsDTO skill : skills) {
						String skillKey = skill.skillName().toUpperCase();
//						if (seenSkillKeys.add(skillKey)) { // Only add unique skills
							SkillDTO skillDTO = new SkillDTO(skill.skillName(), skill.skillExperience(), skill.level());
							if (skill.priority().equals(SkillPriority.PRIMARY)) {
								primarySkills.add(skillDTO);
							} else if (skill.priority().equals(SkillPriority.SECONDARY)) {
								secondarySkills.add(skillDTO);
							}
						}
//					}

					for (ResourceAllocationsFlatDTO flat : resourceAllocationsTempDTO) {
						ProjectAllocationDetailsDTO alloc = flat.currentAllocation();
						if (alloc == null || alloc.projectCode() == null)
							continue;

						BigDecimal plannedHours = defaultZero(alloc.plannedUtil());
						long daysWithEntries = Optional.ofNullable(alloc.daysWithEntries()).orElse(0L);

						BigDecimal plannedUtil = dailyHours.compareTo(BigDecimal.ZERO) > 0 ? plannedHours
								.divide(dailyHours, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
								: BigDecimal.ZERO;

						BigDecimal actualUtil = (dailyHours.compareTo(BigDecimal.ZERO) > 0 && daysWithEntries > 0)
								? BigDecimal.valueOf(daysWithEntries)
										.divide(dailyHours.multiply(BigDecimal.valueOf(daysWithEntries)), 4,
												RoundingMode.HALF_UP)
										.multiply(BigDecimal.valueOf(100))
								: BigDecimal.ZERO;

						totalBillability = totalBillability.add(defaultZero(alloc.billability()));
						totalPlannedUtil = totalPlannedUtil.add(plannedUtil);
						totalActualUtil = totalActualUtil.add(actualUtil);
						allocationCount++;

						ProjectAllocationDetailsDTO updatedAlloc = new ProjectAllocationDetailsDTO(alloc.projectCode(),
								alloc.projectName(),alloc.isCustomer(), alloc.from(), alloc.to(), alloc.role(),
								defaultZero(alloc.billability()), plannedUtil, actualUtil, daysWithEntries);

						projectAllocationsDTO.add(updatedAlloc);
					}

					BigDecimal avgBillability = average(totalBillability, allocationCount);

					return new ResourceAllocationsDTO(empId, first.name(), primarySkills, secondarySkills,
							first.designation(), first.experience(), projectAllocationsDTO, // ✅ always a list (can be
																							// empty)
							avgBillability, totalPlannedUtil, totalActualUtil);
				}).toList();
	}

	@Transactional
	public void allocateResources(ProjectResourceAllocationDTO projectResourceAllocationDTO) {
		String projectCode = projectResourceAllocationDTO.projectCode();
		long projectId = projectDetailsRepository.findIdByProjectCode(projectCode)
				.orElseThrow(() -> new RecordNotFoundException("Project not found with code: " + projectCode));
		ProjectDetails projectDetails = entityManager.getReference(ProjectDetails.class, projectId);
		List<ResourceAllocationsSubmitDTO> resourceAllocationsSubmitDTOs = projectResourceAllocationDTO.allocations();
		List<String> empIds = resourceAllocationsSubmitDTOs.stream().map(ResourceAllocationsSubmitDTO::id).toList();
		List<IdAndCodeDTO> foundResources = userInfoRepository.findIdAndEmpIdByEmpIdIn(empIds);
		validateEmpIds(empIds, foundResources);
		Map<String, Integer> empIdToIdMap = new HashMap<>();
		for (IdAndCodeDTO resource : foundResources) {
			empIdToIdMap.put(resource.code(), resource.id());
		}
		List<ResourceAllocation> resourceAllocations = new ArrayList<>();
		for (ResourceAllocationsSubmitDTO allocation : resourceAllocationsSubmitDTOs) {
			ResourceAllocation resourceAllocation = new ResourceAllocation(allocation);
			resourceAllocation.setProject(projectDetails);
			UserInfo resource = entityManager.getReference(UserInfo.class, empIdToIdMap.get(allocation.id()));
			resourceAllocation.setResource(resource);
			resourceAllocations.add(resourceAllocation);
		}

		if (!resourceAllocations.isEmpty()) {
			resourceAllocationRepository.saveAllAndFlush(resourceAllocations);
			log.info("Resource allocations saved successfully for project: {}", projectCode);
			resourceAllocationRepository.markAllocationsAsCompletedForResourceInBench(
					resourceAllocationsSubmitDTOs.stream().map(ResourceAllocationsSubmitDTO::id).toList());
			try {
				List<ResourceAllocationSummaryDTO> resourceAllocationSummaryDTOs = resourceAllocationRepository.findResourceAllocationSummaryByProjectCodeAndEmpId(projectCode, empIds);

				resourceAllocationSummaryDTOs = resourceAllocationSummaryDTOs.stream().map(
						resourceAllocationsSubmitDTO -> {
							return new ResourceAllocationSummaryDTO(
									resourceAllocationsSubmitDTO.projectCode(),
									resourceAllocationsSubmitDTO.projectName(),
									resourceAllocationsSubmitDTO.empId(),
									resourceAllocationsSubmitDTO.empEmail(),
									resourceAllocationsSubmitDTO.empName(),
									resourceAllocationsSubmitDTO.allocationStartDate(),
									resourceAllocationsSubmitDTO.allocationEndDate(),
									resourceAllocationsSubmitDTO.designation(),
									resourceAllocationsSubmitDTO.yearsOfExperience(),
									resourceAllocationsSubmitDTO.billabilityPercentage(),
									resourceAllocationsSubmitDTO.dailtyWOrkingHours(),
									resourceAllocationsSubmitDTO.plannedUtilizationPercentage().divide(resourceAllocationsSubmitDTO.dailtyWOrkingHours())	
							);
						}).toList();
				emailService.sendAllocationSummaryToResources(resourceAllocationSummaryDTOs, "managerEmail", "managerId", "managerName", "managerPhone");
			} catch (Exception e) {
				log.error("Error while sending mail: {}", e.getMessage());
			}

		} else {
			log.warn("No resource allocations to save for project: {}", projectCode);
		}

	}

	private BigDecimal defaultZero(BigDecimal value) {
		return value != null ? value : BigDecimal.ZERO;
	}

	private BigDecimal average(BigDecimal total, int count) {
		return count > 0 ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	public void validateEmpIds(List<String> requestedEmpIds, List<IdAndCodeDTO> found) {

		Set<String> foundEmpIds = found.stream().map(IdAndCodeDTO::code).collect(Collectors.toSet());

		List<String> invalidEmpIds = requestedEmpIds.stream().filter(id -> !foundEmpIds.contains(id)).toList();

		if (!invalidEmpIds.isEmpty()) {
			throw new RecordNotFoundException("Invalid empIds: " + invalidEmpIds);
		}
	}
	
	@Override
	public ResourceWeeklySummaryDTO getResourceProjectCountAndWeeklyHours() {
		Integer resourceId = authenticationService.getCurrentUserDetails().getRight().userId();
	    LocalDate today = LocalDate.now();
	    LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
	    LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

	    return resourceAllocationRepository.getWeeklySummaryForUser(resourceId, startOfWeek, endOfWeek);
	}

	@Override
	public List<ResourceAllocationsDTO> searchAmongResources(ResourceFilterDTO resourceFilterDTO) {
		List<String> empIds = null;
		if (!resourceFilterDTO.skill().isBlank()) {
			String skill = resourceFilterDTO.skill().isBlank() ? " " : resourceFilterDTO.skill();
			empIds = gptSkillNormalizerService.normalizeSkillSingle(new UserSkillDTO("Rnd124", List.of(skill)), 50);
			log.info("EmpIds found for skill {}: {}", skill, empIds);
		}

		List<String> designation = null;
		BigDecimal experience = resourceFilterDTO.experience() == null ? BigDecimal.ZERO
				: resourceFilterDTO.experience();

		if (resourceFilterDTO.designation().isEmpty() && (empIds == null || empIds.isEmpty())
				&& (resourceFilterDTO.experience() == null
						|| resourceFilterDTO.experience().compareTo(BigDecimal.ZERO) <= 0)) {
			log.info("No designation or empIds provided for search, returning all resources");
			return getAllResourceAllocations();
		}

		if (resourceFilterDTO.designation() == null || resourceFilterDTO.designation().isEmpty()) {
			log.info("No designation provided for search, ignoring designation filter");
			designation = null;
		} else {
			log.info("Designation provided for search, filtering resources by designation");
			designation = resourceFilterDTO.designation();
		}

		if (empIds != null && empIds.isEmpty()) {
			empIds = null;
		}

		List<ResourceAllocationsFlatDTO> resourceAllocationFlatDTO = resourceAllocationRepository
				.searchResourceAllocations(Roles.RESOURCE, empIds, designation, resourceFilterDTO.experience());

		List<UserSkillDetailsDTO> userSingleSkillDTOs = userInfoRepository.fetchFlatUserSkillsByEmpIdIn(empIds);
		Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId = userSingleSkillDTOs.stream()
				.collect(Collectors.groupingBy(UserSkillDetailsDTO::empId));

		if (resourceAllocationFlatDTO.isEmpty()) {
			throw new RuntimeException("No resource allocations found");
		}

		if (empIds != null && !empIds.isEmpty()) {
			Map<String, Integer> orderMap = new HashMap<>();
			for (int i = 0; i < empIds.size(); i++) {
				orderMap.put(empIds.get(i), i);
			}

			resourceAllocationFlatDTO
					.sort(Comparator.comparingInt(dto -> orderMap.getOrDefault(dto.id(), Integer.MAX_VALUE)));
		}

		return toResourceAllocationsDTO(resourceAllocationFlatDTO, skillMapByEmpId);
	}

	@Override
	public ProjectAllocationViewDTO getProjectAllocationsViewDTO(String projectCode, Pageable pageable) {
		List<ProjectAllocationsViewFlatDTO> resourceAllocations = resourceAllocationRepository
				.findAllResourceAllocationsForProject(Roles.RESOURCE, projectCode);
		List<UserSkillDetailsDTO> userSingleSkillDTOs = userInfoRepository.fetchFlatUserSkillsByEmpIdIn(
				resourceAllocations.stream().map(r -> r.resourceAllocations().empId()).toList());

		log.info("User skills fetched for project code {}: {}", projectCode, userSingleSkillDTOs);
		Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId = userSingleSkillDTOs.stream()
				.collect(Collectors.groupingBy(UserSkillDetailsDTO::empId));
		log.info("Skill map by empId for project code {}: {}", projectCode, skillMapByEmpId);
		if (resourceAllocations.isEmpty()) {
			throw new RecordNotFoundException("No resource allocations found for project code: " + projectCode);
		}

		return toProjectAllocationViewDTO(resourceAllocations, pageable, skillMapByEmpId);
	}

	public ProjectAllocationViewDTO toProjectAllocationViewDTO(List<ProjectAllocationsViewFlatDTO> flatDTOList,
			Pageable pageable, Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId) {
		if (flatDTOList == null || flatDTOList.isEmpty()) {
			return null;
		}

		ProjectAllocationsViewFlatDTO first = flatDTOList.get(0);

// Step 1: Convert flat DTOs to resource allocation DTOs
		List<ProjectResourceAllocationsDTO> allResources = convertToResourceAllocations(flatDTOList, skillMapByEmpId);

// Step 2: Apply pagination manually
		Page<ProjectResourceAllocationsDTO> page = createPagedResult(allResources, pageable);

// Step 3: Build final ProjectAllocationViewDTO
		return new ProjectAllocationViewDTO(first.projectCode(), first.projectName(), first.customerName(), page);
	}

	private List<ProjectResourceAllocationsDTO> convertToResourceAllocations(
			List<ProjectAllocationsViewFlatDTO> flatDTOList, Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId) {
		return flatDTOList.stream().map(flat -> createResourceAllocationDTO(flat, skillMapByEmpId)).toList();
	}

	private ProjectResourceAllocationsDTO createResourceAllocationDTO(ProjectAllocationsViewFlatDTO flat,
			Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId) {
		ProjectResourceAllocationsWithoutSkillsDTO res = flat.resourceAllocations();

// Calculate utilization metrics
		UtilizationMetrics metrics = calculateUtilizationMetrics(res);

// Process skills
		SkillCategories skillCategories = processSkills(res.empId(), skillMapByEmpId);

		return new ProjectResourceAllocationsDTO(res.empId(), res.name(), skillCategories.primarySkills(),
				skillCategories.secondarySkills(), res.designation(), metrics.dailyHours(), metrics.daysWorked(),
				res.allocationStartDate(), res.allocationEndDate(), res.actualAllocationEndDate(), res.role(),
				res.experience(), defaultZero(res.billability()), metrics.plannedUtil(), metrics.actualUtil());
	}

	private UtilizationMetrics calculateUtilizationMetrics(ProjectResourceAllocationsWithoutSkillsDTO res) {
		BigDecimal dailyHours = defaultZero(res.totalWorkingHoursDaily());
		BigDecimal plannedHours = defaultZero(res.plannedUtil());
		Long daysWorked = Optional.ofNullable(res.totalDaysWorked()).orElse(0L);

		BigDecimal plannedUtil = calculatePlannedUtilization(dailyHours, plannedHours);
		BigDecimal actualUtil = calculateActualUtilization(dailyHours, daysWorked);

		return new UtilizationMetrics(dailyHours, daysWorked, plannedUtil, actualUtil);
	}

	private BigDecimal calculatePlannedUtilization(BigDecimal dailyHours, BigDecimal plannedHours) {
		return dailyHours.compareTo(BigDecimal.ZERO) > 0
				? plannedHours.divide(dailyHours, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
				: BigDecimal.ZERO;
	}

	private BigDecimal calculateActualUtilization(BigDecimal dailyHours, Long daysWorked) {
		return (dailyHours.compareTo(BigDecimal.ZERO) > 0 && daysWorked > 0) ? BigDecimal.valueOf(daysWorked)
				.divide(dailyHours.multiply(BigDecimal.valueOf(daysWorked)), 4, RoundingMode.HALF_UP)
				.multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
	}

	private SkillCategories processSkills(String empId, Map<String, List<UserSkillDetailsDTO>> skillMapByEmpId) {
		List<UserSkillDetailsDTO> skills = skillMapByEmpId.getOrDefault(empId, List.of());
		log.info("Skills fetched for empId {}: {}", empId, skills);

		List<SkillDTO> primarySkills = new ArrayList<>();
		List<SkillDTO> secondarySkills = new ArrayList<>();

		for (UserSkillDetailsDTO skill : skills) {
			SkillDTO skillDTO = new SkillDTO(skill.skillName(), skill.skillExperience(), skill.level());

			if (skill.priority().equals(SkillPriority.PRIMARY)) {
				primarySkills.add(skillDTO);
			} else if (skill.priority().equals(SkillPriority.SECONDARY)) {
				secondarySkills.add(skillDTO);
			}
		}

		return new SkillCategories(primarySkills, secondarySkills);
	}

	private Page<ProjectResourceAllocationsDTO> createPagedResult(List<ProjectResourceAllocationsDTO> allResources,
			Pageable pageable) {
		int total = allResources.size();
		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), total);

		List<ProjectResourceAllocationsDTO> pagedContent = allResources.subList(start, end);

		return new PageImpl<>(pagedContent, pageable, total);
	}

//Helper record classes for better data organization
	private record UtilizationMetrics(BigDecimal dailyHours, Long daysWorked, BigDecimal plannedUtil,
			BigDecimal actualUtil) {
	}

	private record SkillCategories(List<SkillDTO> primarySkills, List<SkillDTO> secondarySkills) {
	}
	
	
	public List<UtilizationPairDTO> getResourceDashboardUtilStats(String empId, LocalDate startDate, LocalDate endDate) {
		List<UtilizationPairDTO> utilizationStats = resourceAllocationRepository.fetchDailyUtilization(empId, startDate, endDate);
		return utilizationStats;
	}
	
	@Transactional
	public void dellocateResource(String projectId, String empId) {
		LocalDate today = LocalDate.now();
		log.info("Deallocating resource with empId: {} from projectId: {}", empId, projectId);
		int deAllocated = resourceAllocationRepository.markAllocationsAsCompletedForResource(empId, projectId,today );
		log.info("Deallocated {} allocations for empId: {} in project: {}", deAllocated, empId, projectId);
		if(deAllocated == 0) {
			log.warn("No allocations found to deallocate for empId: {} in project: {}", empId, projectId);
			throw new RecordNotFoundException("No allocations found to deallocate for empId: " + empId + " in project: " + projectId);
		}
		long count = resourceAllocationRepository.countActiveAllocationsExcludingProject(empId, 1);
		if(count == 0) {
			log.info("No active allocations found for empId: {} after deallocation in project: {}", empId, projectId);
			LocalDate fixedEndDate = LocalDate.of(2026, 12, 31);
			resourceAllocationRepository.markAllocationForBench111(empId, 1, fixedEndDate);
		} else {
			log.info("Active allocations still exist for empId: {} after deallocation in project: {}", empId, projectId);
		}
	}
	
	@Transactional
	public void deallocateResourceFromDM(String resourceEmpId, Integer deliveryManagerId) {
		log.info("Deallocating resource with empId: {} from delivery manager with id: {}", resourceEmpId, deliveryManagerId);
		resourceAllocationRepository.markAlloationCompletedForMultipleAllocations(resourceEmpId, deliveryManagerId);

		LocalDate fixedEndDate = LocalDate.of(2026, 12, 31);
		log.info("Deallocating resource with empId: {} from delivery manager with id: {}", resourceEmpId, deliveryManagerId);
		resourceAllocationRepository.markAllocationForBench111(resourceEmpId, 1, fixedEndDate);
		userInfoRepository.deAllocateFromDM(resourceEmpId);
	}
	
	public void allocateToDM(String resourceEmpId, Integer deliveryManagerId) {
		log.info("Allocating resource with empId: {} to delivery manager with id: {}", resourceEmpId, deliveryManagerId);
		UserInfo deliveryManagerRef = entityManager.getReference(UserInfo.class, deliveryManagerId);
		int rows = userInfoRepository.allocateToDM(resourceEmpId, deliveryManagerRef);
		if(rows == 0) {
			log.warn("No allocations found to allocate for resourceEmpId: {} and deliveryManagerId: {}", resourceEmpId, deliveryManagerId);
			throw new AccessDeniedException("No allocations found to allocate for resourceEmpId: " + resourceEmpId + " and deliveryManagerId: " + deliveryManagerId);
		}
		log.info("Resource with empId: {} allocated to delivery manager with id: {}", resourceEmpId, deliveryManagerId);
	}

}
