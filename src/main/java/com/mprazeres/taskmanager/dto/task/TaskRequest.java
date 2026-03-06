package com.mprazeres.taskmanager.dto.task;

import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
        String title,

        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        Status status,

        Priority priority,

        @FutureOrPresent(message = "Due date must be today or in the future")
        LocalDate dueDate
) {}
