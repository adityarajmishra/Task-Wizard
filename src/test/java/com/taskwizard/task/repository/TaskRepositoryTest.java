package com.taskwizard.task.repository;

import com.taskwizard.task.domain.Task;
import com.taskwizard.task.domain.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskRepositoryTest {
    private TaskRepository taskRepository;
    private Task testTask;

    @BeforeEach
    void setUp() {
        taskRepository = new TaskRepository();
        testTask = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.PENDING)
                .userId(UUID.randomUUID())
                .build();
    }

    @Test
    void save_Success() {
        Task savedTask = taskRepository.save(testTask);

        assertNotNull(savedTask);
        assertEquals(testTask.getId(), savedTask.getId());
        assertEquals(testTask.getTitle(), savedTask.getTitle());
    }

    @Test
    void findById_Success() {
        taskRepository.save(testTask);

        Optional<Task> result = taskRepository.findById(testTask.getId());

        assertTrue(result.isPresent());
        assertEquals(testTask.getId(), result.get().getId());
    }

    @Test
    void findById_NotFound() {
        Optional<Task> result = taskRepository.findById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserId_Success() {
        taskRepository.save(testTask);

        List<Task> results = taskRepository.findByUserId(testTask.getUserId());

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testTask.getId(), results.get(0).getId());
    }

    @Test
    void findByUserId_NoTasks() {
        List<Task> results = taskRepository.findByUserId(UUID.randomUUID());

        assertTrue(results.isEmpty());
    }

    @Test
    void deleteById_Success() {
        taskRepository.save(testTask);

        taskRepository.deleteById(testTask.getId());

        Optional<Task> result = taskRepository.findById(testTask.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    void concurrentAccess() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    Task task = Task.builder()
                            .id(UUID.randomUUID())
                            .title("Concurrent Task")
                            .build();
                    taskRepository.save(task);
                    taskRepository.findById(task.getId());
                    taskRepository.deleteById(task.getId());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
    }
}