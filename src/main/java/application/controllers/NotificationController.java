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
    private ResponseEntity<GeneralListResponse<NotificationDto>> getNotifications(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(notificationService.getNotifications(), offset, itemPerPage));
    }

    @PutMapping("/notifications")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> readNotifications(
            @RequestParam(required = false) boolean all) {

        return ResponseEntity.ok(new GeneralResponse<>(notificationService.readNotifications()));
    }

    @PutMapping("/notifications/{id}")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> readNotificationForId
            (@RequestParam(value = "id") int id) {
        return ResponseEntity.ok(new GeneralResponse<>(notificationService.readNotificationForId(id)));
    }
}
