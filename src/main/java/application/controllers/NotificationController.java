package application.controllers;

import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  private ResponseEntity<GeneralResponse<MessageResponseDto>> readNotifications(
          @RequestParam(required = false) boolean all) throws InterruptedException {
    return ResponseEntity.ok(notificationService.readNotifications());
  }
}
