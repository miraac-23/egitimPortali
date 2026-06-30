package com.egitim.portal.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/** Task demo için girdi/çıktı DTO'ları. */
public final class TaskDtos {

    private TaskDtos() {
    }

    /** İstemciden gelen görev (Bean Validation ile doğrulanır). */
    public static class TaskRequest {

        @NotBlank(message = "title boş olamaz")
        private String title;

        private String description;

        @NotNull(message = "status zorunludur (TODO, IN_PROGRESS, DONE)")
        private TaskStatus status;

        @NotNull(message = "priority zorunludur")
        @Min(value = 1, message = "priority en az 1 olmalı")
        @Max(value = 5, message = "priority en fazla 5 olmalı")
        private Integer priority;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }
    }

    /** İstemciye dönen görev. */
    public record TaskResponse(
            Long id,
            String title,
            String description,
            TaskStatus status,
            Integer priority,
            LocalDateTime createdAt
    ) {
        public static TaskResponse fromEntity(Task task) {
            return new TaskResponse(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getPriority(),
                    task.getCreatedAt());
        }
    }
}
