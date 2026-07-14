package com.pkos.backend.repository;

import com.pkos.backend.entity.FileMetadata;
import com.pkos.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, Long> {

    List<FileMetadata> findByUser(User user);

    Optional<FileMetadata> findByIdAndUser(
            Long id,
            User user
    );
}