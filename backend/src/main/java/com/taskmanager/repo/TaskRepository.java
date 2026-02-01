package com.taskmanager.repo;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByOwner(User owner, Pageable pageable);
}
