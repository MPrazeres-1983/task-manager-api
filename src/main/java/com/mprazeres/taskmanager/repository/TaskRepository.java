package com.mprazeres.taskmanager.repository;

import com.mprazeres.taskmanager.model.Task;
import com.mprazeres.taskmanager.model.enums.Priority;
import com.mprazeres.taskmanager.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByUserId(Long userId, Pageable pageable);

    Page<Task> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);

    Page<Task> findByUserIdAndPriority(Long userId, Priority priority, Pageable pageable);

    Page<Task> findByUserIdAndStatusAndPriority(
            Long userId, Status status, Priority priority, Pageable pageable);

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Status status);
}
