package com.taskwizard.task.repository;

import com.taskwizard.task.domain.Task;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.jcip.annotations.ThreadSafe;

@Repository
@ThreadSafe
public class TaskRepository {
    private final ConcurrentHashMap<UUID, Task> tasks = new ConcurrentHashMap<>();

    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(UUID id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> findByUserId(UUID userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public void deleteById(UUID id) {
        tasks.remove(id);
    }
}