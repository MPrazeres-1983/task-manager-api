package com.mprazeres.taskmanager.dto.auth;

public record AuthResponse(
        String token,
        String email,
        String name
) {}
