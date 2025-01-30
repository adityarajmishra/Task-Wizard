package com.taskwizard.task.dto;

import com.taskwizard.task.domain.TaskStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskUpdateRequest {
    private String description;
    private TaskStatus status;
}