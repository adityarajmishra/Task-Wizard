package com.taskwizard.task.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskwizard.task.domain.Task;
import com.taskwizard.task.domain.TaskStatus;
import com.taskwizard.task.dto.TaskRequest;
import com.taskwizard.task.dto.TaskUpdateRequest;
import com.taskwizard.task.service.TaskService;
import com.taskwizard.user.domain.User;
import com.taskwizard.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@ExtendWith(MockitoExtension.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void createTask_Success() throws Exception {
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTask.getId().toString()))
                .andExpect(jsonPath("$.title").value(testTask.getTitle()))
                .andExpect(jsonPath("$.status").value(testTask.getStatus().toString()));

        verify(taskService).createTask(any(TaskRequest.class));
    }

    @Test
    void createTask_InvalidRequest() throws Exception {
        taskRequest.setTitle("");  // Invalid title

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskRequest.class));
    }

    @Test
    void getTasksByUser_Success() throws Exception {
        when(taskService.getTasksByUserId(any(UUID.class)))
                .thenReturn(List.of(testTask));

        mockMvc.perform(get("/api/v1/tasks/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTask.getId().toString()))
                .andExpect(jsonPath("$[0].title").value(testTask.getTitle()));

        verify(taskService).getTasksByUserId(testUser.getId());
    }

    @Test
    void updateTask_Success() throws Exception {
        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .status(TaskStatus.IN_PROGRESS)
                .description("Updated Description")
                .build();

        when(taskService.updateTask(any(UUID.class), any(TaskUpdateRequest.class)))
                .thenReturn(testTask);

        mockMvc.perform(patch("/api/v1/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId().toString()));

        verify(taskService).updateTask(any(UUID.class), any(TaskUpdateRequest.class));
    }

    @Test
    void deleteTask_Success() throws Exception {
        doNothing().when(taskService).deleteTask(any(UUID.class));

        mockMvc.perform(delete("/api/v1/tasks/{id}", testTask.getId()))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(testTask.getId());
    }

    @Test
    void deleteTask_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Task not found"))
                .when(taskService).deleteTask(any(UUID.class));

        mockMvc.perform(delete("/api/v1/tasks/{id}", UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}