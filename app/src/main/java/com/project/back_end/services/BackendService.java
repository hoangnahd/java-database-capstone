package com.project.back_end.services;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.back_end.models.Admin;
import com.project.back_end.repo.AdminRepository;

@Service
public class BackendService {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;

    public BackendService(TokenService tokenService, AdminRepository adminRepository) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
    }

    public ResponseEntity<Map<String, Object>> validateAdmin(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid admin credentials."));
        }

        if (!password.equals(admin.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid admin credentials."));
        }

        String token = tokenService.generateToken(username);
        return ResponseEntity.ok(Map.of("message", "Login successful", "token", token));
    }
}
