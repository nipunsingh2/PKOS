package com.pkos.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.FileContent;

public interface FileContentRepository
        extends JpaRepository<FileContent, Long> {

}