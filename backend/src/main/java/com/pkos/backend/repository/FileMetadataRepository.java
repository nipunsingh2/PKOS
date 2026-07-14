package com.pkos.backend.repository;

import com.pkos.backend.entity.FileMetadata;
import com.pkos.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository
        extends JpaRepository<FileMetadata, Long> {

        Page<FileMetadata> findByUser(
                User user,
                Pageable pageable
        );

        Optional<FileMetadata> findByIdAndUser(
                Long id,
                User user
        );
}