package com.pkos.backend.service;

import com.pkos.backend.dto.request.LoginRequest;
import com.pkos.backend.dto.request.RegisterRequest;
import com.pkos.backend.dto.response.AuthResponse;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.EmailAlreadyExistsException;
import com.pkos.backend.exception.UserAlreadyExistsException;
import com.pkos.backend.exception.UserNotFoundException;
import com.pkos.backend.repository.UserRepository;
import com.pkos.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        validateRegistration(request);

        User user = buildUser(request);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        authenticate(request);

        User user = getUserByEmail(request.getEmail());

        UserDetails userDetails = buildUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, "Bearer");
    }

    private void validateRegistration(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }
    }

    private User buildUser(RegisterRequest request) {

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
    }

    private void authenticate(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    private UserDetails buildUserDetails(User user) {

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}