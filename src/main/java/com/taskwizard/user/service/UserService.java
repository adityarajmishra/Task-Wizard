package com.taskwizard.user.service;

import com.taskwizard.common.exception.BusinessException;
import com.taskwizard.task.domain.Task;
import com.taskwizard.task.service.TaskService;
import com.taskwizard.task.service.TaskUserService;
import com.taskwizard.user.domain.User;
import com.taskwizard.user.dto.UserRequest;
import com.taskwizard.user.repository.UserRepository;
import com.taskwizard.common.exception.DuplicateResourceException;
import com.taskwizard.common.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final TaskUserService taskUserService;

    @Autowired
    public UserService(UserRepository userRepository, TaskService taskService, TaskUserService taskUserService) {
        this.userRepository = userRepository;
        this.taskUserService = taskUserService;
    }

    @Transactional
    public User createUser(UserRequest request) {
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new DuplicateResourceException("Email already exists");
                });

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Task> userTasks = taskUserService.getTasksByUserId(userId);
        if (!userTasks.isEmpty()) {
            throw new BusinessException("Cannot delete user with existing tasks");
        }

        userRepository.deleteById(userId);
    }
}