package application.service;

import application.dao.DaoNotification;
import application.dao.DaoPerson;
import application.models.NotificationType;
import application.models.Person;
import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final DaoPerson daoPerson;
    private final DaoNotification daoNotification;

    @Scheduled(cron = "${scheduled.time.birthdate.friends}")
    private void getBirthDateId() {
        LocalDate now = LocalDate.now();
        List<Person> allPerson = daoPerson.getAllPerson();
        for (Person person : allPerson) {
            List<Integer> idList = daoPerson.getFriends(person.getId()).stream().filter(p -> Instant
                    .ofEpochMilli(person.getBirthDate()).atZone(ZoneId.systemDefault())
                    .toLocalDate().getDayOfMonth() == now.getDayOfMonth() && Instant
                    .ofEpochMilli(person.getBirthDate()).atZone(ZoneId.systemDefault())
                    .toLocalDate().getMonth() == now.getMonth()).map(Person::getId).collect(Collectors.toList());

            daoNotification.addFriendBirthdateNotification(System.currentTimeMillis(), person.getId(),
                    idList, person.getEmail(), NotificationType.FRIEND_BIRTHDAY);
        }
    }

    @Scheduled(cron = "0 10 1 * * ?")
    private static void saveInDropbox() throws IOException, DbxException {
        DropboxService.saveInDropbox();
    }

    @Scheduled(cron = "0 0 2 * * ?")
    private static void deleteFromDropbox() {
        DropboxService.deleteFromDropbox();
    }
}
