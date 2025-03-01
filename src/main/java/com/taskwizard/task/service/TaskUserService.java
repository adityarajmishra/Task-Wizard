package com.taskwizard.task.service;

import com.taskwizard.task.domain.Task;
import java.util.List;
import java.util.UUID;

public interface TaskUserService {
    List<Task> getTasksByUserId(UUID userId);
    void validateUser(UUID userId);
}