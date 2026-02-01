package com.taskmanager.service;

import com.taskmanager.dto.TaskDtos;
import com.taskmanager.model.*;
import com.taskmanager.repo.TaskRepository;
import com.taskmanager.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    public Page<TaskDtos.TaskDto> getTasks(Pageable pageable, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow();
        return taskRepository.findByOwner(owner, pageable).map(this::toDto);
    }

    public TaskDtos.TaskDto create(TaskDtos.CreateTaskRequest req, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail).orElseThrow();

        Task task = Task.builder()
                .title(req.title)
                .description(req.description)
                .priority(req.priority == null ? TaskPriority.MEDIUM : req.priority)
                .status(TaskStatus.TODO)
                .dueDate(req.dueDate)
                .createdAt(Instant.now())
                .owner(owner)
                .build();

        Task saved = taskRepository.save(task);
        var dto = toDto(saved);

        webSocketService.broadcast("TASK_CREATED", dto);
        return dto;
    }

    public TaskDtos.TaskDto update(Long id, TaskDtos.UpdateTaskRequest req, String ownerEmail) {
        Task task = checkOwner(id, ownerEmail);

        if (req.title != null) task.setTitle(req.title);
        if (req.description != null) task.setDescription(req.description);
        if (req.priority != null) task.setPriority(req.priority);
        if (req.dueDate != null) task.setDueDate(req.dueDate);

        Task saved = taskRepository.save(task);
        var dto = toDto(saved);

        webSocketService.broadcast("TASK_UPDATED", dto);
        return dto;
    }

    public void delete(Long id, String ownerEmail) {
        Task task = checkOwner(id, ownerEmail);
        taskRepository.delete(task);
        webSocketService.broadcast("TASK_DELETED", id);
    }

    public TaskDtos.TaskDto updateStatus(Long id, TaskStatus status, String ownerEmail) {
        Task task = checkOwner(id, ownerEmail);
        task.setStatus(status);
        Task saved = taskRepository.save(task);

        var dto = toDto(saved);
        webSocketService.broadcast("TASK_STATUS_UPDATED", dto);
        return dto;
    }

    private Task checkOwner(Long id, String ownerEmail) {
        Task task = taskRepository.findById(id).orElseThrow();
        if (!task.getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Not allowed");
        }
        return task;
    }

    private TaskDtos.TaskDto toDto(Task task) {
        return new TaskDtos.TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCreatedAt()
        );
    }
}
