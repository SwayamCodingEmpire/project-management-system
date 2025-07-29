//Here is a JUnit 5 test class for the `ZohoScheduleUpsert` service class:
//
//```java
//package com.cozentus.pms.serviceImpl;
//
//import com.cozentus.pms.dto.ZohoEmployeeDTO;
//import com.cozentus.pms.entites.Credential;
//import com.cozentus.pms.entites.UserInfo;
//import com.cozentus.pms.repositories.CredentialRepository;
//import com.cozentus.pms.repositories.UserInfoRepository;
//import com.cozentus.pms.services.GptSkillNormalizerService;
//import com.cozentus.pms.services.ZohoService;
//import org.apache.commons.lang3.tuple.Pair;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//import java.util.*;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class ZohoScheduleUpsertTest {
//
//    @Mock
//    private ZohoService zohoService;
//
//    @Mock
//    private UserInfoRepository userInfoRepository;
//
//    @Mock
//    private CredentialRepository credentialRepository;
//
//    @Mock
//    private BCryptPasswordEncoder passwordEncoder;
//
//    @Mock
//    private GptSkillNormalizerService gptSkillNormalizerService;
//
//    @InjectMocks
//    private ZohoScheduleUpsert zohoScheduleUpsert;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testSyncResourcesWithDB() {
//        // Prepare test data
//        ZohoEmployeeDTO zohoEmployeeDTO = new ZohoEmployeeDTO("1", "John", "Doe", "john.doe@example.com", "1234567890", "Software Engineer", "Engineer", "5", "Full-time", "2", "Jane Doe", "Java", "Python", "Intermediate", "Beginner", "Engineer", "jane.doe@example.com");
//        Map<String, ZohoEmployeeDTO> zohoMap = new HashMap<>();
//        zohoMap.put("1", zohoEmployeeDTO);
//        Set<String> empIds = new HashSet<>();
//        empIds.add("1");
//        Pair<Map<String, ZohoEmployeeDTO>, Set<String>> zohoData = Pair.of(zohoMap, empIds);
//
//        UserInfo userInfo = new UserInfo();
//        userInfo.setEmpId("1");
//        List<UserInfo> userInfoList = new ArrayList<>();
//        userInfoList.add(userInfo);
//
//        Credential credential = new Credential();
//        credential.setUsername("john.doe@example.com");
//
//        // Mock interactions
//        when(zohoService.fetchDataAllEmployeeDataFromZoho()).thenReturn(zohoData);
//        when(userInfoRepository.findAll()).thenReturn(userInfoList);
//        when(credentialRepository.findByUsername(anyString())).thenReturn(Optional.of(credential));
//
//        // Call the method under test
//        zohoScheduleUpsert.syncResourcesWithDB();
//
//        // Verify interactions
//        verify(zohoService, times(1)).fetchDataAllEmployeeDataFromZoho();
//        verify(userInfoRepository, times(1)).findAll();
//        verify(credentialRepository, times(1)).findByUsername(anyString());
//        verify(userInfoRepository, atLeastOnce()).saveAllAndFlush(anyList());
//    }
//}
//```
//
//This test class uses the `@SpringBootTest` annotation to indicate that it is a Spring Boot test and should have access to all Spring Boot features and infrastructure. The `@MockBean` annotation is used to create mock objects for the dependencies of the `ZohoScheduleUpsert` service. These mock objects are then injected into the service using the `@InjectMocks` annotation.
//
//The `testSyncResourcesWithDB` method is a test case for the `syncResourcesWithDB` method of the `ZohoScheduleUpsert` service. It prepares some test data, sets up the expected behavior of the mock objects, calls the method under test, and then verifies that the expected interactions with the mock objects occurred.
//
//This test case covers the scenario where the `syncResourcesWithDB` method is called when there is one employee in the Zoho data and the same employee already exists in the database. It does not cover all possible scenarios, but it should give you a good starting point for writing more test cases.