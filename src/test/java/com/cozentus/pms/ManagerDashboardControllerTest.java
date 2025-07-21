package com.cozentus.pms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.cozentus.pms.dto.KeyPerformanceIndicatorsDTO;
import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.dto.ProjectDashboardDTO;
import com.cozentus.pms.dto.ResourceBasicDTO;
import com.cozentus.pms.dto.SkillCountDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Manager Dashboard Controller Integration Tests")
public class ManagerDashboardControllerTest {

    @LocalServerPort
    private int port;

    private String token;

    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // Login and get token
        LoginDTO loginDTO = new LoginDTO("CZ0286@cozentus.com", "C0Z1234"); // adjust creds as needed
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDTO> request = new HttpEntity<>(loginDTO, headers);

        ResponseEntity<LoginResponseDTO> response = restTemplate.postForEntity(baseUrl + "/public/login", request, LoginResponseDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        token = response.getBody().token();
        assertNotNull(token);
    }

    private HttpHeaders withAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    @Test
    @DisplayName("GET /manager-dashboard/kpi - Should return KPI data")
    void testGetKPI() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<Object> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/kpi",
                HttpMethod.GET,
                request,
                Object.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /manager-dashboard/skill-counts - Should return skill counts")
    void testGetSkillCounts() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/skill-counts",
                HttpMethod.GET,
                request,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /manager-dashboard/skill-resource-details - Should return resource details for skill")
    void testGetSkillResourceDetails() {
        HttpHeaders headers = withAuthHeaders();
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/manager-dashboard/skill-resource-details")
                .queryParam("skillName", "Java")
                .queryParam("level", "Intermediate");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                request,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /manager-dashboard/projects - Should return project dashboard data")
    void testGetProjects() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/projects",
                HttpMethod.GET,
                request,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /manager-dashboard/project-count - Should return project count for PMs")
    void testGetProjectCount() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/project-count",
                HttpMethod.GET,
                request,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("GET /manager-dashboard/projects-by-pm/{id} - Should return projects under a PM")
    void testGetProjectsByPM() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());
        String projectManagerEmpId = "EMP001"; // ensure this test data exists

        ResponseEntity<List> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/projects-by-pm/" + projectManagerEmpId,
                HttpMethod.GET,
                request,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    @DisplayName("GET /manager-dashboard/kpi - Should return correct KPI metrics")
    void testKPIValuesCorrectness() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<KeyPerformanceIndicatorsDTO> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/kpi",
                HttpMethod.GET,
                request,
                KeyPerformanceIndicatorsDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        KeyPerformanceIndicatorsDTO kpi = response.getBody();
        assertNotNull(kpi);

        // Assert known values based on test data
        assertEquals(1, kpi.billedNotBilled());
        assertEquals(1, kpi.nonUtilizedResources());
        assertEquals(2, kpi.totalResources());
        assertEquals(50.0, kpi.customerPlannedUtilization()); // assert with tolerance
    }
    
    @Test
    @DisplayName("GET /manager-dashboard/skill-counts - Should match expected skill stats")
    void testSkillStatsAccuracy() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List<SkillCountDTO>> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/skill-counts",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<SkillCountDTO> skillCounts = response.getBody();

        SkillCountDTO javaStat = skillCounts.stream()
            .filter(s -> s.name().equalsIgnoreCase("Java"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Java skill not found"));

        assertEquals(2, javaStat.totalCount());
    }

    
    @Test
    @DisplayName("GET /manager-dashboard/projects - Validate known project data")
    void testProjectDashboardAccuracy() {
        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List<ProjectDashboardDTO>> response = restTemplate.exchange(
                baseUrl + "/manager-dashboard/projects",
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<ProjectDashboardDTO> projects = response.getBody();

        ProjectDashboardDTO ecommerce = projects.stream()
            .filter(p -> p.getCode().equals("PROJ001"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("E-Commerce project not found"));

        assertEquals("E-Commerce Platform", ecommerce.getName());
    }
    
    @Test
    @DisplayName("Skill Resource Details - Verify employee name and level")
    void testSkillResourceExactMatch() {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/manager-dashboard/skill-resource-details")
                .queryParam("skillName", "Java")
                .queryParam("level", "Intermediate");

        HttpEntity<Void> request = new HttpEntity<>(withAuthHeaders());

        ResponseEntity<List<ResourceBasicDTO>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResourceBasicDTO user = response.getBody().stream()
            .filter(r -> r.employeeId().equals("EMP001"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("EMP001 not returned"));

        assertEquals("John Doe", user.name());
    }



}
