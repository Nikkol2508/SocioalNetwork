package application.service;

import application.dao.DaoDialog;
import application.dao.DaoMessage;
import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.exceptions.UserIsBlockedException;
import application.models.*;
import application.models.dto.*;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
import com.github.dockerjava.api.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
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
    private final DaoNotification daoNotification;
    private final DaoDialog daoDialog;

    private int getActiveUserId() {

        Person person = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return person.getId();
    }

    public Message sendMessage(int dialogId, MessageSendDtoRequest request) throws UserIsBlockedException {

        Message message = new Message();
        message.setMessageText(request.getMessageText());
        message.setAuthorId(getActiveUserId());
        Dialog dialog = daoDialog.getDialogById(dialogId);
        if (dialog == null) {
            throw new EntityNotFoundException("Dialog with id = " + dialogId + " is not exist");
        }
        if (dialog.getFirstUserId() != getActiveUserId() && dialog.getSecondUserId() != getActiveUserId()) {
            throw new UnauthorizedException("You can't send message in this dialog");
        }
        if (daoPerson.isPersonBlockedByAnotherPerson(dialog.getFirstUserId(), dialog.getSecondUserId())
                || daoPerson.isPersonBlockedByAnotherPerson(dialog.getSecondUserId(), dialog.getFirstUserId())
                || daoPerson.getById(dialog.getFirstUserId()).isBlocked()
                || daoPerson.getById(dialog.getSecondUserId()).isBlocked()) {

            throw new UserIsBlockedException("You can't send message (one of the participants of the dialog is blocked)");
        }
        message.setRecipientId(dialog.getFirstUserId() == getActiveUserId()
                ? dialog.getSecondUserId()
                : dialog.getFirstUserId());
        message.setDialogId(dialogId);
        message.setReadStatus(ReadStatus.SENT);
        message = daoMessage.saveAndReturnMessage(message);
        daoNotification.addNotification(message.getRecipientId(), daoPerson.getAuthPerson().getId(), message.getTime(),
                message.getId(), daoPerson.getById(message.getAuthorId()).getEmail(),
                NotificationType.MESSAGE.toString(), message.getMessageText());
        return message;
    }

    public MessageDeleteDto deleteMessage(int messageId, int dialogId) {

        Message messageDel = daoMessage.getById(messageId);
        if (messageDel.getAuthorId() != getActiveUserId()) {
            throw new UnauthorizedException("You can't delete this message");
        } else {
            daoMessage.deleteById(messageId, dialogId);
            return new MessageDeleteDto(messageId);
        }
    }

    public Message editMessage(int messageId, MessageSendDtoRequest request) {

        Message message = daoMessage.getById(messageId);
        if (message == null) {
            throw new EntityNotFoundException("Message with id = " + messageId + " is not exist");
        }
        if (message.getAuthorId() != getActiveUserId()) {
            throw new UnauthorizedException("You can't edit this message");
        } else {
            message.setMessageText(request.getMessageText());
            return daoMessage.updateAndReturnMessage(message);
        }
    }

    public MessageResponseDto readMessage(int dialogId, int messageId) {

        Message message = daoMessage.getById(messageId);
        if (message == null) {
            throw new EntityNotFoundException("Message with id = " + messageId + " is not exist");
        }
        if (message.getAuthorId() != getActiveUserId()) {
            throw new UnauthorizedException("You can't make this message read");
        } else {
            daoMessage.readMessage(messageId, dialogId);
            return new MessageResponseDto();
        }
    }

    public DialogsActivityResponseDto getActivity(int dialogId, int userId) {

        return new DialogsActivityResponseDto(true, daoPerson.getLastOnlineTime(userId));
    }

    public UnreadedCountDto getUnreadedCount() {

        int id = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        return new UnreadedCountDto(daoMessage.getCountUnreadedMessagesForUser(id));
    }

    public List<DialogDto> getDialogs() {

        int userId = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<Dialog> dialogList = daoDialog.getDialogListForUser(userId);
        return dialogList.stream().map(dialog -> fromDialog(dialog, userId)).collect(Collectors.toList());
    }

    public List<MessageDto> getMessagesInDialog(int offset, int itemPerPage, int dialogId) {

        int userId = daoPerson.getByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).getId();
        List<MessageDto> messageDtoList = daoMessage.getMessagesInDialog(daoDialog.getDialogById(dialogId))
                .stream().map(message -> fromMessage(message, userId))
                .collect(Collectors.toList());
        messageDtoList.stream().filter(messageDto -> messageDto.getReadStatus() == ReadStatus.SENT &&
                messageDto.getRecipient().getId() == userId).forEach(message -> daoMessage
                .readMessage(dialogId, message.getId()));
        return messageDtoList;
    }

    public DialogIdDto createDialog(DialogCreateDtoRequest request) throws UserIsBlockedException {

        int activeUserId = daoPerson.getByEmail(SecurityContextHolder.getContext()
                .getAuthentication().getName()).getId();
        if (daoPerson.isPersonBlockedByAnotherPerson(activeUserId, request.getUsersIds().get(0)) ||
                daoPerson.isPersonBlockedByAnotherPerson(request.getUsersIds().get(0), activeUserId)) {
            throw new UserIsBlockedException("You can't create dialog, you are blocked by this user");
        }
        Dialog foundDialog = daoDialog.getDialogByUsersId(activeUserId, request.getUsersIds().get(0));
        if (foundDialog == null) {
            int dialogId = daoDialog.createDialog(activeUserId, request.getUsersIds().get(0));
            Person personFromRequest = daoPerson.getById(request.getUsersIds().get(0));
            sendMessage(dialogId, new MessageSendDtoRequest(String.format("Hi, %s %s. Let's talk!",
                    personFromRequest.getFirstName(), personFromRequest.getLastName())));
            return new DialogIdDto(dialogId);
        } else {
            return new DialogIdDto(foundDialog.getId());
        }
    }

    public DialogIdDto deleteDialog(int id) {

        int activeUserId = getActiveUserId();
        Dialog dialogDel = daoDialog.getDialogById(id);
        if (dialogDel.getFirstUserId() != activeUserId && dialogDel.getSecondUserId() != activeUserId) {
            throw new UnauthorizedException("You can't delete this dialog");
        } else {
            daoDialog.deleteDialogById(id);
            return new DialogIdDto(id);
        }
    }

    public UserIdsDto addUserInDialog(UserIdsDto ids) {

        return ids;
    }

    public UserIdsDto deleteUsersFromDialog(List<Integer> usersIds, int dialogId) {

        return new UserIdsDto(usersIds);
    }

    private DialogDto fromDialog(Dialog dialog, int userId) {

        DialogDto dialogDto = new DialogDto();
        dialogDto.setId(dialog.getId());
        dialogDto.setUnreadCount(daoMessage.getCountUnreadedMessagesInDialog(userId, dialog.getId()));
        dialogDto.setRecipient(PersonDialogsDto.fromPerson(daoPerson
                .getById(dialog.getFirstUserId() == userId ? dialog.getSecondUserId() : dialog.getFirstUserId())));
        dialogDto.setLastMessage(fromMessage(daoMessage.getLastMessageInDialog(dialog), userId));
        return dialogDto;
    }

    private MessageDto fromMessage(Message message, int userId) {

        MessageDto messageDto = new MessageDto();
        messageDto.setMessageText(message.getMessageText());
        messageDto.setId(message.getId());
        messageDto.setReadStatus(message.getReadStatus());
        messageDto.setTime(message.getTime());
        messageDto.setAuthor(PersonDialogsDto.fromPerson(daoPerson.getById(message.getAuthorId())));
        messageDto.setRecipient(PersonDialogsDto.fromPerson(daoPerson.getById(message.getRecipientId())));
        messageDto.setSentByMe(userId == message.getAuthorId());
        return messageDto;
    }
}
