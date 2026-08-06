package com.pkos.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.entity.enums.MemoryType;

public interface MemoryRepository
        extends JpaRepository<Memory, Long> {

    List<Memory> findByUser(
            User user
    );

    List<Memory> findByUserAndMemoryType(
            User user,
            MemoryType memoryType
    );

    Optional<Memory> findByUserAndMemoryTypeAndNormalizedValue(
            User user,
            MemoryType memoryType,
            String normalizedValue
    );

    boolean existsByUserAndMemoryTypeAndNormalizedValue(
            User user,
            MemoryType memoryType,
            String normalizedValue
    );

    Optional<Memory> findByIdAndUser(
            Long id,
            User user
    );

    void deleteByIdAndUser(
            Long id,
            User user
    );

}