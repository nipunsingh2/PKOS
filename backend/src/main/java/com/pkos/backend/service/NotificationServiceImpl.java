package com.pkos.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pkos.backend.dto.response.NotificationResponse;
import com.pkos.backend.entity.Notification;
import com.pkos.backend.entity.NotificationType;
import com.pkos.backend.entity.Reminder;
import com.pkos.backend.entity.User;
import com.pkos.backend.exception.ResourceNotFoundException;
import com.pkos.backend.mapper.NotificationMapper;
import com.pkos.backend.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        User currentUser = currentUserService.getCurrentUser();

        return notificationRepository.countByUserAndReadFalse(currentUser);
    }

    @Override
    public NotificationResponse markAsRead(Long notificationId) {

        User currentUser = currentUserService.getCurrentUser();

        Notification notification =
                findOwnedNotification(notificationId, currentUser);

        notification.setRead(true);

        notificationRepository.save(notification);

        return notificationMapper.toResponse(notification);
    }

    @Override
    public void createReminderNotification(Reminder reminder) {

        Notification notification = new Notification();

        notification.setUser(reminder.getNote().getUser());
        notification.setReminder(reminder);
        notification.setType(NotificationType.REMINDER);
        notification.setMessage("Reminder: " + reminder.getNote().getTitle());

        notificationRepository.save(notification);
    }

    private Notification findOwnedNotification(Long notificationId, User user) {

        Notification notification = notificationRepository
                .findById(notificationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Notification not found");
        }

        return notification;
    }
}