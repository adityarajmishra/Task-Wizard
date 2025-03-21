package com.taskwizard.task.service;

import com.taskwizard.task.domain.Task;
import com.taskwizard.task.dto.TaskRequest;
import com.taskwizard.task.dto.TaskUpdateRequest;
import com.taskwizard.task.repository.TaskRepository;
import com.taskwizard.user.service.UserService;
import com.taskwizard.common.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@Slf4j
public class TaskService implements TaskUserService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public List<Task> getTasksByUserId(UUID userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public void validateUser(UUID userId) {
        // This will be checked by UserService
    }

    @Transactional
    public Task createTask(TaskRequest request) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .userId(request.getUserId())
                .build();

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public Task getTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @Transactional(readOnly = true)
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Transactional
    public Task updateTask(UUID id, TaskUpdateRequest request) {
        Task task = getTaskById(id);

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(UUID id) {
        getTaskById(id); // Verify task exists
        taskRepository.deleteById(id);
    }
}