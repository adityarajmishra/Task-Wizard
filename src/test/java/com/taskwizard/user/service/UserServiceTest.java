package com.taskwizard.user.service;

import com.taskwizard.user.domain.User;
import com.taskwizard.user.dto.UserRequest;
import com.taskwizard.user.repository.UserRepository;
import com.taskwizard.task.domain.Task;
import com.taskwizard.task.service.TaskService;
import com.taskwizard.common.exception.BusinessException;
import com.taskwizard.common.exception.DuplicateResourceException;
import com.taskwizard.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private UserService userService;

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
    void createUser_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(userRequest);

        assertNotNull(result);
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());

        verify(userRepository).findByEmail(userRequest.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_DuplicateEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(DuplicateResourceException.class, () ->
                userService.createUser(userRequest)
        );

        verify(userRepository).findByEmail(userRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(testUser.getId());

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());

        verify(userRepository).findById(testUser.getId());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                userService.getUserById(UUID.randomUUID())
        );
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(taskService.getTasksByUserId(any(UUID.class))).thenReturn(Collections.emptyList());

        userService.deleteUser(testUser.getId());

        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void deleteUser_WithTasks() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(testUser));
        when(taskService.getTasksByUserId(any(UUID.class)))
                .thenReturn(List.of(Task.builder().build()));

        assertThrows(BusinessException.class, () ->
                userService.deleteUser(testUser.getId())
        );

        verify(userRepository, never()).deleteById(any());
    }
}