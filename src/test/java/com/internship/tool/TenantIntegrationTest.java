package com.internship.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.internship.tool.dto.LoginRequest;
import com.internship.tool.dto.TenantDTO;
import com.internship.tool.entity.User;
import com.internship.tool.repository.TenantRepository;
import com.internship.tool.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TenantIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        tenantRepository.deleteAll();
        userRepository.deleteAll();

        // Create Admin User
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        userRepository.save(admin);

        // Create Regular User
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user123"));
        user.setRole("USER");
        userRepository.save(user);

        // Authenticate Admin
        LoginRequest adminAuth = new LoginRequest();
        adminAuth.setUsername("admin");
        adminAuth.setPassword("admin123");
        MvcResult adminResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminAuth)))
                .andReturn();
        // Assuming response is an object with token: {"token": "..."}
        String adminResponseStr = adminResult.getResponse().getContentAsString();
        adminToken = objectMapper.readTree(adminResponseStr).get("token").asText();

        // Authenticate User
        LoginRequest userAuth = new LoginRequest();
        userAuth.setUsername("user");
        userAuth.setPassword("user123");
        MvcResult userResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAuth)))
                .andReturn();
        String userResponseStr = userResult.getResponse().getContentAsString();
        userToken = objectMapper.readTree(userResponseStr).get("token").asText();
    }

    @Test
    void testAdminCanCreateTenant() throws Exception {
        TenantDTO tenantDTO = TenantDTO.builder()
                .name("Integration Tenant")
                .description("Test Description")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/tenants")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Integration Tenant"));
    }

    @Test
    void testUserCannotCreateTenant_ReturnsForbidden() throws Exception {
        TenantDTO tenantDTO = TenantDTO.builder()
                .name("Integration Tenant")
                .description("Test Description")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/tenants")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUserCanGetAllTenants() throws Exception {
        // Admin creates one first
        TenantDTO tenantDTO = TenantDTO.builder()
                .name("Tenant A")
                .description("Description")
                .status("ACTIVE")
                .build();

        mockMvc.perform(post("/api/tenants")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tenantDTO)));

        // User gets all
        mockMvc.perform(get("/api/tenants")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Tenant A"));
    }
}
