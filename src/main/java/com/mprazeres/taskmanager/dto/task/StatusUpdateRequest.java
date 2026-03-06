package com.mprazeres.taskmanager.dto.task;

import com.mprazeres.taskmanager.model.enums.Status;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull(message = "Status is required")
        Status status
) {}
