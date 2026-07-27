package com.pkos.backend.controller;

import com.pkos.backend.dto.request.CreateReminderRequest;
import com.pkos.backend.dto.request.UpdateReminderRequest;
import com.pkos.backend.dto.response.ReminderResponse;
import com.pkos.backend.service.ReminderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    private final ReminderService reminderService;

    public ReminderController(ReminderService reminderService) {
        this.reminderService = reminderService;
    }

    @PostMapping("/notes/{noteId}")
    public ResponseEntity<ReminderResponse> createReminder(
            @PathVariable Long noteId,
            @Valid @RequestBody CreateReminderRequest request) {

        return ResponseEntity.ok(
                reminderService.createReminder(noteId, request)
        );
    }

    @PutMapping("/{reminderId}")
    public ResponseEntity<ReminderResponse> updateReminder(
            @PathVariable Long reminderId,
            @Valid @RequestBody UpdateReminderRequest request) {

        return ResponseEntity.ok(
                reminderService.updateReminder(reminderId, request)
        );
    }

    @DeleteMapping("/{reminderId}")
    public ResponseEntity<Void> deleteReminder(
            @PathVariable Long reminderId) {

        reminderService.deleteReminder(reminderId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{reminderId}/complete")
    public ResponseEntity<ReminderResponse> markCompleted(
            @PathVariable Long reminderId) {

        return ResponseEntity.ok(
                reminderService.markCompleted(reminderId)
        );
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ReminderResponse>> getPendingReminders() {

        return ResponseEntity.ok(
                reminderService.getPendingReminders()
        );
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<ReminderResponse>> getUpcomingReminders() {

        return ResponseEntity.ok(
                reminderService.getUpcomingReminders()
        );
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<ReminderResponse>> getOverdueReminders() {

        return ResponseEntity.ok(
                reminderService.getOverdueReminders()
        );
    }
}