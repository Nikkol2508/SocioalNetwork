package application.controllers;

import application.models.dto.MessageRequestDto;
import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/notifications")
  private ResponseEntity<GeneralListResponse<NotificationDto>> getNotifications() {
    return ResponseEntity.ok(notificationService.getNotifications());
  }

  @PutMapping("/notifications")
  private ResponseEntity<GeneralResponse<MessageRequestDto>> readNotifications() {
    return ResponseEntity.ok(notificationService.readNotifications());
  }
}
