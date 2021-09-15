package application.controllers;

import application.models.Message;
import application.models.dto.*;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.DialogsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogsController {

    private final DialogsService dialogsService;

    @GetMapping
    private ResponseEntity<GeneralListResponse<DialogDto>> getDialogs(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getDialogs(): start():");
        GeneralListResponse<DialogDto> generalListResponse = new GeneralListResponse<>(dialogsService.getDialogs(), offset, itemPerPage);
        log.debug("getDialogs(): query = {}", query);
        log.debug("getDialogs(): responseList = {}", generalListResponse);
        log.info("getDialogs(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @PostMapping
    private ResponseEntity<GeneralResponse<DialogIdDto>> createDialog(@RequestBody DialogCreateDtoRequest request) {

        log.info("createDialog(): start():");
        GeneralResponse<DialogIdDto> generalResponse = new GeneralResponse<>(dialogsService.createDialog(request));
        log.debug("createDialog(): requestBody = {}", request);
        log.debug("createDialog(): response = {}", generalResponse);
        log.info("createDialog(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("{id}/messages")
    private ResponseEntity<GeneralListResponse<MessageDto>> getMessagesInDialog(
            @PathVariable("id") int dialogId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getMessagesInDialog(): start():");
        GeneralListResponse<MessageDto> generalListResponse = new GeneralListResponse<>(dialogsService
                .getMessagesInDialog(offset, itemPerPage, dialogId), offset, itemPerPage);
        log.debug("getMessagesInDialog(): query = {}", query);
        log.debug("getMessagesInDialog(): responseList = {}", generalListResponse);
        log.info("getMessagesInDialog(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @GetMapping("/unreaded")
    private ResponseEntity<GeneralResponse<UnreadedCountDto>> getCountUnreaded() {

        log.info("getCountUnreaded(): start():");
        GeneralResponse<UnreadedCountDto> generalResponse = new GeneralResponse<>(dialogsService.getUnreadedCount());
        log.debug("getCountUnreaded(): response = {}", generalResponse);
        log.info("getCountUnreaded(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PostMapping("/{id}/messages")
    private ResponseEntity<GeneralResponse<Message>> sendMessage(@PathVariable int id,
                                                                 @RequestBody MessageSendDtoRequest request) {
        log.info("sendMessage(): start():");
        GeneralResponse<Message> generalResponse = new GeneralResponse<>(dialogsService.sendMessage(id, request));
        log.debug("sendMessage(): messageId = {}, requestBody = {}", id, request);
        log.debug("sendMessage(): response = {}", generalResponse);
        log.info("sendMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    private ResponseEntity<GeneralResponse<MessageDeleteDto>> deleteMessage(@PathVariable("dialog_id") int dialogId,
                                                                            @PathVariable("message_id") int messageId) {
        log.info("deleteMessage(): start():");
        GeneralResponse<MessageDeleteDto> generalResponse = new GeneralResponse<>(dialogsService.deleteMessage(messageId, dialogId));
        log.debug("deleteMessage(): dialogId = {}, messageId = {}", dialogId, messageId);
        log.debug("deleteMessage(): response = {}", generalResponse);
        log.info("deleteMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    private ResponseEntity<GeneralResponse<Message>> editMessage(@PathVariable("dialog_id") int dialogId,
                                                                 @PathVariable("message_id") int messageId,
                                                                 @RequestBody MessageSendDtoRequest request) {
        log.info("editMessage(): start():");
        GeneralResponse<Message> generalResponse = new GeneralResponse<>(dialogsService.editMessage(messageId, request));
        log.debug("editMessage(): dialogId = {}, messageId = {}, requestBody = {}", dialogId, messageId, request);
        log.debug("editMessage(): response = {}", generalResponse);
        log.info("editMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> readMessage(@PathVariable("dialog_id") int dialogId,
                                                                            @PathVariable("message_id") int messageId) {

        log.info("readMessage(): start():");
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(dialogsService.readMessage(dialogId, messageId));
        log.debug("readMessage(): dialogId = {}, messageId = {}", dialogId, messageId);
        log.debug("readMessage(): response = {}", generalResponse);
        log.info("readMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("/{id}/activity/{user_id}")
    private ResponseEntity<GeneralResponse<DialogsActivityResponseDto>> getActivity(
            @PathVariable("id") int dialogId, @PathVariable("user_id") int userId) {

        log.info("getActivity(): start():");
        GeneralResponse<DialogsActivityResponseDto> generalResponse = new GeneralResponse<>(dialogsService.getActivity(dialogId, userId));
        log.debug("getActivity(): dialogId = {}, userId = {}", dialogId, userId);
        log.debug("getActivity(): response = {}", generalResponse);
        log.info("getActivity(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PostMapping("{id}/activity/{user_id}")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> changeTypingStatus(
            @PathVariable("id") int dialogId, @PathVariable("user_id") int userId) {

        log.info("changeTypingStatus(): start():");
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(new MessageResponseDto());
        log.debug("changeTypingStatus(): dialogId = {}, userId = {}", dialogId, userId);
        log.debug("changeTypingStatus(): response = {}", generalResponse);
        log.info("changeTypingStatus(): finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
