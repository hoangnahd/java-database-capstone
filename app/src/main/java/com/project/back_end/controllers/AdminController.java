package com.project.back_end.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.back_end.services.BackendService;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final BackendService backendService;

    public AdminController(BackendService backendService) {
        this.backendService = backendService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.getOrDefault("username", "").trim();
        String password = credentials.getOrDefault("password", "").trim();

        if (username.isBlank() || password.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password are required."));
        }

        return backendService.validateAdmin(username, password);
    }
}

