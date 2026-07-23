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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.pkos.backend.entity.Notebook;
import com.pkos.backend.repository.NotebookRepository;
import com.pkos.backend.util.AppConstants;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final NotebookRepository notebookRepository;

    @Transactional
    public void register(RegisterRequest request) {

        logger.info("Registration attempt for email: {}", request.getEmail());

        validateRegistration(request);

        User user = buildUser(request);

        User savedUser = userRepository.save(user);

        createDefaultNotebook(savedUser);

        logger.info("User registered successfully with email: {}", request.getEmail());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}", request.getEmail());

        authenticate(request);

        User user = getUserByEmail(request.getEmail());

        UserDetails userDetails = buildUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        logger.info("User logged in successfully: {}", request.getEmail());

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

    private void createDefaultNotebook(User user) {

        Notebook inbox = Notebook.builder()
                .name(AppConstants.DEFAULT_NOTEBOOK_NAME)
                .user(user)
                .build();

        notebookRepository.save(inbox);

        logger.info(
                "Default notebook '{}' created for user: {}",
                AppConstants.DEFAULT_NOTEBOOK_NAME,
                user.getEmail()
        );
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