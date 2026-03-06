package com.mprazeres.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mprazeres.taskmanager.dto.auth.RegisterRequest;
import com.mprazeres.taskmanager.dto.task.StatusUpdateRequest;
import com.mprazeres.taskmanager.dto.task.TaskRequest;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import com.mprazeres.taskmanager.repository.TaskRepository;
import com.mprazeres.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Task Controller Integration Tests")
class TaskControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private TaskRepository taskRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        token = registerAndGetToken("mario@test.com", "password123");
    }

    private String registerAndGetToken(String email, String password) throws Exception {
        RegisterRequest reg = new RegisterRequest("Mario", email, password);
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reg)))
                .andReturn();
        String body = result.getResponse().getContentAsString();
        return objectMapper.readTree(body).get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("POST /api/tasks - should create task and return 201")
    void createTask_shouldReturn201() throws Exception {
        TaskRequest request = new TaskRequest(
                "Fix CI pipeline", "Update GitHub Actions", Status.TODO,
                Priority.HIGH, LocalDate.now().plusDays(5));

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Fix CI pipeline"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/tasks - should return 200 with paginated list")
    void getTasks_shouldReturn200WithPage() throws Exception {
        TaskRequest request = new TaskRequest("Task 1", null, null, null, null);
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/tasks - should return 401 without token")
    void getTasks_shouldReturn401WithoutToken() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(4)
    @DisplayName("PATCH /api/tasks/{id}/status - should update status")
    void updateStatus_shouldUpdateAndReturn200() throws Exception {
        TaskRequest create = new TaskRequest("Task", null, Status.TODO, null, null);
        MvcResult created = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn();
        Long taskId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        StatusUpdateRequest statusReq = new StatusUpdateRequest(Status.DONE);
        mockMvc.perform(patch("/api/tasks/" + taskId + "/status")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    @Test
    @Order(5)
    @DisplayName("DELETE /api/tasks/{id} - should delete and return 204")
    void deleteTask_shouldReturn204() throws Exception {
        TaskRequest create = new TaskRequest("To delete", null, null, null, null);
        MvcResult created = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn();
        Long taskId = objectMapper.readTree(created.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/tasks - should not return tasks from other users")
    void getTasks_shouldIsolateByUser() throws Exception {
        TaskRequest req = new TaskRequest("Private task", null, null, null, null);
        mockMvc.perform(post("/api/tasks")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        String otherToken = registerAndGetToken("other@test.com", "password123");
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @Order(7)
    @DisplayName("POST /api/tasks - should return 400 for empty title")
    void createTask_shouldReturn400ForEmptyTitle() throws Exception {
        String payload = "{\"title\": \"\", \"status\": \"TODO\"}";
        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(8)
    @DisplayName("GET /api/tasks/{id} - should return 404 for unknown task")
    void getTask_shouldReturn404ForUnknownTask() throws Exception {
        mockMvc.perform(get("/api/tasks/9999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
