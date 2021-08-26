package application.service;

import application.dao.DaoMessage;
import application.dao.DaoPerson;
import application.models.Dialog;
import application.models.Message;
import application.models.Person;
import application.models.ReadStatus;
import application.models.dto.DialogsActivityResponseDto;
import application.models.dto.MessageDeleteDto;
import application.models.dto.MessageResponseDto;
import application.models.requests.MessageSendDtoRequest;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class DialogsService {

    private final DaoMessage daoMessage;
    private final DaoPerson daoPerson;

    private int getAuthorId() {
        Person person = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return person.getId();
    }

    public ResponseEntity<GeneralResponse<Message>> sendMessage(int id, MessageSendDtoRequest request) {
        Message message = new Message();
        message.setMessageText(request.getMessageText());
        message.setAuthorId(getAuthorId());
        Dialog dialog = daoMessage.getDialogById(id);
        if (dialog == null) {
            throw new EntityNotFoundException("Dialog with id = " + id + " is not exist");
        }
        message.setRecipientId(dialog.getFirstUserId() == getAuthorId()
                ? dialog.getSecondUserId()
                : dialog.getFirstUserId());
        message.setDialogId(id);
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
        DialogsActivityResponseDto dialogsActivityResponseDto = new DialogsActivityResponseDto(false,
                daoPerson.getLastOnlineTime(userId));
        return ResponseEntity.ok(new GeneralResponse<>(dialogsActivityResponseDto));
    }
}
