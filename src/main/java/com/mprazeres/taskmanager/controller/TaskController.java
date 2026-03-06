package com.mprazeres.taskmanager.controller;

import com.mprazeres.taskmanager.dto.task.StatusUpdateRequest;
import com.mprazeres.taskmanager.dto.task.TaskRequest;
import com.mprazeres.taskmanager.dto.task.TaskResponse;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import com.mprazeres.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Tasks", description = "Task CRUD operations with filtering and pagination")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "List tasks", description = "Returns paginated tasks. Filter by status and/or priority.")
    public ResponseEntity<Page<TaskResponse>> getTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(
                taskService.getTasks(userDetails.getUsername(), status, priority, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(userDetails.getUsername(), id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task")
    public ResponseEntity<TaskResponse> createTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task (full update)")
    public ResponseEntity<TaskResponse> updateTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(userDetails.getUsername(), id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status only")
    public ResponseEntity<TaskResponse> updateStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        return ResponseEntity.ok(
                taskService.updateStatus(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task")
    public ResponseEntity<Void> deleteTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        taskService.deleteTask(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
