package com.pkos.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pkos.backend.entity.enums.MemorySource;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "memory_type",
            nullable = false,
            length = 30
    )
    private MemoryType memoryType;

    @Column(
            nullable = false,
            length = 500
    )
    private String value;

    @Column(
            nullable = false,
            precision = 3,
            scale = 2
    )
    private BigDecimal confidence;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private MemorySource source;

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