package com.example.demoToDo.controller;

import com.example.demoToDo.Utils.JWTUtil;
import com.example.demoToDo.model.LoginDto;
import com.example.demoToDo.model.User;
import com.example.demoToDo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiAuthController.class)
class ApiAuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    void login_shouldReturnJwt_whenCredentialsValid() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@example.com"))
                .thenReturn("mocked-jwt-token");

        // When & Then
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("mocked-jwt-token"));
    }

    @Test
    void login_shouldReturnBadRequest_whenPasswordInvalid() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("wrong-password");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("correct-password");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnBadRequest_whenUserNotFound() throws Exception {
        // Given
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("nonexistent@example.com");
        loginDto.setPassword("password123");

        when(userRepository.findByEmail("nonexistent@example.com"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldReturnSuccess_whenEmailNotExists() throws Exception {
        // Given
        LoginDto registerDto = new LoginDto();
        registerDto.setEmail("newuser@example.com");
        registerDto.setPassword("password123");
        registerDto.setName("New User");

        when(userRepository.findByEmail("newuser@example.com"))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Регистрация успешна"));
    }

    @Test
    void register_shouldReturnBadRequest_whenEmailExists() throws Exception {
        // Given
        LoginDto registerDto = new LoginDto();
        registerDto.setEmail("existing@example.com");
        registerDto.setPassword("password123");
        registerDto.setName("Existing User");

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email уже используется"));
    }

    @Test
    void getProfile_shouldReturnUserData_whenUserAuthenticated() throws Exception {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");

        // Создаём mock-запрос с атрибутом currentUser
        mockMvc.perform(get("/api/auth/me")
                .requestAttr("currentUser", user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getProfile_shouldReturnForbidden_whenUserNotAuthenticated() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isForbidden());
    }
}
