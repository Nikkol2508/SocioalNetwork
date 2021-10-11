package application.dao.mappers;

import application.dao.DaoPerson;
import application.models.FriendshipStatus;
import application.models.dto.PersonDto;
import lombok.experimental.UtilityClass;
import org.springframework.dao.EmptyResultDataAccessException;

@UtilityClass
public class MapperUtil {

    public static PersonDto getExtendedPersonDto(PersonDto personDto, int activePersonId, DaoPerson daoPerson) {
        if (!personDto.isBlocked()) {
            personDto.setBlocked(daoPerson.isPersonBlockedByAnotherPerson(activePersonId, personDto.getId()));
        }
        personDto.setMe(personDto.getId() == activePersonId);
        personDto.setBlockedByThisPerson(daoPerson.isPersonBlockedByAnotherPerson(personDto.getId(), activePersonId));
        try {
            String status = daoPerson.getFriendStatus(personDto.getId(), activePersonId);
            if (status.equals(FriendshipStatus.FRIEND.toString())) {
                personDto.setFriendStatus(FriendshipStatus.FRIEND.toString());
            } else if (status.equals(FriendshipStatus.REQUEST.toString())) {
                if (daoPerson.getSrcPersonIdFriendRequest(personDto.getId(), activePersonId) == activePersonId) {
                    personDto.setFriendStatus(FriendshipStatus.REQUEST_SENT.toString());
                } else {
                    personDto.setFriendStatus(FriendshipStatus.REQUEST_RECEIVED.toString());
                }
            }
        } catch (EmptyResultDataAccessException exception) {
            return personDto;
        }
        return personDto;
    }
}
