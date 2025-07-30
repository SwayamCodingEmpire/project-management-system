//package com.cozentus.pms.controllers;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.context.WebApplicationContext;
//
//import com.cozentus.pms.dto.ClientDTO;
//import com.cozentus.pms.dto.LoginDTO;
//import com.cozentus.pms.dto.LoginResponseDTO;
//import com.cozentus.pms.services.ClientService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class ClientControllerTest {
//
//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    private MockMvc mockMvc;
//
//    // Only mock the service called by controller methods
//    @MockBean
//    private ClientService clientService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private String authToken;
//    private final String LOGIN_URL = "/public/login";
//    private final String CLIENTS_URL = "/clients/all";
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // Setup MockMvc with full Spring context
//        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
//        
//        // Perform REAL login request through the actual authentication flow
//        // This will use your real database and authentication service
//        LoginDTO loginRequest = new LoginDTO("niharika.dash@cozentus.com", "C0Z1234");
//        
//        MvcResult loginResult = mockMvc.perform(post(LOGIN_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Extract token from the REAL login response
//        String loginResponseJson = loginResult.getResponse().getContentAsString();
//        LoginResponseDTO response = objectMapper.readValue(loginResponseJson, LoginResponseDTO.class);
//        this.authToken = response.token();
//        
//        System.out.println("Retrieved REAL auth token: " + this.authToken);
//    }
//
//    @Test
//    void getAllClients_WithValidToken_ShouldReturnClientsList() throws Exception {
//        // ONLY mock the service method called by the controller
//        List<ClientDTO> expectedClients = Arrays.asList(
//            new ClientDTO(1, "Client A", "Legal Entity A", "Business Unit Alpha"),
//            new ClientDTO(2, "Client B", "Legal Entity B", "Business Unit Beta"),
//            new ClientDTO(3, "Client C", "Legal Entity C", "Business Unit Gamma")
//        );
//
//        when(clientService.getAllClients()).thenReturn(expectedClients);
//
//        // Act & Assert - Uses REAL authentication token
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.length()").value(3))
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].name").value("Client A"))
//                .andExpect(jsonPath("$[0].legalEntity").value("Legal Entity A"))
//                .andExpect(jsonPath("$[0].businessUnit").value("Business Unit Alpha"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].name").value("Client B"))
//                .andExpect(jsonPath("$[1].legalEntity").value("Legal Entity B"))
//                .andExpect(jsonPath("$[1].businessUnit").value("Business Unit Beta"))
//                .andExpect(jsonPath("$[2].id").value(3))
//                .andExpect(jsonPath("$[2].name").value("Client C"))
//                .andExpect(jsonPath("$[2].legalEntity").value("Legal Entity C"))
//                .andExpect(jsonPath("$[2].businessUnit").value("Business Unit Gamma"));
//    }
//
//    @Test
//    void getAllClients_WithValidToken_EmptyList_ShouldReturnEmptyArray() throws Exception {
//        // Mock service to return empty list
//        when(clientService.getAllClients()).thenReturn(Arrays.asList());
//
//        // Act & Assert - Uses REAL authentication
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.length()").value(0));
//    }
//
//    @Test
//    void getAllClients_WithoutToken_ShouldReturnUnauthorized() throws Exception {
//        // No token - should fail with REAL security
//        mockMvc.perform(get(CLIENTS_URL)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void getAllClients_WithInvalidToken_ShouldReturnUnauthorized() throws Exception {
//        // Invalid token - should fail with REAL JWT validation
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer invalid.token.here")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void getAllClients_WithMalformedAuthHeader_ShouldReturnUnauthorized() throws Exception {
//        // Malformed header - should fail with REAL security
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "InvalidFormat " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void getAllClients_ServiceThrowsException_ShouldReturnInternalServerError() throws Exception {
//        // Mock service to throw exception - only the service method is mocked
//        when(clientService.getAllClients()).thenThrow(new RuntimeException("Database connection failed"));
//
//        // Uses REAL authentication but mocked service
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    void getAllClients_WithDifferentUserLogin_ShouldWork() throws Exception {
//        // Login with REAL different user credentials
//        LoginDTO userLoginRequest = new LoginDTO("admin@cozentus.com", "ADMIN123"); // Use real test user
//        
//        MvcResult userLoginResult = mockMvc.perform(post(LOGIN_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(userLoginRequest)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String userLoginResponseJson = userLoginResult.getResponse().getContentAsString();
//        LoginResponseDTO userResponse = objectMapper.readValue(userLoginResponseJson, LoginResponseDTO.class);
//        String userToken = userResponse.token();
//
//        // Mock ONLY the service method
//        List<ClientDTO> expectedClients = Arrays.asList(
//            new ClientDTO(1, "Admin Client", "Admin Legal Entity", "Admin Business Unit")
//        );
//        when(clientService.getAllClients()).thenReturn(expectedClients);
//
//        // Test with the REAL user token
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + userToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].name").value("Admin Client"))
//                .andExpect(jsonPath("$[0].legalEntity").value("Admin Legal Entity"))
//                .andExpect(jsonPath("$[0].businessUnit").value("Admin Business Unit"));
//    }
//
//    @Test
//    void completeLoginToClientAccessFlow_IntegrationTest() throws Exception {
//        // Step 1: REAL login with actual authentication service
//        LoginDTO integrationLoginRequest = new LoginDTO("test@cozentus.com", "TEST123"); // Use real test user
//        
//        MvcResult integrationLoginResult = mockMvc.perform(post(LOGIN_URL)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(integrationLoginRequest)))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Step 2: Extract token from REAL authentication response
//        String integrationResponseJson = integrationLoginResult.getResponse().getContentAsString();
//        LoginResponseDTO integrationResponse = objectMapper.readValue(integrationResponseJson, LoginResponseDTO.class);
//        String integrationToken = integrationResponse.token();
//
//        // Verify the REAL login response contains expected fields
//        assertThat(integrationResponse.token()).isNotNull();
//        assertThat(integrationResponse.role()).isNotNull();
//        assertThat(integrationResponse.name()).isNotNull();
//        assertThat(integrationResponse.empId()).isNotNull();
//
//        // Step 3: Mock ONLY the service method and use REAL token
//        List<ClientDTO> clients = Arrays.asList(
//            new ClientDTO(100, "Integration Client", "Integration Legal Entity", "Integration Business Unit")
//        );
//        when(clientService.getAllClients()).thenReturn(clients);
//
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + integrationToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Integration Client"))
//                .andExpect(jsonPath("$[0].legalEntity").value("Integration Legal Entity"))
//                .andExpect(jsonPath("$[0].businessUnit").value("Integration Business Unit"));
//    }
//
//    @Test
//    void testMultipleClientScenarios_RealAuthMockedService() throws Exception {
//        // Test 1: Large list of clients - Mock ONLY service method
//        List<ClientDTO> largeClientList = Arrays.asList(
//            new ClientDTO(1, "Client 1", "Legal Entity 1", "Business Unit 1"),
//            new ClientDTO(2, "Client 2", "Legal Entity 2", "Business Unit 2"),
//            new ClientDTO(3, "Client 3", "Legal Entity 3", "Business Unit 3"),
//            new ClientDTO(4, "Client 4", "Legal Entity 4", "Business Unit 4"),
//            new ClientDTO(5, "Client 5", "Legal Entity 5", "Business Unit 5")
//        );
//        
//        when(clientService.getAllClients()).thenReturn(largeClientList);
//
//        // Use REAL authentication token
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(5))
//                .andExpect(jsonPath("$[4].name").value("Client 5"));
//
//        // Test 2: Single client - Mock ONLY service method
//        List<ClientDTO> singleClient = Arrays.asList(
//            new ClientDTO(99, "Single Client", "Single Legal Entity", "Single Business Unit")
//        );
//        
//        when(clientService.getAllClients()).thenReturn(singleClient);
//
//        // Use REAL authentication token
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + authToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(1))
//                .andExpect(jsonPath("$[0].id").value(99))
//                .andExpect(jsonPath("$[0].name").value("Single Client"));
//    }
//
//    @Test
//    void testTokenValidation_RealJWTValidation() throws Exception {
//        // Test that expired or invalid tokens fail with REAL JWT validation
//        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0IiwiZXhwIjoxfQ.expired";
//        
//        mockMvc.perform(get(CLIENTS_URL)
//                .header("Authorization", "Bearer " + expiredToken)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//    }
//}