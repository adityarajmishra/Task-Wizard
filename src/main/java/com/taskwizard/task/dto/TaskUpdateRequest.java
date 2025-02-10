package com.taskwizard.task.dto;

import com.taskwizard.task.domain.TaskStatus;
import lombok.*;

@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    private String description;
    private TaskStatus status;
}