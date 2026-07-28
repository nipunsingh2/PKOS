package com.pkos.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.Event;
import com.pkos.backend.entity.User;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByUserOrderByStartTimeAsc(User user);

    Optional<Event> findByIdAndUser(Long id, User user);

    List<Event> findByUserAndStartTimeBetweenOrderByStartTimeAsc(
            User user,
            LocalDateTime start,
            LocalDateTime end);

    List<Event> findByUserAndStartTimeGreaterThanEqualOrderByStartTimeAsc(
            User user,
            LocalDateTime start);
}