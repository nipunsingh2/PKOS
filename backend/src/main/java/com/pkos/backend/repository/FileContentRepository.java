package com.pkos.backend.repository;

import com.pkos.backend.entity.FileContent;
import com.pkos.backend.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileContentRepository
        extends JpaRepository<FileContent, Long> {

    @Query("""
            SELECT fc
            FROM FileContent fc
            WHERE fc.fileMetadata.user = :user
            AND LOWER(fc.content)
                LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<FileContent> searchUserFiles(
            @Param("user") User user,
            @Param("keyword") String keyword,
            Pageable pageable
    );

}