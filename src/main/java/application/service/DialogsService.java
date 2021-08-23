package application.service;

import application.dao.DaoMessage;
import application.dao.DaoPerson;
import application.models.Dialog;
import application.models.Message;
import application.models.Person;
import application.models.ReadStatus;
import application.models.requests.MessageSendDtoRequest;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        message.setRecipientId(dialog.getFirstUserId() == getAuthorId()
                ? dialog.getSecondUserId()
                : dialog.getFirstUserId());
        message.setDialogId(id);
        message.setReadStatus(ReadStatus.SENT);
        int messageId = daoMessage.saveAndReturnMessageId(message);
        return ResponseEntity.ok(new GeneralResponse<>(daoMessage.getById(messageId)));
    }


}
