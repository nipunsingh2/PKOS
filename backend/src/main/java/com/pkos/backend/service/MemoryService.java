package com.pkos.backend.service;

import java.util.List;
import java.util.Optional;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.entity.enums.MemoryType;

public interface MemoryService {

    List<Memory> getMemories(
            User user
    );

    List<Memory> getMemories(
            User user,
            MemoryType memoryType
    );

    Optional<Memory> getMemory(
            User user,
            MemoryType memoryType,
            String value
    );

    boolean exists(
            User user,
            MemoryType memoryType,
            String value
    );

    Memory save(
            Memory memory
    );

}