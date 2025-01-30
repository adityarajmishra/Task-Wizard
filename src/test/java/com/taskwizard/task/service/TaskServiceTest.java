package com.taskwizard.task.service;

import com.taskwizard.task.domain.Task;
import com.taskwizard.task.domain.TaskStatus;
import com.taskwizard.task.dto.TaskRequest;
import com.taskwizard.task.dto.TaskUpdateRequest;
import com.taskwizard.task.repository.TaskRepository;
import com.taskwizard.user.domain.User;
import com.taskwizard.user.service.UserService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    private Task testTask;
    private TaskRequest taskRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .name("Test User")
                .email("test@example.com")
                .build();

        testTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .userId(testUser.getId())
                .build();

        taskRequest = TaskRequest.builder()
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .userId(testUser.getId())
                .build();
    }

    @Test
    void createTask_Success() {
        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(taskRequest);

        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getDescription(), result.getDescription());
        assertEquals(testTask.getStatus(), result.getStatus());
        assertEquals(testTask.getUserId(), result.getUserId());

        verify(userService).getUserById(taskRequest.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_UserNotFound() {
        when(userService.getUserById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.createTask(taskRequest)
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskById_Success() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(testTask));

        Task result = taskService.getTaskById(testTask.getId());

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());

        verify(taskRepository).findById(testTask.getId());
    }

    @Test
    void getTaskById_NotFound() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.getTaskById(UUID.randomUUID())
        );
    }

    @Test
    void getTasksByUserId_Success() {
        when(userService.getUserById(any(UUID.class))).thenReturn(testUser);
        when(taskRepository.findByUserId(any(UUID.class)))
                .thenReturn(List.of(testTask));

        List<Task> results = taskService.getTasksByUserId(testUser.getId());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testTask.getId(), results.get(0).getId());

        verify(userService).getUserById(testUser.getId());
        verify(taskRepository).findByUserId(testUser.getId());
    }

    @Test
    void updateTask_Success() {
        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .status(TaskStatus.IN_PROGRESS)
                .description("Updated Description")
                .build();

        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.updateTask(testTask.getId(), updateRequest);

        assertNotNull(result);
        assertEquals(updateRequest.getStatus(), result.getStatus());
        assertEquals(updateRequest.getDescription(), result.getDescription());

        verify(taskRepository).findById(testTask.getId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_NotFound() {
        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.updateTask(UUID.randomUUID(), updateRequest)
        );

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.of(testTask));

        taskService.deleteTask(testTask.getId());

        verify(taskRepository).deleteById(testTask.getId());
    }

    @Test
    void deleteTask_NotFound() {
        when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.deleteTask(UUID.randomUUID())
        );

        verify(taskRepository, never()).deleteById(any());
    }
}