package com.pkos.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pkos.backend.entity.User;
import com.pkos.backend.entity.UserKnowledge;
import com.pkos.backend.entity.enums.KnowledgeType;

public interface UserKnowledgeRepository
        extends JpaRepository<UserKnowledge, Long> {

    List<UserKnowledge> findByUser(
            User user
    );

    List<UserKnowledge> findByUserAndKnowledgeType(
            User user,
            KnowledgeType knowledgeType
    );

    Optional<UserKnowledge> findByUserAndKey(
            User user,
            String key
    );

    boolean existsByUserAndKey(
            User user,
            String key
    );

}