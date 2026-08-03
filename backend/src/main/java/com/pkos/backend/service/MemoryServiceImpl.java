package com.pkos.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.entity.Memory;
import com.pkos.backend.entity.User;
import com.pkos.backend.entity.enums.MemoryStatus;
import com.pkos.backend.entity.enums.MemoryType;
import com.pkos.backend.repository.MemoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemoryServiceImpl
        implements MemoryService {

    private final MemoryRepository memoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Memory> getMemories(
            User user
    ) {

        return memoryRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Memory> getMemories(
            User user,
            MemoryType memoryType
    ) {

        return memoryRepository.findByUserAndMemoryType(
                user,
                memoryType
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Memory> getMemory(
            User user,
            MemoryType memoryType,
            String value
    ) {

        return memoryRepository.findByUserAndMemoryTypeAndValue(
                user,
                memoryType,
                value
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(
            User user,
            MemoryType memoryType,
            String value
    ) {

        return memoryRepository.existsByUserAndMemoryTypeAndValue(
                user,
                memoryType,
                value
        );
    }

    @Override
    public Memory save(
            Memory memory
    ) {

        initializeDefaults(memory);

        return memoryRepository.save(memory);
    }

    /**
     * Initializes newly introduced Phase 18 fields so that existing callers
     * remain fully backward compatible.
     */
    private void initializeDefaults(
            Memory memory
    ) {

        if (memory.getNormalizedValue() == null) {
            memory.setNormalizedValue(memory.getValue());
        }

        if (memory.getObservationCount() == null) {
            memory.setObservationCount(1);
        }

        if (memory.getStatus() == null) {
            memory.setStatus(MemoryStatus.CURRENT);
        }
    }

}