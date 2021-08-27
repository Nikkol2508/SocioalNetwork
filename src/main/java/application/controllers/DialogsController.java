package application.controllers;

import application.models.Message;
import application.models.dto.*;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.DialogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return dialogsService.getDialogs(offset, itemPerPage);
    }

    @PostMapping
    private ResponseEntity<GeneralResponse<DialogIdDto>> createDialog(@RequestBody DialogCreateDtoRequest request) {
        return dialogsService.createDialog(request);
    }

    @GetMapping("{id}/messages")
    private ResponseEntity<GeneralListResponse<MessageDto>> getMessagesInDialog(
            @PathVariable("id") int dialogId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {
        return dialogsService.getMessagesInDialog(offset, itemPerPage, dialogId);
    }

    @GetMapping("/unreaded")
    private ResponseEntity<GeneralResponse<UnreadedCountDto>> getCountUnreaded() {
        return dialogsService.getUnreadedCount();
    }

    @PostMapping("/{id}/messages")
    private ResponseEntity<GeneralResponse<Message>> sendMessage(@PathVariable int id,
                                                                 @RequestBody MessageSendDtoRequest request) {
        return dialogsService.sendMessage(id, request);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    private ResponseEntity<GeneralResponse<MessageDeleteDto>> deleteMessage(@PathVariable("dialog_id") int dialogId,
                                                                            @PathVariable("message_id") int messageId) {
        return dialogsService.deleteMessage(messageId, dialogId);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    private ResponseEntity<GeneralResponse<Message>> editMessage(@PathVariable("dialog_id") int dialogId,
                                                                 @PathVariable("message_id") int messageId,
                                                                 @RequestBody MessageSendDtoRequest request) {
        return dialogsService.editMessage(messageId, request);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> readMessage(@PathVariable("dialog_id") int dialogId,
                                                                            @PathVariable("message_id") int messageId) {
        return dialogsService.readMessage(dialogId, messageId);
    }

    @GetMapping("/{id}/activity/{user_id}")
    private ResponseEntity<GeneralResponse<DialogsActivityResponseDto>> getActivity(
            @PathVariable("id") int dialogId, @PathVariable("user_id") int userId) {
        return dialogsService.getActivity(dialogId, userId);
    }

    @PostMapping("{id}/activity/{user_id}")
    private ResponseEntity<GeneralResponse<MessageResponseDto>> changeTypingStatus(
            @PathVariable("id") int dialogId, @PathVariable("user_id") int userId) {
        return ResponseEntity.ok(new GeneralResponse<>(new MessageResponseDto("ok")));
    }
}
