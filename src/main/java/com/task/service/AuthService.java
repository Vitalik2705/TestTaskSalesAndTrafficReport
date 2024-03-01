package com.task.service;

import com.task.dto.AuthResponseDTO;
import com.task.dto.LoginDTO;
import com.task.dto.RegisterDTO;
import com.task.model.User;
import com.task.repository.UserRepository;
import com.task.security.JwtTokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Cacheable(value = "loginCache", key = "#loginDto")
    public AuthResponseDTO login(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
        ));

        var user = userRepository.findUserByUsername(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }

    @Cacheable(value = "registerCache", key = "#registerDto")
    public AuthResponseDTO register(RegisterDTO registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            throw new RuntimeException("Email already in use");
        }

        User user = mapRegisterDtoToUser(registerDto);

        String encodedPassword = passwordEncoder.encode(registerDto.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .userId(user.getId())
                .build();
    }

    @Cacheable(value = "getUserByIdrCache", key = "#id")
    public User getUserById(String id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

    private User mapRegisterDtoToUser(RegisterDTO registerDto) {
        return User.builder()
                .username(registerDto.getUsername())
                .password(registerDto.getPassword())
                .roles(registerDto.getRoles())
                .build();
    }
}

