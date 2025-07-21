package com.cozentus.pms.serviceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cozentus.pms.dto.ZohoApiResponseDTO;
import com.cozentus.pms.dto.ZohoAuthResponseDTO;
import com.cozentus.pms.dto.ZohoEmployeeDTO;
import com.cozentus.pms.services.ZohoService;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class ZohoServiceImpl implements ZohoService {
	@Value("${zoho.refresh.token}")
	private String refreshToken;
	@Value("${zoho.client.id}")
	private String clientId;
	@Value("${zoho.client.secret}")
	private String clientSecret;
	
	
	private final RestTemplate restTemplate;
	public ZohoServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public ZohoAuthResponseDTO getAccessToken() {
		String url = "https://accounts.zoho.in/oauth/v2/token";


	    UriComponentsBuilder uriComponentsBuilderForQueryParam = UriComponentsBuilder.fromUriString(url)
	        .queryParam("refresh_token", refreshToken)
	        .queryParam("client_id", clientId)
	        .queryParam("client_secret", clientSecret)
	        .queryParam("grant_type", "refresh_token");

	    // Set headers
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Bearer your_token");
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	    HttpEntity<String> httpEntity = new HttpEntity<>(headers);

	    // Send request
	    ResponseEntity<ZohoAuthResponseDTO> response = restTemplate.exchange(
	        uriComponentsBuilderForQueryParam.toUriString(),              // Full URL with query params
	        HttpMethod.POST,
	        httpEntity,
	        ZohoAuthResponseDTO.class
	    );
//	    log.info("Response from Zoho: {}", response.getBody());
	    return response.getBody();
	}
	
	
	public Pair<Map<String, ZohoEmployeeDTO>, Set<String>> fetchDataAllEmployeeDataFromZoho() {
	    ZohoAuthResponseDTO zohoAuthResponse = getAccessToken();
	    String url = "https://people.zoho.in/people/api/forms/employee/getRecords";

	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Authorization", "Zoho-oauthtoken " + zohoAuthResponse.access_token());
	    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

	    HttpEntity<String> httpEntity = new HttpEntity<>(headers);

	    ResponseEntity<ZohoApiResponseDTO> response = restTemplate.exchange(
	        url,
	        HttpMethod.GET,
	        httpEntity,
	        ZohoApiResponseDTO.class
	    );

	    ZohoApiResponseDTO body = response.getBody();
	    if (body == null || body.response() == null || body.response().result() == null) {
	        throw new RuntimeException("Empty or invalid Zoho response.");
	    }

	    List<Map<String, List<ZohoEmployeeDTO>>> resultList = body.response().result();

	    Map<String, ZohoEmployeeDTO> employeeMap = new HashMap<>();
	    Set<String> allSkills = new HashSet<>();

	    for (Map<String, List<ZohoEmployeeDTO>> resultMap : resultList) {
	        for (Map.Entry<String, List<ZohoEmployeeDTO>> entry : resultMap.entrySet()) {
	            String employeeId = entry.getKey();
	            List<ZohoEmployeeDTO> employees = entry.getValue();

	            if (employees != null && !employees.isEmpty()) {
	                ZohoEmployeeDTO emp = employees.get(0); // only one per ID
	                employeeMap.put(employeeId, emp);

	                Stream.of(emp.primarySkills(), emp.secondarySkills())
	                    .filter(s -> s != null && !s.isBlank())
	                    .flatMap(s -> Arrays.stream(s.split(",")))
	                    .map(String::trim)
	                    .filter(s -> !s.isBlank())
	                    .map(String::toUpperCase)
	                    .forEach(allSkills::add);
	            }
	        }
	    }

	    return Pair.of(employeeMap, allSkills);
	}

	

}
