package com.mprazeres.taskmanager.service;

import com.mprazeres.taskmanager.dto.task.StatusUpdateRequest;
import com.mprazeres.taskmanager.dto.task.TaskRequest;
import com.mprazeres.taskmanager.exception.ResourceNotFoundException;
import com.mprazeres.taskmanager.model.Task;
import com.mprazeres.taskmanager.model.User;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import com.mprazeres.taskmanager.repository.TaskRepository;
import com.mprazeres.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).name("Mário").email("mario@test.com").build();
        task = Task.builder()
                .id(1L).title("Test Task").description("Description")
                .status(Status.TODO).priority(Priority.MEDIUM)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .user(user).build();
    }

    @Test
    @DisplayName("getTasks — should return paginated tasks for user")
    void getTasks_shouldReturnPagedTasks() {
        PageRequest pageable = PageRequest.of(0, 20);
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserId(1L, pageable))
                .thenReturn(new PageImpl<>(List.of(task)));

        Page<var> result = taskService.getTasks("mario@test.com", null, null, pageable);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Test Task");
    }

    @Test
    @DisplayName("createTask — should save and return task")
    void createTask_shouldSaveTask() {
        TaskRequest request = new TaskRequest(
                "New Task", "Description", Status.TODO, Priority.HIGH,
                LocalDate.now().plusDays(3));

        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(2L);
            t.setCreatedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return t;
        });

        var response = taskService.createTask("mario@test.com", request);

        assertThat(response.title()).isEqualTo("New Task");
        assertThat(response.priority()).isEqualTo(Priority.HIGH);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask — should default to TODO and MEDIUM when not specified")
    void createTask_shouldApplyDefaults() {
        TaskRequest request = new TaskRequest("Task", null, null, null, null);

        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> {
            Task t = i.getArgument(0);
            t.setId(3L);
            t.setCreatedAt(LocalDateTime.now());
            t.setUpdatedAt(LocalDateTime.now());
            return t;
        });

        var response = taskService.createTask("mario@test.com", request);

        assertThat(response.status()).isEqualTo(Status.TODO);
        assertThat(response.priority()).isEqualTo(Priority.MEDIUM);
    }

    @Test
    @DisplayName("getTask — should throw ResourceNotFoundException for unknown task")
    void getTask_shouldThrowForUnknownTask() {
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTask("mario@test.com", 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("updateStatus — should change status and return updated task")
    void updateStatus_shouldChangeStatus() {
        StatusUpdateRequest statusRequest = new StatusUpdateRequest(Status.DONE);
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        var response = taskService.updateStatus("mario@test.com", 1L, statusRequest);

        assertThat(response.status()).isEqualTo(Status.DONE);
    }

    @Test
    @DisplayName("deleteTask — should delete and not throw for existing task")
    void deleteTask_shouldDeleteTask() {
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));

        taskService.deleteTask("mario@test.com", 1L);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("getTasks — should filter by status when provided")
    void getTasks_shouldFilterByStatus() {
        PageRequest pageable = PageRequest.of(0, 20);
        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        when(taskRepository.findByUserIdAndStatus(1L, Status.TODO, pageable))
                .thenReturn(new PageImpl<>(List.of(task)));

        var result = taskService.getTasks("mario@test.com", Status.TODO, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        verify(taskRepository).findByUserIdAndStatus(1L, Status.TODO, pageable);
    }
}
