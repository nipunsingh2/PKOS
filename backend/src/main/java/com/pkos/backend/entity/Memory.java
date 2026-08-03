package com.pkos.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pkos.backend.entity.enums.MemorySource;
import com.pkos.backend.entity.enums.MemoryStatus;
import com.pkos.backend.entity.enums.MemoryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "memories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_memory_user_type_value",
                        columnNames = {
                                "user_id",
                                "memory_type",
                                "value"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_memory_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_memory_type",
                        columnList = "memory_type"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Memory {

    /*
     * -------------------------------------------------------------------------
     * Identity
     * -------------------------------------------------------------------------
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    /*
     * -------------------------------------------------------------------------
     * Classification
     * -------------------------------------------------------------------------
     */

    @Enumerated(EnumType.STRING)
    @Column(
            name = "memory_type",
            nullable = false,
            length = 30
    )
    private MemoryType memoryType;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    @Builder.Default
    private MemoryStatus status = MemoryStatus.CURRENT;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private MemorySource source;

    /*
     * -------------------------------------------------------------------------
     * Content
     * -------------------------------------------------------------------------
     */

    @Column(
            nullable = false,
            length = 500
    )
    private String value;

    /**
     * Canonical representation of the memory.
     *
     * Initially this will mirror {@code value}. Later phases will populate it
     * using the Memory Normalization pipeline before semantic duplicate
     * detection.
     */
    @Column(
            name = "normalized_value",
            nullable = false,
            length = 500
    )
    private String normalizedValue;

    /*
     * -------------------------------------------------------------------------
     * Intelligence Metadata
     * -------------------------------------------------------------------------
     */

    @Column(
            nullable = false,
            precision = 3,
            scale = 2
    )
    private BigDecimal confidence;

    /**
     * Number of independent observations that have reinforced this memory.
     *
     * Phase 18.1 initializes this to 1 for every newly created memory.
     */
    @Column(
            nullable = false
    )
    @Builder.Default
    private Integer observationCount = 1;

    /*
     * -------------------------------------------------------------------------
     * Audit
     * -------------------------------------------------------------------------
     */

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}