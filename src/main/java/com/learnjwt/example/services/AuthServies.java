package com.learnjwt.example.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.learnjwt.example.dto.AuthRequest;
import com.learnjwt.example.dto.AuthResponse;
import com.learnjwt.example.dto.RegisterRequest;
import com.learnjwt.example.entity.AppUser;
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

    public AuthResponse register(RegisterRequest request) throws Exception {
        Optional<AppUser> existingUser = userRepo.findByUsername(request.getUsername());

        if(existingUser.isPresent())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            
        AppUser user = new AppUser(request.getUsername(), passwordEncoder.encode(request.getPassword()), com.learnjwt.example.entity.Role.ROLE_USER );
        try {
            userRepo.save(user);
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(user.getUsername(), token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while registering user");
        }
    }

    public AuthResponse login(AuthRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            String token = jwtService.generateToken(userDetails);
            return new AuthResponse(userDetails.getUsername(), token);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    }
}
