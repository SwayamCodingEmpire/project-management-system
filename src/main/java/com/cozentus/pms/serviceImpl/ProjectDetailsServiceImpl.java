package com.cozentus.pms.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.IdAndCodeDTO;
import com.cozentus.pms.dto.MailNotificationConfigDTO;
import com.cozentus.pms.dto.ProjectDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ProjectDetailsForProjectListDTO;
import com.cozentus.pms.dto.ProjectManagerProjectCountDTO;
import com.cozentus.pms.dto.ProjectMinimalDataDTO;
import com.cozentus.pms.dto.ProjectResourceSummaryCountDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownDTO;
import com.cozentus.pms.dto.ProjectTypeDropdownGroupDTO;
import com.cozentus.pms.dto.ProjectTypeOptionsDTO;
import com.cozentus.pms.dto.ResourceProjectMinimalDashboardDTO;
import com.cozentus.pms.dto.SkillDTO;
import com.cozentus.pms.dto.UserInfoIdentifierDTO;
import com.cozentus.pms.entites.Client;
import com.cozentus.pms.entites.ProjectDetails;
import com.cozentus.pms.entites.ProjectType;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.exceptions.RecordNotFoundException;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.ClientRepository;
import com.cozentus.pms.repositories.CredentialRepository;
import com.cozentus.pms.repositories.ProjectDetailsRepository;
import com.cozentus.pms.repositories.ProjectTypeRepository;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.repositories.UserSkillDetailRepository;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.EmailService;
import com.cozentus.pms.services.ProjectDetailsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class ProjectDetailsServiceImpl implements ProjectDetailsService {
	@PersistenceContext
	private EntityManager entityManager;
	private final AuthenticationService authenticationService;
	private final ProjectDetailsRepository projectDetailsRepository;
	private final UserSkillDetailRepository userSkillDetailRepository;
	private final UserInfoRepository userInfoRepository;
	private final ClientRepository clientRepository;
	private final EmailService emailService;
	private final CredentialRepository credentialRepository;
	private final ProjectTypeRepository projectTypeRepository;
	private final SkillRepository skillRepository;
	public ProjectDetailsServiceImpl(ProjectDetailsRepository projectDetailsRepository, ClientRepository clientRepository, UserInfoRepository userInfoRepository, EmailService emailService, CredentialRepository credentialRepository, ProjectTypeRepository projectTypeRepository, SkillRepository skillRepository, UserSkillDetailRepository userSkillDetailRepository, AuthenticationService authenticationService) {
		this.projectDetailsRepository = projectDetailsRepository;
		this.authenticationService = authenticationService;
		this.clientRepository = clientRepository;
		this.userInfoRepository = userInfoRepository;
		this.emailService = emailService;
		this.credentialRepository = credentialRepository;
		this.projectTypeRepository = projectTypeRepository;
		this.skillRepository = skillRepository;
		this.userSkillDetailRepository = userSkillDetailRepository;
	}

	@Override
	@Caching(evict = {
			@CacheEvict(value = "resources", allEntries = true),
		    @CacheEvict(value = "projectManagers", key = "'allProjectManagersWithProjects'"),
		    @CacheEvict(value = "projects", allEntries = true),
			@CacheEvict(value = "resourceAndAssociatedProjects", key = "'allProjectManagersWithProjects'")
		})
	@Transactional
	public void createProjectDetails(ProjectDTO projectDTO) {
		
		ProjectDetails projectDetails = new ProjectDetails(projectDTO.projectInfo(), projectDTO.projectType());
		if(!projectTypeRepository.existsById(projectDTO.projectType().projectType())) {
			throw new RecordNotFoundException("Project type not found");
		}
		
		ProjectType projectType = entityManager.getReference(ProjectType.class, projectDTO.projectType().projectType());
		projectDetails.setProjectType(projectType);
		Client client;
		if(!projectDTO.projectType().customerProject()) {
			client = entityManager.getReference(Client.class, 1);
		}
		else if (projectDTO.customerInfo().id() != null) {
			if (!clientRepository.existsById(projectDTO.customerInfo().id())) {
			    throw new RecordNotFoundException("Client not found");
			}
			client = entityManager.getReference(Client.class, projectDTO.customerInfo().id());
		} else {
		    client = new Client(projectDTO.customerInfo());
		}

		projectDetails.setCustomer(client);
		UserInfoIdentifierDTO managerDTO = userInfoRepository.findBasicsByEmpId(projectDTO.managerId())
				.orElseThrow(() -> new RecordNotFoundException("User not found"));
		if(managerDTO.role().equals(Roles.RESOURCE)) {
			credentialRepository.updateRoleByUserId(managerDTO.id(), Roles.PROJECT_MANAGER);
		}
		UserInfo manager = entityManager.getReference(UserInfo.class, managerDTO.id());
		projectDetails.setProjectManager(manager);
		UserInfo deliveryManager = entityManager.getReference(UserInfo.class, 32);
		projectDetails.setDeliveryManager(deliveryManager);	
		
		projectDetailsRepository.saveAndFlush(projectDetails);
		try {
			emailService.sendProjectCreationEmailToManager("emailManager@gmail.com", projectDTO, managerDTO.name());
		} catch (Exception e) {
			throw new RuntimeException("Failed to send email notification for project creation", e);
		}
		
	}

	@Override
	@Cacheable(
		    value = "dmProjects",
		    key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #deliveryManagerId"
		)
		public Page<ProjectDetailsForProjectListDTO> fetchAllProjectsForDeliveryManager(
		        String search, Pageable pageable, Integer deliveryManagerId) {
		    
		Page<ProjectDetailsForProjectListDTO> pages =  projectDetailsRepository.findAllProjectsForDeliveryManager(deliveryManagerId, search, pageable);
		log.info(pages.getContent().toString());
		return pages;
		
		}
	
	@Override
	@Cacheable(
		    value = "pmProjects",
		    key = "#search + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #projectManagerId"
		)
		public Page<ProjectDetailsForProjectListDTO> fetchAllProjectsForProjectManager(
		        String search, Pageable pageable, Integer projectManagerId) {
		    log.info("Fetching all projects for project manager with ID: {}", projectManagerId);
		Page<ProjectDetailsForProjectListDTO> pages =  projectDetailsRepository.findAllProjectsForProjectManager(projectManagerId, search, pageable);
		log.info(pages.getContent().toString());
		return pages;
		
		}

	


	@Caching(evict = {
			@CacheEvict(value = "resources", allEntries = true),
		    @CacheEvict(value = "projectManagers", key = "'allProjectManagersWithProjects'"),
		    @CacheEvict(value = "projects", allEntries = true),
		    @CacheEvict(value = "resourceAndAssociatedProjects", key = "'allProjectManagersWithProjects'")
		})
	@Override
	@Transactional
	public void updateProjectDetails(ProjectDTO projectDTO, String code) {
		
		ProjectDetails projectDetails = projectDetailsRepository.findByProjectCode(code).orElseThrow(() -> new RecordNotFoundException("Project not found"));;
		Client client;
		if(!projectDTO.projectType().customerProject()) {
			client = entityManager.getReference(Client.class, 31);
		}
		else if (projectDTO.customerInfo().id() != null) {
			if (!clientRepository.existsById(projectDTO.customerInfo().id())) {
			    throw new RecordNotFoundException("Client not found");
			}
			client = entityManager.getReference(Client.class, projectDTO.customerInfo().id());
		} else {
		    client = new Client(projectDTO.customerInfo());
		}
		
		if(!projectTypeRepository.existsById(projectDTO.projectType().projectType())) {
			throw new RecordNotFoundException("Project type not found");
		}
		ProjectType projectType = entityManager.getReference(ProjectType.class, projectDTO.projectType().projectType());
		projectDetails.setProjectType(projectType);
		projectDetails.updateProjectDetails(projectDTO.projectInfo(), projectDTO.projectType());
		projectDetails.setCustomer(client);
		UserInfoIdentifierDTO managerDTO = userInfoRepository.findBasicsByEmpId(projectDTO.managerId())
				.orElseThrow(() -> new RecordNotFoundException("User not found"));
		if(managerDTO.role().equals(Roles.RESOURCE)) {
			credentialRepository.updateRoleByUserId(managerDTO.id(), Roles.PROJECT_MANAGER);
		}
		UserInfo manager = entityManager.getReference(UserInfo.class, managerDTO.id());
		projectDetails.setProjectManager(manager);
		UserInfo deliveryManager = entityManager.getReference(UserInfo.class, 32);
		projectDetails.setDeliveryManager(deliveryManager);	
		
		projectDetailsRepository.saveAndFlush(projectDetails);
		
		try {
			
			emailService.sendProjectEditEmailToManager("managerEmail@cozentus.com", projectDTO, managerDTO.name());
		} catch (Exception e) {
			throw new RuntimeException("Failed to send email notification for project update", e);
		}
		
	}

	@Override
	public ProjectDTO getProjectDetails(String code) {
		return projectDetailsRepository.findByProjectCodeForEditForm(code)
				.orElseThrow(() -> new RecordNotFoundException("Project not found"));
	}

	@Override
	public MailNotificationConfigDTO getProjectMailConfig(String projectCode) {
		// TODO Auto-generated method stub
		return projectDetailsRepository.findProjectDetailsByProjectCode(projectCode)
				.orElseThrow(() -> new RecordNotFoundException("Mail configuration not found for project code: " + projectCode));
	}
	
	@Override	
	public void updateProjectMailConfig(MailNotificationConfigDTO mailConfig, String projectCode) {
		int updatedRows = projectDetailsRepository.updateMailConfigByProjectCode(
				projectCode, mailConfig.timesheetSummaryDay(), mailConfig.timesheetWarningDay1(),
				mailConfig.timesheetWarningDay2(), mailConfig.timesheetReminderDay());
		if (updatedRows == 0) {
			throw new RecordNotFoundException("Project not found or mail configuration not updated");
		}
	}
	
	@Override
	public void updateDefaultProjectMailConfig(MailNotificationConfigDTO mailConfig) {
		int updatedRows = projectDetailsRepository.updateDefaultMailConfig(
				mailConfig.timesheetSummaryDay(), mailConfig.timesheetWarningDay1(),
				mailConfig.timesheetWarningDay2(), mailConfig.timesheetReminderDay());
		if (updatedRows == 0) {
			throw new RecordNotFoundException("Default project mail configuration not updated");
		}
	}
	
	public List<ProjectTypeDropdownGroupDTO> getAllProjectTypes() {
	    List<ProjectTypeDropdownDTO> flatList = projectDetailsRepository.findAllProjectTypes();

	    if (flatList.isEmpty()) {
	        throw new RecordNotFoundException("No project types found");
	    }

	    return flatList.stream()
	        .collect(Collectors.groupingBy(ProjectTypeDropdownDTO::projectCategory))
	        .entrySet().stream()
	        .map(entry -> new ProjectTypeDropdownGroupDTO(
	            entry.getKey(), // label = projectCategory
	            entry.getValue().stream()
	                .map(item -> new ProjectTypeOptionsDTO(item.id(), item.projectType(), item.projectType()))
	                .toList()
	        ))
	        .toList();
	}

	@Override
	public void addSkillsToResources(String empId, SkillDTO skills, SkillPriority skillPriority) {	
		IdAndCodeDTO userIDandEmpId = userInfoRepository.findIdAndEmpIdByEmpId(empId).orElseThrow(() -> new RecordNotFoundException("Project not found with code: " + empId));
		IdAndCodeDTO skillIdAndName = skillRepository.findIdAndNameBySkillsName(skills.skillName())
				.orElseThrow(() -> new RecordNotFoundException("Skill not found with name: " + skills.skillName()));
		UserInfo userInfo = entityManager.getReference(UserInfo.class, userIDandEmpId.id());
		Skill skill = entityManager.getReference(Skill.class, skillIdAndName.id());
		UserSkillDetail userSkillDetail = new UserSkillDetail();
		userSkillDetail.setUser(userInfo);
		userSkillDetail.setSkill(skill);
		userSkillDetail.setPriority(skillPriority);
		userSkillDetail.setExperienceInYears(skills.skillExperience());
		userSkillDetailRepository.save(userSkillDetail);
	}
	
	@Override
	public List<ProjectDashboardDTO> getDashboardData(Integer managerId, Roles role) {
		
		List<ProjectDashboardDTO> dashboardData;
		if(role.equals(Roles.DELIVERY_MANAGER)) {
	    dashboardData = projectDetailsRepository.findAllDashboardData(managerId);
		} else {
			dashboardData = projectDetailsRepository.getPmDashboardProjectListByManager(managerId);
		}
	    
	    if (dashboardData.isEmpty()) {
	        throw new RecordNotFoundException("No dashboard data found for delivery manager ID: " + managerId);
	    }

	    return dashboardData;
	}
	
	public List<ProjectManagerProjectCountDTO> getProjectManagersUnderManager(String dmEmpId) {
//		String dmEmpId = "CZ0433"; // Replace with actual delivery manager employee ID
	    return projectDetailsRepository.findProjectManagersByDeliveryManager(dmEmpId);
	}
	
	public List<ProjectMinimalDataDTO> getProjectsUnderManager(String projectManagerEmpId, String dmEmpId) {
		log.info("Fetching project managers under manager with empId: {}", projectManagerEmpId);
	    return projectDetailsRepository.findProjectMinimalDataByDeliveryManager(projectManagerEmpId, dmEmpId);
	}
	
	public List<ProjectResourceSummaryCountDTO> getProjectResourceSummaryByManager(String dmEmpId) {
		
	    return projectDetailsRepository.getProjectResourceSummaryByManager(dmEmpId);
	}
	
	public List<ResourceProjectMinimalDashboardDTO> getResourceProjectMinimalDashboardData(String projectCode) {
		return projectDetailsRepository.findResourceProjectMinimalDashboardByProjectCode(projectCode);
	}
	
	public List<ProjectDetailsForProjectListDTO> getAllProjectsForResource() {
		Integer resourceId = authenticationService.getCurrentUserDetails().getRight().userId();
		return projectDetailsRepository.findAllProjectsForResource(resourceId);
	}


	
	
	


}
