package com.almousleck.service;

import com.almousleck.dto.AuthRequest;
import com.almousleck.dto.AuthResponse;
import com.almousleck.dto.RegisterRequest;
import com.almousleck.model.Role;
import com.almousleck.model.User;
import com.almousleck.repository.RoleRepository;
import com.almousleck.repository.UserRepository;
import com.almousleck.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername())
                .isPresent()) {
            throw new RuntimeException("Username is already in use");
        }

        Role role = roleRepository.findByName(request.getRole().toUpperCase());
        if (role == null) {
            role = new Role();
            role.setName(request.getRole().toUpperCase());
            role = roleRepository.save(role);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(role));
        userRepository.save(user);

        return "User registered successfully!";
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        var springUser = new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName()))
                        .toList()
        );

        String token = jwtUtil.generateToken(springUser);
        return new AuthResponse(token);
    }











}
