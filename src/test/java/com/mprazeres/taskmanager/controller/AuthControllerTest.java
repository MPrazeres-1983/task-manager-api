package com.mprazeres.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mprazeres.taskmanager.dto.auth.LoginRequest;
import com.mprazeres.taskmanager.dto.auth.RegisterRequest;
import com.mprazeres.taskmanager.repository.TaskRepository;
import com.mprazeres.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Auth Controller Integration Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    @BeforeEach
    void cleanUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/auth/register — should register and return 201 with token")
    void register_shouldReturn201WithToken() throws Exception {
        RegisterRequest request = new RegisterRequest("Mário", "mario@test.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("mario@test.com"))
                .andExpect(jsonPath("$.name").value("Mário"));
    }

    @Test
    @Order(2)
    @DisplayName("POST /api/auth/register — should return 409 for duplicate email")
    void register_shouldReturn409ForDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest("Mário", "mario@test.com", "password123");

        // First registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // Duplicate
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    @DisplayName("POST /api/auth/register — should return 400 for invalid payload")
    void register_shouldReturn400ForInvalidPayload() throws Exception {
        RegisterRequest request = new RegisterRequest("", "not-an-email", "short");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.properties.errors").exists());
    }

    @Test
    @Order(4)
    @DisplayName("POST /api/auth/login — should return 200 with token for valid credentials")
    void login_shouldReturn200WithToken() throws Exception {
        // Register first
        RegisterRequest reg = new RegisterRequest("Mário", "mario@test.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        // Now login
        LoginRequest login = new LoginRequest("mario@test.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Order(5)
    @DisplayName("POST /api/auth/login — should return 401 for wrong password")
    void login_shouldReturn401ForWrongPassword() throws Exception {
        RegisterRequest reg = new RegisterRequest("Mário", "mario@test.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reg)));

        LoginRequest login = new LoginRequest("mario@test.com", "wrongpassword");
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized());
    }
}
