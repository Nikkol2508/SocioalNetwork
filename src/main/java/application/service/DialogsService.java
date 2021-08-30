package application.service;

import application.dao.DaoMessage;
import application.dao.DaoPerson;
import application.models.Dialog;
import application.models.Message;
import application.models.Person;
import application.models.ReadStatus;
import application.models.dto.*;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DialogsService {

    private final DaoMessage daoMessage;
    private final DaoPerson daoPerson;

    private int getAuthorId() {
        Person person = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return person.getId();
    }

    public ResponseEntity<GeneralResponse<Message>> sendMessage(int dialogId, MessageSendDtoRequest request) {
        Message message = new Message();
        message.setMessageText(request.getMessageText());
        message.setAuthorId(getAuthorId());
        Dialog dialog = daoMessage.getDialogById(dialogId);
        if (dialog == null) {
            throw new EntityNotFoundException("Dialog with id = " + dialogId + " is not exist");
        }
        message.setRecipientId(dialog.getFirstUserId() == getAuthorId()
                ? dialog.getSecondUserId()
                : dialog.getFirstUserId());
        message.setDialogId(dialogId);
        message.setReadStatus(ReadStatus.SENT);
        return ResponseEntity.ok(new GeneralResponse<>(daoMessage.saveAndReturnMessage(message)));
    }

    public ResponseEntity<GeneralResponse<MessageDeleteDto>> deleteMessage(int messageId, int dialogId) {
        daoMessage.deleteById(messageId, dialogId);
        return ResponseEntity.ok(new GeneralResponse<>(new MessageDeleteDto(messageId)));
    }

    public ResponseEntity<GeneralResponse<Message>> editMessage(int messageId, MessageSendDtoRequest request) {
        Message message = daoMessage.getById(messageId);
        if (message == null) {
            throw new EntityNotFoundException("Message with id = " +  messageId + " is not exist");
        }
        message.setMessageText(request.getMessageText());
        return ResponseEntity.ok(new GeneralResponse<>(daoMessage.updateAndReturnMessage(message)));
    }

    public ResponseEntity<GeneralResponse<MessageResponseDto>> readMessage(int dialogId, int messageId) {
        Message message = daoMessage.getById(messageId);
        if (message == null) {
            throw new EntityNotFoundException("Message with id = " +  messageId + " is not exist");
        }
        daoMessage.readMessage(messageId, dialogId);
        return ResponseEntity.ok(new GeneralResponse<>(new MessageResponseDto("ok")));
    }

    public ResponseEntity<GeneralResponse<DialogsActivityResponseDto>> getActivity(int dialogId, int userId) {
        DialogsActivityResponseDto dialogsActivityResponseDto = new DialogsActivityResponseDto(true,
                daoPerson.getLastOnlineTime(userId));
        return ResponseEntity.ok(new GeneralResponse<>(dialogsActivityResponseDto));
    }

    public ResponseEntity<GeneralResponse<UnreadedCountDto>> getUnreadedCount() {
        int id = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        return ResponseEntity.ok(new GeneralResponse<>(
                new UnreadedCountDto(daoMessage.getCountUnreadedMessagesForUser(id))));
    }

    public ResponseEntity<GeneralListResponse<DialogDto>> getDialogs(int offset, int itemPerPage) {
        int userId = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Dialog> dialogList = daoMessage.getDialogListForUser(userId);
        GeneralListResponse<DialogDto> response = new GeneralListResponse<>(dialogList.stream()
                .map(dialog -> fromDialog(dialog, userId))
                .collect(Collectors.toList()));
        response.setPerPage(itemPerPage);
        response.setOffset(offset);
        response.setTotal(dialogList.size());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GeneralListResponse<MessageDto>> getMessagesInDialog(int offset,
                                                                               int itemPerPage,
                                                                               int dialogId) {
        int userId = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        GeneralListResponse<MessageDto> response = new GeneralListResponse<>(daoMessage.getMessagesInDialog(dialogId)
                .stream().map(message -> fromMessage(message, userId)).collect(Collectors.toList()));
        response.getData().stream().filter(messageDto -> messageDto.getReadStatus() == ReadStatus.SENT
                        && messageDto.getRecipientId().getId() == userId)
                .forEach(message -> daoMessage.readMessage(dialogId, message.getId()));
        response.setOffset(offset);
        response.setPerPage(itemPerPage);
        response.setTotal(response.getData().size());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<GeneralResponse<DialogIdDto>> createDialog(DialogCreateDtoRequest request) {
        int activeUserId = daoPerson.getByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()).getId();
        Dialog foundDialog = daoMessage.getDialogByUsersId(activeUserId, request.getUsersIds().get(0));
        if (foundDialog == null) {
            int dialogId = daoMessage.createDialog(
                    activeUserId, request.getUsersIds().get(0));
            Person personFromRequest = daoPerson.getById(request.getUsersIds().get(0));
            sendMessage(dialogId, new MessageSendDtoRequest(String.format("Create new dialog with %s %s",
                    personFromRequest.getFirstName(),
                    personFromRequest.getLastName())));
            return ResponseEntity.ok(new GeneralResponse<>(new DialogIdDto(dialogId)));
        } else {
            return ResponseEntity.ok(new GeneralResponse<>(new DialogIdDto(foundDialog.getId())));
        }
    }

    private DialogDto fromDialog(Dialog dialog, int userId) {
        DialogDto dialogDto = new DialogDto();
        dialogDto.setId(dialog.getId());
        dialogDto.setUnreadCount(daoMessage.getCountUnreadedMessagesInDialog(userId, dialog.getId()));
        dialogDto.setRecipient(PersonDialogsDto.fromPerson(daoPerson.getById(dialog.getFirstUserId() == userId
                ? dialog.getSecondUserId()
                : dialog.getFirstUserId())));
        dialogDto.setLastMessage(fromMessage(daoMessage.getLastMessageInDialog(dialog), userId));
        return dialogDto;
    }

    private MessageDto fromMessage(Message message, int userId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setMessageText(message.getMessageText());
        messageDto.setId(message.getId());
        messageDto.setReadStatus(message.getReadStatus());
        messageDto.setTime(message.getTime());
        messageDto.setAuthorId(PersonDialogsDto.fromPerson(daoPerson.getById(message.getAuthorId())));
        messageDto.setRecipientId(PersonDialogsDto.fromPerson(daoPerson.getById(message.getRecipientId())));
        messageDto.setSentByMe(userId == message.getAuthorId());
        return messageDto;
    }
}
