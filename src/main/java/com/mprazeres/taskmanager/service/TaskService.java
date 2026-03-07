package com.mprazeres.taskmanager.service;

import com.mprazeres.taskmanager.dto.task.StatusUpdateRequest;
import com.mprazeres.taskmanager.dto.task.TaskRequest;
import com.mprazeres.taskmanager.dto.task.TaskResponse;
import com.mprazeres.taskmanager.exception.ResourceNotFoundException;
import com.mprazeres.taskmanager.model.Task;
import com.mprazeres.taskmanager.model.User;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import com.mprazeres.taskmanager.repository.TaskRepository;
import com.mprazeres.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public Page<TaskResponse> getTasks(
            String email,
            Status status,
            Priority priority,
            Pageable pageable) {

        User user = findUserByEmail(email);

        Page<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByUserIdAndStatusAndPriority(
                    user.getId(), status, priority, pageable);
        } else if (status != null) {
            tasks = taskRepository.findByUserIdAndStatus(user.getId(), status, pageable);
        } else if (priority != null) {
            tasks = taskRepository.findByUserIdAndPriority(user.getId(), priority, pageable);
        } else {
            tasks = taskRepository.findByUserId(user.getId(), pageable);
        }

        return tasks.map(TaskResponse::from);
    }

    public TaskResponse getTask(String email, Long taskId) {
        User user = findUserByEmail(email);
        Task task = findTaskByIdAndUser(taskId, user.getId());
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse createTask(String email, TaskRequest request) {
        User user = findUserByEmail(email);

        Task task = Task.builder()
                .title(request.title())
                .description(request.description())
                .status(request.status() != null ? request.status() : Status.TODO)
                .priority(request.priority() != null ? request.priority() : Priority.MEDIUM)
                .dueDate(request.dueDate())
                .user(user)
                .build();

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTask(String email, Long taskId, TaskRequest request) {
        User user = findUserByEmail(email);
        Task task = findTaskByIdAndUser(taskId, user.getId());

        task.setTitle(request.title());
        task.setDescription(request.description());
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        task.setDueDate(request.dueDate());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateStatus(String email, Long taskId, StatusUpdateRequest request) {
        User user = findUserByEmail(email);
        Task task = findTaskByIdAndUser(taskId, user.getId());
        task.setStatus(request.status());
        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(String email, Long taskId) {
        User user = findUserByEmail(email);
        Task task = findTaskByIdAndUser(taskId, user.getId());
        taskRepository.delete(task);
    }

    private User findUserByEmail(String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        return userRepository.findByEmailIgnoreCase(normalizedEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + normalizedEmail));
    }

    private Task findTaskByIdAndUser(Long taskId, Long userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found: " + taskId));
    }
}
