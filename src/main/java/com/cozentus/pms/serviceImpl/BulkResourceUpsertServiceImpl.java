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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cozentus.pms.dto.UserCreationEmailDTO;
import com.cozentus.pms.dto.ZohoEmployeeDTO;
import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.entites.Skill;
import com.cozentus.pms.entites.UserInfo;
import com.cozentus.pms.entites.UserSkillDetail;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.helpers.SkillPriority;
import com.cozentus.pms.repositories.SkillRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.GptSkillNormalizerService;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class BulkResourceUpsertServiceImpl {
    private final ZohoServiceImpl zohoService;
    private final SkillRepository skillRepository;
    private final UserInfoRepository userInfoRepository;
    private final GptSkillNormalizerService gptSkillNormalizerService;
    private final BCryptPasswordEncoder passwordEncoder;

    public BulkResourceUpsertServiceImpl(ZohoServiceImpl zohoService, SkillRepository skillRepository, 
            UserInfoRepository userInfoRepository, GptSkillNormalizerService gptSkillNormalizerService, BCryptPasswordEncoder passwordEncoder) {
        this.zohoService = zohoService;
        this.skillRepository = skillRepository;
        this.userInfoRepository = userInfoRepository;
        this.gptSkillNormalizerService = gptSkillNormalizerService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public void syncResourcesWithDB() {
        // Step 1: Fetch data from Zoho
        Pair<Map<String, ZohoEmployeeDTO>, Set<String>> zohoData = zohoService.fetchDataAllEmployeeDataFromZoho();
        Map<String, ZohoEmployeeDTO> zohoMap = zohoData.getLeft();
        Set<String> allSkillNames = normalizeSkillNames(zohoData.getRight());

        // Step 2: Fetch existing users and skills
        Map<String, UserInfo> existingUsers = getExistingUserMap();
        Map<String, Skill> skillMap = createAndSaveSkills(allSkillNames);

        // Step 3: Prepare lists
        Map<String, UserInfo> usersToInsert = new HashMap<>();
        List<UserInfo> usersToUpdate = new ArrayList<>();

        for (Map.Entry<String, ZohoEmployeeDTO> entry : zohoMap.entrySet()) {
            String empId = entry.getKey();
            ZohoEmployeeDTO dto = entry.getValue();

            if (!existingUsers.containsKey(empId)) {
                usersToInsert.put(empId, createNewUser(dto, skillMap));
            } else {
                UserInfo existing = existingUsers.get(empId);
                if (updateExistingUserIfNeeded(existing, dto)) {
                    usersToUpdate.add(existing);
                }
            }
        }

        // Step 4: Handle reporting managers and persist
        setReportingManagers(zohoMap, usersToInsert);
        Set<String> insertedEmpIds = usersToInsert.keySet();
        userInfoRepository.saveAllAndFlush(usersToInsert.values());
        userInfoRepository.saveAllAndFlush(usersToUpdate);

        if (!usersToInsert.isEmpty()) {
            log.info("Inserted {} new users", usersToInsert.size());
            log.info(insertedEmpIds.toString());
        }
    }

    
    @Transactional
    public void insertResources() {
        // Step 1: Fetch data from Zoho
        Pair<Map<String, ZohoEmployeeDTO>, Set<String>> pairs = zohoService.fetchDataAllEmployeeDataFromZoho();
        Map<String, ZohoEmployeeDTO> employeeDTOMap = pairs.getLeft();
        Set<String> skillSet = pairs.getRight().stream().map(String::trim)
			.map(String::toUpperCase) 			
			.collect(Collectors.toSet());
//        log.info(skillSet.toString());
        
        // Step 2: Process and save skills
        Map<String, Skill> skillMap = createAndSaveSkills(skillSet);
        
        // Step 3: Create user info objects (without reporting managers)
        Map<String, UserInfo> userInfoMap = createUserInfoObjects(employeeDTOMap, skillMap);
        
        // Step 4: Set reporting managers for all users
        setReportingManagers(employeeDTOMap, userInfoMap);
        
        // Step 5: Save all users
        
        List<UserCreationEmailDTO> userCreationMailDTOs =  userInfoMap.values().stream()
        					.map(userInfo -> new UserCreationEmailDTO(
				userInfo.getEmpId(),
				userInfo.getName(),
				"likun987654321@gmail.com",
				userInfo.getCredential().getUsername(),
				userInfo.getCredential().getPassword()
			)).collect(Collectors.toList());
        
        Set<String> insertedEmpIds = new HashSet<>(userInfoMap.keySet());
        userInfoRepository.saveAllAndFlush(userInfoMap.values());
//        
        gptSkillNormalizerService.populateQuadrantVectorDB();
    }
    
    
    
    private Map<String, Skill> createAndSaveSkills(Set<String> skillSet) {
        Map<String, Skill> skillMap = new HashMap<>();

        // Fetch all existing skills and map by UPPERCASE name
        Map<String, Skill> existingSkills = skillRepository.findAllBySkillNameIn(skillSet).stream()
            .filter(s -> s.getSkillName() != null)
            .collect(Collectors.toMap(
                s -> s.getSkillName().toUpperCase().trim(), // uppercase keys
                Function.identity(),
                (s1, s2) -> s1
            ));

        List<Skill> skillsToCreate = new ArrayList<>();

        for (String inputName : skillSet) {

            if (existingSkills.containsKey(inputName)) {
                // Skill already exists
                skillMap.put(inputName, existingSkills.get(inputName));
            } else {
                // Create new skill in uppercase
                Skill newSkill = new Skill();
                newSkill.setSkillName(inputName);
                skillsToCreate.add(newSkill);
                skillMap.put(inputName, newSkill); // placeholder; will be updated after save
            }
        }

        // Save only new skills
        if (!skillsToCreate.isEmpty()) {
            List<Skill> savedSkills = skillRepository.saveAll(skillsToCreate);
            for (Skill saved : savedSkills) {
                skillMap.put(saved.getSkillName(), saved); // override with saved instance
            }
        }

        return skillMap;
    }

    
    private Map<String, UserInfo> createUserInfoObjects(Map<String, ZohoEmployeeDTO> employeeDTOMap, 
                                                        Map<String, Skill> skillMap) {
        Map<String, UserInfo> userInfoMap = new HashMap<>();
        
        for (Map.Entry<String, ZohoEmployeeDTO> entry : employeeDTOMap.entrySet()) {
            ZohoEmployeeDTO zohoEmployee = entry.getValue();
            UserInfo userInfo = createUserInfo(zohoEmployee);
            
            // Set user skills
            List<UserSkillDetail> userSkillDetails = createUserSkillDetails(zohoEmployee, skillMap, userInfo);
            userInfo.setUserSkillDetails(userSkillDetails);
            
            // Set credentials
            Credential credential = createCredential(zohoEmployee);
            userInfo.setCredential(credential);
            
            userInfoMap.put(entry.getKey(), userInfo);
        }
        
        return userInfoMap;
    }
    
    private UserInfo createUserInfo(ZohoEmployeeDTO zohoEmployee) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName(zohoEmployee.firstName() + " " + zohoEmployee.lastName());
        userInfo.setEmailId(zohoEmployee.emailId());
        userInfo.setPhoneNo(zohoEmployee.phoneNo());
//        log.info(zohoEmployee.emailId());
        userInfo.setDesignation(zohoEmployee.designation());
        userInfo.setRole(zohoEmployee.organizationRole());
        userInfo.setDailyWorkingHours(BigDecimal.valueOf(8.00));
        userInfo.setExpInYears(
            BigDecimal.valueOf(Integer.parseInt(zohoEmployee.experience()))
                       .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
        );
        userInfo.setCreatedBy("system");
        userInfo.setEmployeeType(zohoEmployee.employeeType());
        userInfo.setEmpId(zohoEmployee.employeeId());
        userInfo.setEnabled(true);
        // Placeholder, can be updated later
        
        return userInfo;
    }
    
    private List<UserSkillDetail> createUserSkillDetails(ZohoEmployeeDTO zohoEmployee, 
                                                        Map<String, Skill> skillMap, 
                                                        UserInfo userInfo) {
        List<UserSkillDetail> userSkillDetails = new ArrayList<>();
        
        Stream.of(
            Map.entry(zohoEmployee.primarySkills(), SkillPriority.PRIMARY),
            Map.entry(zohoEmployee.secondarySkills(), SkillPriority.SECONDARY)
        ).filter(entryPair -> entryPair.getKey() != null && !entryPair.getKey().isBlank())
         .flatMap(entryPair -> Arrays.stream(entryPair.getKey().split(","))
                                     .map(String::trim)
                                     .filter(s -> !s.isBlank())
                                     .map(skillName -> Map.entry(skillName.toUpperCase(), entryPair.getValue())))    
         .forEach(entryPair -> {
             String skillName = entryPair.getKey();
             SkillPriority priority = entryPair.getValue();
             Skill skill = skillMap.get(skillName);
             
             if (skill != null) {
                 UserSkillDetail detail = createSkillDetail(skill, userInfo, priority, zohoEmployee);
//                 log.info("User: {}", userInfo.getEmpId());
//                 log.info("Adding skill: {} with level: {}", skillName, detail.getLevel());
                 userSkillDetails.add(detail);
             }
         });
        
        return userSkillDetails;
    }
    
    private UserSkillDetail createSkillDetail(Skill skill, UserInfo userInfo, SkillPriority priority, 
                                            ZohoEmployeeDTO zohoEmployee) {
        UserSkillDetail detail = new UserSkillDetail();
        detail.setSkill(skill);
        detail.setUser(userInfo);
        detail.setPriority(priority);
        detail.setLevel(
            Optional.ofNullable(priority == SkillPriority.PRIMARY ? zohoEmployee.primarySkillLevel() : zohoEmployee.secondarySkillLevel())
                    .filter(level -> !level.isBlank())
                    .orElse("Beginner")
        );
        detail.setExperienceInYears(BigDecimal.ZERO);
        
        return detail;
    }
    
    private Credential createCredential(ZohoEmployeeDTO zohoEmployee) {
        Credential credential = new Credential();
        credential.setUsername(zohoEmployee.employeeId().concat("@cozentus.com"));
//        String password = "C0Z" + String.format("%04d", new Random().nextInt(10_000));
        String password = passwordEncoder.encode("C0Z" + "1234"); // Placeholder password
        credential.setPassword(password);
        credential.setEnabled(true);
        credential.setCreatedBy("system");
        
        Roles role = determineUserRole(zohoEmployee.organizationRole(), zohoEmployee.employeeId());
        credential.setRole(role);
        
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
    
    private void setReportingManagers(Map<String, ZohoEmployeeDTO> employeeDTOMap, 
                                    Map<String, UserInfo> userInfoMap) {
        for (Map.Entry<String, ZohoEmployeeDTO> entry : employeeDTOMap.entrySet()) {
            ZohoEmployeeDTO dto = entry.getValue();
            String employeeZohoId = entry.getKey(); // current user
            UserInfo employee = userInfoMap.get(employeeZohoId);
//            log.info("Processing employee: {}", employee.getName());
            String reportingManagerZohoId = dto.reportingManagerId(); // Zoho ID of manager
            
            if (reportingManagerZohoId != null && userInfoMap.containsKey(reportingManagerZohoId)) {
                // Manager exists in current employee list
                employee.setReportingManager(userInfoMap.get(reportingManagerZohoId));
            } 
            
            else if (reportingManagerZohoId != null) {
                // Manager not in list - create ghost manager
                UserInfo ghostManager = createGhostManager(dto.reportingManagerName(), reportingManagerZohoId);
                userInfoMap.put(reportingManagerZohoId, ghostManager);
                employee.setReportingManager(ghostManager);
            }
        }
    }
    
    private UserInfo createGhostManager(String managerNameRaw, String reportingManagerZohoId) {
        String extractedEmpId = extractEmployeeIdFromString(managerNameRaw);
        String extractedName = extractNameFromString(managerNameRaw);
        
        UserInfo ghostManager = new UserInfo();
        ghostManager.setEmpId(extractedEmpId);
        ghostManager.setName(extractedName);
        ghostManager.setCreatedBy("system");
        ghostManager.setEmailId(extractedEmpId + "@cozentus.com");
        ghostManager.setPhoneNo("0000000000"); // Placeholder, can be updated later
        ghostManager.setDesignation("Reporting Manager");
        ghostManager.setRole("Reporting Manager");
        ghostManager.setDailyWorkingHours(BigDecimal.valueOf(8.00));
        ghostManager.setExpInYears(BigDecimal.ZERO);
        
        Credential ghostCredential = new Credential();
        ghostCredential.setUsername(extractedEmpId + "@cozentus.com");
        String password = passwordEncoder.encode("C0Z" + "1234"); // Placeholder password
        ghostCredential.setPassword(password); // Placeholder password
        ghostCredential.setEnabled(true);
        ghostCredential.setCreatedBy("system");
        ghostCredential.setRole(Roles.RESOURCE); // Default role for ghost managers
        ghostManager.setCredential(ghostCredential);
        
        return ghostManager;
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
    
    private Map<String, UserInfo> getExistingUserMap() {
        return userInfoRepository.findAllWithManagers().stream()
            .collect(Collectors.toMap(UserInfo::getEmpId, Function.identity()));
    }

    private Set<String> normalizeSkillNames(Set<String> skillSet) {
        return skillSet.stream()
            .map(String::trim)
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
    }

    private UserInfo createNewUser(ZohoEmployeeDTO dto, Map<String, Skill> skillMap) {
        UserInfo user = createUserInfo(dto);
        user.setUserSkillDetails(createUserSkillDetails(dto, skillMap, user));
        user.setCredential(createCredential(dto));
        return user;
    }

    private boolean updateExistingUserIfNeeded(UserInfo user, ZohoEmployeeDTO dto) {
        boolean changed = false;

        changed |= updateBasicField(user::getName, user::setName, dto.firstName() + " " + dto.lastName());
        changed |= updateBasicField(user::getEmailId, user::setEmailId, dto.emailId());
        changed |= updateBasicField(user::getPhoneNo, user::setPhoneNo, dto.phoneNo());
        changed |= updateBasicField(user::getDesignation, user::setDesignation, dto.designation());

        BigDecimal expInYears = BigDecimal.valueOf(Integer.parseInt(dto.experience()))
                .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        if (user.getExpInYears() == null || user.getExpInYears().compareTo(expInYears) != 0) {
            user.setExpInYears(expInYears);
            changed = true;
        }

        changed |= updateBasicField(user::getEmployeeType, user::setEmployeeType, dto.employeeType());

        // ✅ Compare Reporting Manager
        String zohoMgrId = dto.reportingManagerId();
        if (zohoMgrId != null) {
            UserInfo currentMgr = user.getReportingManager();
            boolean isDifferent = currentMgr == null || !zohoMgrId.equals(currentMgr.getEmpId());

            if (isDifferent) {
                UserInfo newMgr = findOrCreateReportingManager(zohoMgrId, dto.reportingManagerName());
                user.setReportingManager(newMgr);
                changed = true;
            }
        }

        return changed;
    }


    private boolean updateBasicField(Supplier<String> getter, Consumer<String> setter, String newValue) {
        if (newValue != null && !newValue.equals(getter.get())) {
            setter.accept(newValue);
            return true;
        }
        return false;
    }
    
    private UserInfo findOrCreateReportingManager(String managerEmpId, String rawManagerName) {
        return userInfoRepository.findByEmpId(managerEmpId)
            .orElseGet(() -> {
                UserInfo ghost = createGhostManager(rawManagerName, managerEmpId);
                return userInfoRepository.save(ghost); // persist ghost
            });
    }


}
