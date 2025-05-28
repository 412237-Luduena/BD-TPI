package com.example.todolist.controller;

import com.example.todolist.dto.UserResponse;
import com.example.todolist.dto.auth.JwtResponse;
import com.example.todolist.dto.auth.LoginRequest;
import com.example.todolist.dto.auth.RegisterRequest;
import com.example.todolist.model.User;
import com.example.todolist.security.jwt.JwtTokenProvider;
import com.example.todolist.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthServiceImpl authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        User user = authService.registerUser(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtTokenProvider.generateToken(userDetails);
        User user = authService
                .authenticate(request.getUsername(), request.getPassword())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));
        return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getEmail()));
    }
}
