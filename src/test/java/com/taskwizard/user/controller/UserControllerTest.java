package com.taskwizard.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskwizard.config.TestConfig;
import com.taskwizard.user.domain.User;
import com.taskwizard.user.dto.UserRequest;
import com.taskwizard.user.service.UserService;
import com.taskwizard.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .build();

        userRequest = UserRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
    }

    @Test
    @WithMockUser
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserRequest.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.name").value(testUser.getName()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));

        verify(userService).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser
    void createUser_InvalidEmail() throws Exception {
        userRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserRequest.class));
    }

    @Test
    @WithMockUser
    void getUser_Success() throws Exception {
        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);

        mockMvc.perform(get("/api/v1/users/{id}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()));

        verify(userService).getUserById(testUser.getId());
    }

    @Test
    @WithMockUser
    void getUser_NotFound() throws Exception {
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/v1/users/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}