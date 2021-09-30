package application.controllers;

import application.models.dto.MessageResponseDto;
import application.models.dto.NotificationDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ResponseEntity<GeneralListResponse<NotificationDto>> getNotifications(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getNotifications(): start():");
        GeneralListResponse<NotificationDto> generalListResponse = new GeneralListResponse<>(notificationService.getNotifications(), offset, itemPerPage);
        log.debug("getNotifications(): responseList = {}", generalListResponse);
        log.info("getNotifications(): finish():");
        return ResponseEntity.ok(new GeneralListResponse<>(notificationService.getNotifications(), offset, itemPerPage));
    }

    @PutMapping("/notifications")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> readNotifications(
            @RequestParam(required = false) Boolean all,
            @RequestParam(required = false) Integer id) {

        log.info("readNotifications(): start():");
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(notificationService.readNotifications(all, id));
        log.debug("readNotifications(): response = {}", generalResponse);
        log.info("readNotifications(): finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
