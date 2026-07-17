package com.pkos.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "file_contents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtractionType extractionType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id", nullable = false, unique = true)
    private FileMetadata fileMetadata;
}