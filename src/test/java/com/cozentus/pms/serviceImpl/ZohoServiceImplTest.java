//
//package com.cozentus.pms.serviceImpl;
//
//import com.cozentus.pms.dto.ZohoApiResponseDTO;
//import com.cozentus.pms.dto.ZohoAuthResponseDTO;
//import com.cozentus.pms.dto.ZohoEmployeeDTO;
//import org.apache.commons.lang3.tuple.Pair;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class ZohoServiceImplTest {
//
//    @MockBean
//    private RestTemplate restTemplate;
//
//    private ZohoServiceImpl zohoService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        zohoService = new ZohoServiceImpl(restTemplate);
//    }
//
//    @Test
//    public void testGetAccessToken() {
//        ZohoAuthResponseDTO mockResponse = new ZohoAuthResponseDTO("token", "scope", "domain", "type", 3600);
//        ResponseEntity<ZohoAuthResponseDTO> responseEntity = ResponseEntity.ok(mockResponse);
//
//        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoAuthResponseDTO.class)))
//                .thenReturn(responseEntity);
//
//        ZohoAuthResponseDTO result = zohoService.getAccessToken();
//
//        assertEquals(mockResponse, result);
//        verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoAuthResponseDTO.class));
//    }
//
//    @Test
//    public void testFetchDataAllEmployeeDataFromZoho() {
//        ZohoAuthResponseDTO mockAuthResponse = new ZohoAuthResponseDTO("token", "scope", "domain", "type", 3600);
//        ZohoEmployeeDTO mockEmployee = new ZohoEmployeeDTO("id", "first", "last", "email", "phone", "designation", "role", "experience", "type", "managerId", "managerName", "primarySkills", "secondarySkills", "primarySkillLevel", "secondarySkillLevel", "organizationRole", "managerEmail");
//        ZohoApiResponseDTO mockApiResponse = new ZohoApiResponseDTO(Collections.singletonList(Collections.singletonMap("id", Collections.singletonList(mockEmployee))));
//        ResponseEntity<ZohoApiResponseDTO> responseEntity = ResponseEntity.ok(mockApiResponse);
//
//        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoAuthResponseDTO.class)))
//                .thenReturn(ResponseEntity.ok(mockAuthResponse));
//        when(restTemplate.exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoApiResponseDTO.class)))
//                .thenReturn(responseEntity);
//
//        Pair<Map<String, ZohoEmployeeDTO>, Set<String>> result = zohoService.fetchDataAllEmployeeDataFromZoho();
//
//        assertEquals(1, result.getLeft().size());
//        assertEquals(mockEmployee, result.getLeft().get("id"));
//        assertTrue(result.getRight().contains("PRIMARYSKILLS"));
//        assertTrue(result.getRight().contains("SECONDARYSKILLS"));
//
//        verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoAuthResponseDTO.class));
//        verify(restTemplate, times(1)).exchange(anyString(), any(), any(HttpEntity.class), eq(ZohoApiResponseDTO.class));
//    }
//}