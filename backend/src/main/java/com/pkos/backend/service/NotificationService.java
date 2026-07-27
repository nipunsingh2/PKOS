package com.pkos.backend.service;

import java.util.List;

import com.pkos.backend.dto.response.NotificationResponse;
import com.pkos.backend.entity.Reminder;

public interface NotificationService {

    List<NotificationResponse> getNotifications();

    List<NotificationResponse> getUnreadNotifications();

    long getUnreadCount();

    NotificationResponse markAsRead(Long notificationId);

    void createReminderNotification(Reminder reminder);
}