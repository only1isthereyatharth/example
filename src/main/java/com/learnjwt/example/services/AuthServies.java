package com.learnjwt.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.learnjwt.example.dto.AuthRequest;
import com.learnjwt.example.dto.AuthResponse;
import com.learnjwt.example.dto.RegisterRequest;
import com.learnjwt.example.entity.AppUser;
import com.learnjwt.example.exception.ConflictException;
import com.learnjwt.example.exception.InvalidCredentialsException;
import com.learnjwt.example.repository.UserRepo;

@Service
public class AuthServies {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtServie jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
            
        AppUser user = new AppUser(request.getUsername(), passwordEncoder.encode(request.getPassword()), com.learnjwt.example.entity.Role.ROLE_USER );
        try {
            user = userRepo.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Username already exists");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(user.getUsername(), token);
    }

    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtService.generateToken(userDetails);
        return new AuthResponse(userDetails.getUsername(), token);
    }
}
