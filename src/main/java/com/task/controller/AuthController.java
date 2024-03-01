package com.task.controller;

import com.task.dto.AuthResponseDTO;
import com.task.dto.LoginDTO;
import com.task.dto.RegisterDTO;
import com.task.model.User;
import com.task.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO registerDTO) {
        AuthResponseDTO response = authService.register(registerDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO loginDto) {
        AuthResponseDTO response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        User user = authService.getUserById(id);
        return user != null ? new ResponseEntity<>(user, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}