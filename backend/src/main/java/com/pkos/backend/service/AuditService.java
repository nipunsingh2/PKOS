package com.pkos.backend.service;

import com.pkos.backend.entity.AuditLog;
import com.pkos.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AuditService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public void logEvent(String action, String username) {

        AuditLog auditLog = AuditLog.builder()
                .username(username)
                .action(action)
                .createdAt(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);

        logger.info(
                "AUDIT SAVED | User: {} | Action: {}",
                username,
                action
        );
    }
}