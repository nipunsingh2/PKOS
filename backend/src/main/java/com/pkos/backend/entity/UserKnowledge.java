package com.pkos.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.pkos.backend.entity.enums.KnowledgeSource;
import com.pkos.backend.entity.enums.KnowledgeType;

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
        name = "user_knowledge",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_knowledge_user_key",
                        columnNames = {
                                "user_id",
                                "knowledge_key"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_user_knowledge_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_user_knowledge_type",
                        columnList = "knowledge_type"
                ),
                @Index(
                        name = "idx_user_knowledge_key",
                        columnList = "knowledge_key"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserKnowledge {

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
            name = "knowledge_type",
            nullable = false,
            length = 30
    )
    private KnowledgeType knowledgeType;

    @Column(
            name = "knowledge_key",
            nullable = false,
            length = 100
    )
    private String key;

    @Column(
            nullable = false,
            length = 150
    )
    private String displayName;

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
    private KnowledgeSource source;

    @CreationTimestamp
    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private LocalDateTime lastVerifiedAt;

}