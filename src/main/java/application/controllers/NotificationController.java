package application.controllers;

import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping("/api/v1/notifications")
  private ResponseEntity<GeneralListResponse<NotificationDto>> getNotifications() {
    return ResponseEntity.ok(notificationService.getNotifications());
  }
}
