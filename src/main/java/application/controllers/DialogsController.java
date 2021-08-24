package application.controllers;

import application.models.Message;
import application.models.dto.DialogsActivityResponseDto;
import application.models.dto.MessageDeleteDto;
import application.models.dto.MessageResponseDto;
import application.models.requests.MessageSendDtoRequest;
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
