package com.taskmanager.dto;

import com.taskmanager.model.TaskPriority;
import com.taskmanager.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

public class TaskDtos {

    @Getter @Setter
    public static class CreateTaskRequest {
        @NotBlank public String title;
        public String description;
        public TaskPriority priority;
        public Instant dueDate;
    }

    @Getter @Setter
    public static class UpdateTaskRequest {
        public String title;
        public String description;
        public TaskPriority priority;
        public Instant dueDate;
    }

    @Getter @Setter
    public static class UpdateTaskStatusRequest {
        public TaskStatus status;
    }

    @Getter @Setter @AllArgsConstructor
    public static class TaskDto {
        public Long id;
        public String title;
        public String description;
        public TaskStatus status;
        public TaskPriority priority;
        public Instant dueDate;
        public Instant createdAt;
    }
}
