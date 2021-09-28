package application.controllers;

import application.exceptions.UserIsBlockedException;
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

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/dialogs")
@RequiredArgsConstructor
public class DialogsController {

    public final DialogsService dialogsService;

    @GetMapping
    public ResponseEntity<GeneralListResponse<DialogDto>> getDialogs(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getDialogs(): start():");
        log.debug("getDialogs(): query = {}, offset = {}, itemPerPage{}", query, offset, itemPerPage);
        GeneralListResponse<DialogDto> generalListResponse = new GeneralListResponse<>(dialogsService.getDialogs(),
                offset, itemPerPage);
        log.debug("getDialogs(): responseList = {}", generalListResponse);
        log.info("getDialogs(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @PostMapping
    public ResponseEntity<GeneralResponse<DialogIdDto>> createDialog(@Valid @RequestBody DialogCreateDtoRequest request)
            throws UserIsBlockedException {

        log.info("createDialog(): start():");
        log.debug("createDialog(): requestBody = {}", request);
        GeneralResponse<DialogIdDto> generalResponse = new GeneralResponse<>(dialogsService.createDialog(request));
        log.debug("createDialog(): response = {}", generalResponse);
        log.info("createDialog(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("{id}/messages")
    public ResponseEntity<GeneralListResponse<MessageDto>> getMessagesInDialog(
            @PathVariable("id") int dialogId,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("getMessagesInDialog(): start():");
        log.debug("getMessagesInDialog(): query = {}, offset = {}, itemPerPage{}", query, offset, itemPerPage);
        GeneralListResponse<MessageDto> generalListResponse = new GeneralListResponse<>(dialogsService
                .getMessagesInDialog(offset, itemPerPage, dialogId), offset, itemPerPage);
        log.debug("getMessagesInDialog(): responseList = {}", generalListResponse);
        log.info("getMessagesInDialog(): finish():");
        return ResponseEntity.ok(generalListResponse);
    }

    @GetMapping("/unreaded")
    public ResponseEntity<GeneralResponse<UnreadedCountDto>> getCountUnreaded() {

        log.info("getCountUnreaded(): start():");
        GeneralResponse<UnreadedCountDto> generalResponse = new GeneralResponse<>(dialogsService.getUnreadedCount());
        log.debug("getCountUnreaded(): response = {}", generalResponse);
        log.info("getCountUnreaded(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse<DialogIdDto>> deleteDialog(
            @PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(dialogsService.deleteDialog(id)));
    }

    @PutMapping("/{id}/users")
    public ResponseEntity<GeneralResponse<UserIdsDto>> addUserInDialog(
            @PathVariable int id, @RequestBody UserIdsDto ids) {

        return ResponseEntity.ok(new GeneralResponse<>(dialogsService.addUserInDialog(ids)));
    }

    @DeleteMapping("/{id}/users/{ids}")
    public ResponseEntity<GeneralResponse<UserIdsDto>> deleteUsersFromDialog(
            @PathVariable(value = "id") int dialogId,
            @PathVariable(value = "ids") List<Integer> usersIds) {

        return ResponseEntity.ok(new GeneralResponse<>(dialogsService.deleteUsersFromDialog(usersIds, dialogId)));
    }

    @GetMapping("/{id}/users/invite")
    public ResponseEntity<GeneralResponse<InviteLinkDto>> getLinkToJoinDialog(@PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(new InviteLinkDto()));
    }

    @PutMapping("/{id}/users/join")
    public ResponseEntity<GeneralResponse<UserIdsDto>> joinDialogByLink(@PathVariable int id) {

        return ResponseEntity.ok(new GeneralResponse<>(new UserIdsDto()));
    }

    @PostMapping("/{id}/messages")
    public ResponseEntity<GeneralResponse<Message>> sendMessage(
            @PathVariable int id, @Valid @RequestBody MessageSendDtoRequest request) throws UserIsBlockedException {

        log.info("sendMessage(): start():");
        log.debug("sendMessage(): messageId = {}, requestBody = {}", id, request);
        GeneralResponse<Message> generalResponse = new GeneralResponse<>(dialogsService.sendMessage(id, request));
        log.debug("sendMessage(): response = {}", generalResponse);
        log.info("sendMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @DeleteMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<GeneralResponse<MessageDeleteDto>> deleteMessage(
            @PathVariable("dialog_id") int dialogId,
            @PathVariable("message_id") int messageId) {
        log.info("deleteMessage(): start():");
        log.debug("deleteMessage(): dialogId = {}, messageId = {}", dialogId, messageId);
        GeneralResponse<MessageDeleteDto> generalResponse = new GeneralResponse<>(dialogsService.deleteMessage(messageId, dialogId));
        log.debug("deleteMessage(): response = {}", generalResponse);
        log.info("deleteMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}")
    public ResponseEntity<GeneralResponse<Message>> editMessage(
            @PathVariable("dialog_id") int dialogId,
            @PathVariable("message_id") int messageId,
            @Valid @RequestBody MessageSendDtoRequest request) {

        log.info("editMessage(): start():");
        log.debug("editMessage(): dialogId = {}, messageId = {}, requestBody = {}", dialogId, messageId, request);
        GeneralResponse<Message> generalResponse = new GeneralResponse<>(dialogsService.editMessage(messageId, request));
        log.debug("editMessage(): response = {}", generalResponse);
        log.info("editMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PutMapping("/{dialog_id}/messages/{message_id}/read")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> readMessage(
            @PathVariable("dialog_id") int dialogId,
            @PathVariable("message_id") int messageId) {

        log.info("readMessage(): start():");
        log.debug("readMessage(): dialogId = {}, messageId = {}", dialogId, messageId);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(dialogsService.readMessage(dialogId, messageId));
        log.debug("readMessage(): response = {}", generalResponse);
        log.info("readMessage(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @GetMapping("/{id}/activity/{user_id}")
    public ResponseEntity<GeneralResponse<DialogsActivityResponseDto>> getActivity(
            @PathVariable("id") int dialogId,
            @PathVariable("user_id") int userId) {

        log.info("getActivity(): start():");
        log.debug("getActivity(): dialogId = {}, userId = {}", dialogId, userId);
        GeneralResponse<DialogsActivityResponseDto> generalResponse = new GeneralResponse<>(dialogsService.getActivity(dialogId, userId));
        log.debug("getActivity(): response = {}", generalResponse);
        log.info("getActivity(): finish():");
        return ResponseEntity.ok(generalResponse);
    }

    @PostMapping("{id}/activity/{user_id}")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> changeTypingStatus(
            @PathVariable("id") int dialogId,
            @PathVariable("user_id") int userId) {

        log.info("changeTypingStatus(): start():");
        log.debug("changeTypingStatus(): dialogId = {}, userId = {}", dialogId, userId);
        GeneralResponse<MessageResponseDto> generalResponse = new GeneralResponse<>(new MessageResponseDto());
        log.debug("changeTypingStatus(): response = {}", generalResponse);
        log.info("changeTypingStatus(): finish():");
        return ResponseEntity.ok(generalResponse);
    }
}
