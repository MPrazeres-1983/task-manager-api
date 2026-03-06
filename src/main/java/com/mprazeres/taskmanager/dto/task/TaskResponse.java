package com.mprazeres.taskmanager.dto.task;

import com.mprazeres.taskmanager.model.Task;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        Status status,
        Priority priority,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
