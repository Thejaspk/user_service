package com.tejusko.user_service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles; // Import ActiveProfiles
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Needed for addFilters

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tejusko.user_service.entity.User;
import com.tejusko.user_service.repository.UserRepository;
import com.tejusko.user_service.security.JwtUtil;
import com.tejusko.user_service.service.EmailService;

// Use AutoConfigureMockMvc to disable security filters for this test,
// as we are testing public endpoints that should not require authentication.
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // <-- THE FIX
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // for converting objects to JSON

    @MockBean
    private UserRepository userRepo;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private EmailService emailService;

    @Test
    void testRegisterEndpoint() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password123");

        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("User registered successfully"));

        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void testLoginEndpoint() throws Exception {
        String rawPassword = "password123";
        // NOTE: The BCryptPasswordEncoder instance should ideally be mocked or configured 
        // to match the one used in the application context for consistency.
        String encodedPassword = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(rawPassword);

        User existing = new User();
        existing.setUsername("testuser");
        existing.setPassword(encodedPassword);

        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(existing));
        when(jwtUtil.generateToken("testuser")).thenReturn("fake-jwt-token");

        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword(rawPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    void testForgotPasswordEndpoint() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("testuser")).thenReturn("reset-token");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Reset link sent to your email"));

        verify(emailService, times(1))
            .sendEmail(eq("test@example.com"), eq("Password Reset Request"), contains("reset-token"));
    }

    @Test
    void testResetPasswordEndpoint() throws Exception {
        String token = "reset-token";
        // Assuming your reset endpoint relies on JWT validation to get the username
        // We ensure the JwtUtil is mocked to return a valid username for the token.

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("oldPassword");

        when(jwtUtil.extractUsername(token)).thenReturn("testuser");
        when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userRepo.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"reset-token\",\"newPassword\":\"newPassword123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Password reset successful"));

        verify(userRepo, times(1)).save(any(User.class));
    }
}
