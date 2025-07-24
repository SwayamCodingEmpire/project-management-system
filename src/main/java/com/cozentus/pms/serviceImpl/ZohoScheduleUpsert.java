package com.cozentus.pms.serviceImpl;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.ZohoEmployeeDTO;
import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.CredentialRepository;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.GptSkillNormalizerService;
import com.cozentus.pms.services.ZohoService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ZohoScheduleUpsert {

    private final GptSkillNormalizerService gptSkillNormalizerServiceImpl;
	private final ZohoService zohoService;
	private final UserInfoRepository userInfoRepository;
	private final CredentialRepository credentialRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final SkillRepository skillRepository;
	public ZohoScheduleUpsert(ZohoService zohoService, UserInfoRepository userInfoRepository, CredentialRepository credentialRepository, BCryptPasswordEncoder passwordEncoder, SkillRepository skillRepository, GptSkillNormalizerService gptSkillNormalizerServiceImpl) {
		this.zohoService = zohoService;
		this.userInfoRepository = userInfoRepository;
		this.credentialRepository = credentialRepository;
		this.passwordEncoder = passwordEncoder;
		this.skillRepository = skillRepository;
		this.gptSkillNormalizerServiceImpl = gptSkillNormalizerServiceImpl;
	}
	
	
	@Scheduled(cron = "0 0 9 * * TUE")
	@Transactional
	public void syncResourcesWithDB() {
		List<String> toInsertEmpIds = new ArrayList<>();
	    Pair<Map<String, ZohoEmployeeDTO>, Set<String>> zohoData = zohoService.fetchDataAllEmployeeDataFromZoho();
	    Map<String, ZohoEmployeeDTO> zohoMap = zohoData.getLeft();

	    Map<String, UserInfo> existingUserMap = userInfoRepository.findAll().stream()
	            .collect(Collectors.toMap(UserInfo::getEmpId, Function.identity()));
	    Set<String> allSkillNames = new HashSet<>();

	 // Pre-collect all skill names
	 zohoMap.values().forEach(dto -> {
	     if (dto.primarySkills() != null) {
	         allSkillNames.addAll(Arrays.stream(dto.primarySkills().split(","))
	                 .map(String::trim).map(String::toUpperCase).collect(Collectors.toSet()));
	     }
	     if (dto.secondarySkills() != null) {
	         allSkillNames.addAll(Arrays.stream(dto.secondarySkills().split(","))
	                 .map(String::trim).map(String::toUpperCase).collect(Collectors.toSet()));
	     }
	 });

	 // Load/Create skill entities
	 Map<String, Skill> skillMap = createAndSaveSkills(allSkillNames);


	    List<UserInfo> toSaveOrUpdate = new ArrayList<>();

	    for (Map.Entry<String, ZohoEmployeeDTO> entry : zohoMap.entrySet()) {
	        ZohoEmployeeDTO dto = entry.getValue();
	        String empId = dto.employeeId();

	        UserInfo user;
	        boolean isNew = false;
	        if (existingUserMap.containsKey(empId)) {
	            user = existingUserMap.get(empId);
	        } else {
	            user = createNewUser(dto);
	            List<UserSkillDetail> skills = createUserSkillDetails(dto, skillMap, user);
	            user.setUserSkillDetails(skills);
	        toInsertEmpIds.add(empId);
	            isNew = true;
	        }

	        boolean changed = false;
	        String fullName = dto.firstName() + " " + dto.lastName();
	        changed |= updateField(user::getName, user::setName, fullName);
	        changed |= updateField(user::getEmailId, user::setEmailId, dto.emailId());
	        changed |= updateField(user::getDesignation, user::setDesignation, dto.designation());
	        changed |= updateField(user::getEmployeeType, user::setEmployeeType, dto.employeeType());
	        log.info(dto.employeeType());

	        BigDecimal exp = new BigDecimal(dto.experience())
	                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
	        if (user.getExpInYears() == null || user.getExpInYears().compareTo(exp) != 0) {
	            user.setExpInYears(exp);
	            changed = true;
	        }
	        
	        user.setEmployeeType(dto.employeeType());

	        if (isNew || changed) {
	            existingUserMap.put(empId, user); // track newly created too
	            toSaveOrUpdate.add(user);
	        }
	    }

	    if (!toSaveOrUpdate.isEmpty()) {
	        userInfoRepository.saveAllAndFlush(toSaveOrUpdate);
	        log.info("Inserted/updated {} users (pass 1)", toSaveOrUpdate.size());
	    }

	    // ===== PASS 2: Reporting Manager Assignment =====
	    List<UserInfo> toUpdateRM = new ArrayList<>();

	    for (Map.Entry<String, ZohoEmployeeDTO> entry : zohoMap.entrySet()) {
	        String empId = entry.getKey();
	        ZohoEmployeeDTO dto = entry.getValue();

	        UserInfo user = existingUserMap.get(dto.employeeId());
	        if (user == null || dto.reportingManagerId() == null) continue;

	        String rmEmpId = extractEmployeeIdFromString(dto.reportingManagerName());
	        UserInfo currentRM = user.getReportingManager();
	        if (currentRM != null && rmEmpId.equals(currentRM.getEmpId())) continue;
	        String rmName = extractNameFromString(dto.reportingManagerName());
	        UserInfo rm = resolveReportingManager(rmEmpId, zohoMap, existingUserMap, rmName, dto.reportingManagerId(), dto.reportingManagerEmailId());
	        user.setReportingManager(rm);
	        user.setUpdatedBy("system");
	        toUpdateRM.add(user);
	    }

	    if (!toUpdateRM.isEmpty()) {
	        userInfoRepository.saveAllAndFlush(toUpdateRM);
	        log.info("Updated {} users with reporting managers (pass 2)", toUpdateRM.size());
	    }
	    
//	    gptSkillNormalizerServiceImpl.populateQuadrantVectorDBForMultiUser(toInsertEmpIds);
	}

	
	private UserInfo createNewUser(ZohoEmployeeDTO dto) {
	    UserInfo user = new UserInfo();
	    user.setEmpId(dto.employeeId());
	    user.setName(dto.firstName() + " " + dto.lastName());
	    user.setEmailId(dto.emailId());
	    user.setPhoneNo(dto.phoneNo());
	    user.setDesignation(dto.designation());
	    user.setRole(dto.organizationRole());
	    user.setExpInYears(new BigDecimal(dto.experience()).divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
	    user.setDailyWorkingHours(BigDecimal.valueOf(8));
	    user.setEmployeeType(dto.employeeType());
	    user.setEnabled(true);
	    user.setCreatedBy("system");
	    user.setCredential(createCredential(dto));
	    return user;
	}
	
	private Credential createCredential(ZohoEmployeeDTO dto) {
	    String username = dto.emailId();
	    Optional<Credential> existing = credentialRepository.findByUsername(username);
	    if (existing.isPresent()) return existing.get();

	    Credential credential = new Credential();
	    credential.setUsername(username);
	    credential.setPassword(passwordEncoder.encode("C0Z1234")); // default
	    credential.setRole(determineUserRole(dto.organizationRole(), dto.employeeId()));
	    credential.setEnabled(true);
	    credential.setCreatedBy("system");
	    return credential;
	}
	
    private Roles determineUserRole(String organizationRole, String employeeId) {
        String orgRole = organizationRole.toLowerCase().trim();
        
        if (orgRole.contains("delivery") && orgRole.contains("manager")) {
            log.info("Setting role as Delivery Manager for {} (Original: {})", 
                     employeeId, organizationRole);
            return Roles.DELIVERY_MANAGER;
        } else if (orgRole.contains("project") && orgRole.contains("manager")) {
            log.info("Setting role as Project Manager for {} (Original: {})", 
                     employeeId, organizationRole);
            return Roles.PROJECT_MANAGER;
        } else {
            log.info("Setting role as Resource for {} (Original: {})", 
                     employeeId, organizationRole);
            return Roles.RESOURCE;
        }
    }
    
    private boolean updateField(Supplier<String> getter, Consumer<String> setter, String newValue) {
        if (newValue != null && !newValue.equals(getter.get())) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
    
    private UserInfo resolveReportingManager(String empId, Map<String, ZohoEmployeeDTO> zohoMap, Map<String, UserInfo> userMap, String rmName, String rmZohoId, String rmEmailId) {
        if (userMap.containsKey(empId)) {
            return userMap.get(empId);
        }

        if (zohoMap.containsKey(rmZohoId)) {
            UserInfo rm = createNewUser(zohoMap.get(rmZohoId));
            UserInfo saved = userInfoRepository.save(rm);
            userMap.put(empId, saved);
            return saved;
        }
        UserInfo ghost = createGhostManager(empId, rmName, rmEmailId);
        UserInfo saved = userInfoRepository.save(ghost);
        userMap.put(empId, saved);
        return saved;
    }
    
    private UserInfo createGhostManager(String empId, String rmName, String rmEmailId) {
        UserInfo ghost = new UserInfo();
        ghost.setEmpId(empId);
        ghost.setName(rmName);
        ghost.setEmailId(rmEmailId);
        ghost.setPhoneNo("0000000000");
        ghost.setDesignation("Reporting Manager");
        ghost.setRole("Reporting Manager");
        ghost.setDailyWorkingHours(BigDecimal.valueOf(8.0));
        ghost.setExpInYears(BigDecimal.ZERO);
        ghost.setCreatedBy("system");
        ghost.setEnabled(true);

        Credential cred = new Credential();
        cred.setUsername(rmEmailId);
        cred.setPassword(passwordEncoder.encode("C0Z1234"));
        cred.setRole(Roles.RESOURCE);
        cred.setEnabled(true);
        cred.setCreatedBy("system");
        ghost.setCredential(cred);
        return ghost;
    }
    
    private String extractEmployeeIdFromString(String input) {
        if (input == null) return null;
        Matcher matcher = Pattern.compile("CZ\\d+").matcher(input);
        return matcher.find() ? matcher.group() : null;
    }
    
    private String extractNameFromString(String input) {
        if (input == null) return null;
        String[] parts = input.split("CZ\\d+");
        if (parts.length == 0) return "Unknown";
        return parts[0].replace("-", "").trim();
    }

    private Map<String, Skill> createAndSaveSkills(Set<String> skillSet) {
        Map<String, Skill> skillMap = new HashMap<>();

        // Fetch existing
        Map<String, Skill> existing = skillRepository.findAllBySkillNameIn(skillSet).stream()
                .filter(s -> s.getSkillName() != null)
                .collect(Collectors.toMap(s -> s.getSkillName().toUpperCase(), Function.identity()));

        List<Skill> toCreate = new ArrayList<>();

        for (String name : skillSet) {
            if (existing.containsKey(name)) {
                skillMap.put(name, existing.get(name));
            } else {
                Skill s = new Skill();
                s.setSkillName(name);
                toCreate.add(s);
                skillMap.put(name, s);
            }
        }

        if (!toCreate.isEmpty()) {
            List<Skill> saved = skillRepository.saveAll(toCreate);
            for (Skill s : saved) {
                skillMap.put(s.getSkillName().toUpperCase(), s);
            }
        }

        return skillMap;
    }
    
    private List<UserSkillDetail> createUserSkillDetails(ZohoEmployeeDTO dto, Map<String, Skill> skillMap, UserInfo user) {
        List<UserSkillDetail> details = new ArrayList<>();

        Stream.of(Map.entry(dto.primarySkills(), SkillPriority.PRIMARY),
                  Map.entry(dto.secondarySkills(), SkillPriority.SECONDARY))
              .filter(e -> e.getKey() != null && !e.getKey().isBlank())
              .flatMap(e -> Arrays.stream(e.getKey().split(","))
                      .map(String::trim)
                      .filter(s -> !s.isBlank())
                      .map(skillName -> Map.entry(skillName.toUpperCase(), e.getValue())))
              .forEach(pair -> {
                  Skill skill = skillMap.get(pair.getKey());
                  if (skill != null) {
                      UserSkillDetail usd = new UserSkillDetail();
                      usd.setSkill(skill);
                      usd.setUser(user);
                      usd.setPriority(pair.getValue());

                      String level = pair.getValue() == SkillPriority.PRIMARY ? dto.primarySkillLevel() : dto.secondarySkillLevel();
                      usd.setLevel(level != null && !level.isBlank() ? level : "Beginner");
                      usd.setExperienceInYears(BigDecimal.ZERO);

                      details.add(usd);
                  }
              });

        return details;
    }




    




}
