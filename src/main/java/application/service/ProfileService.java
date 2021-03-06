package application.service;

import application.dao.*;
import application.dao.mappers.MapperUtil;
import application.models.*;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.requests.PersonSettingsDtoRequest;
import application.models.requests.PostRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DaoPerson daoPerson;
    private final PostsService postsService;
    private final DaoPost daoPost;
    private final DaoLike daoLike;
    private final DaoComment daoComment;
    private final DaoFile daoFile;
    private final DaoNotification daoNotification;
    private final DaoCity daoCity;

    public PersonDto getPerson(int id) {

        Person person = daoPerson.getById(id);
        if (person == null) {
            throw new EntityNotFoundException(String.format("Person with id %d is not found.", id));
        }
        Person activePerson = daoPerson.getAuthPerson();
        return MapperUtil.getExtendedPersonDto(PersonDto.fromPerson(person), activePerson.getId(), daoPerson);
    }

    public PersonDto getProfile() throws EntityNotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        if (person == null) {
            throw new EntityNotFoundException(String.format("Person with email: %s is not found.",
                    authentication.getName()));
        }
        PersonDto personDto = PersonDto.fromPerson(person);
        personDto.setToken(personDto.getToken());
        personDto.setMe(true);
        return personDto;
    }

    public List<PostDto> getWall(int id) {

        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : daoPost.getAllUsersPosts(id)) {
            PostDto postDto = postsService.getPostDto(post.getId());
            if (postDto.getTime() > System.currentTimeMillis()) {
                postDto.setType("QUEUED");
            } else {
                postDto.setType("POSTED");
            }
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    public List<PersonDto> searchPersons(String firstOrLastName, String firstName, String lastName, Long ageFrom,
                                         Long ageTo, String country, String city) throws EntityNotFoundException {

        if ((firstOrLastName == null || firstOrLastName.isBlank()) && (firstName == null || firstName.isBlank())
                && (lastName == null || lastName.isBlank()) && ageFrom == null && ageTo == null
                && (country == null || country.isBlank()) && (city == null || city.isBlank())) {
            return new ArrayList<>();
        }
        Person activePerson = daoPerson.getAuthPerson();
        if (firstOrLastName != null && !firstOrLastName.isBlank()) {
            return daoPerson.getPersonsByFirstNameSurname(firstOrLastName.trim()).stream().map(PersonDto::fromPerson)
                    .map(personDto -> MapperUtil.getExtendedPersonDto(personDto, activePerson.getId(), daoPerson))
                    .collect(Collectors.toList());
        }
        ZonedDateTime zonedDateTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault());
        ageFrom = ageFrom != null ? zonedDateTime.minusYears(ageFrom).toInstant().toEpochMilli() : null;
        ageTo = ageTo != null ? zonedDateTime.minusYears(ageTo + 1).toInstant().toEpochMilli() : null;
        return daoPerson.searchPersons(firstName, lastName, ageFrom, ageTo, country, city).stream()
                .map(p -> MapperUtil.getExtendedPersonDto(PersonDto.fromPerson(p), activePerson.getId(), daoPerson))
                .collect(Collectors.toList());
    }

    public Post setPost(int authorId, Long publishDate, PostRequest postRequest) {

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setPostText(postRequest.getPostText());
        post.setTime(publishDate == null ? System.currentTimeMillis() : publishDate);
        post.setBlocked(false);
        post.setAuthorId(authorId);
        post.setId(daoPost.savePost(post).getId());

        daoNotification.addNotificationsForFriends(daoPerson.getFriends(authorId).stream()
                        .map(Person::getId).collect(Collectors.toList()),
                daoPerson.getAuthPerson().getId(), post.getTime(),
                post.getId(), daoPerson.getById(post.getAuthorId()).getEmail(), NotificationType.POST.toString(),
                post.getTitle());

        postsService.attachTags2Post(postRequest.getTags(), post.getId());
        return post;

    }

    public PersonDto changeProfile(PersonSettingsDtoRequest request) throws ParseException {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = daoPerson.getByEmail(email);
        if (person == null) {
            throw new EntityNotFoundException("Person with email " + email + " is not found.");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long birthDate = request.getBirthDate() == null ? null : dateFormat.parse(request.getBirthDate()).getTime();
        String firstName = request.getFirstName() == null || request.getFirstName().isBlank() ? person.getFirstName()
                : request.getFirstName();
        String lastName = request.getLastName() == null || request.getLastName().isBlank() ? person.getLastName()
                : request.getLastName();
        String photo = request.getPhotoId() == null ? person.getPhoto()
                : daoFile.getPath(Integer.parseInt(request.getPhotoId()));
        String phone = request.getPhone();
        if (phone != null) {
            phone = request.getPhone().length() == 10 ? "7" + request.getPhone() : request.getPhone();
        }

        daoPerson.updatePersonData(person.getId(), firstName.trim(), lastName.trim(), birthDate, phone,
                photo, saveCity(request.getCity()), saveCountry(request.getCountry()), request.getAbout());

        return PersonDto.fromPerson(daoPerson.getById(person.getId()));
    }

    private String saveCity(String city) {

        if (city != null) {
            city = city.trim();
            City cityFromDb = daoCity.getCityByName(city);
            if (cityFromDb == null && !city.isBlank()) {
                daoCity.saveCity(city);
            }
        }
        return city;
    }

    private String saveCountry(String country) {

        if (country != null) {
            country = country.trim();
            Country countryFromDb = daoCity.getCountryByName(country);
            if (countryFromDb == null && !country.isBlank()) {
                daoCity.setCountry(country);
            }
        }
        return country;
    }

    public MessageResponseDto deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        if (person == null) {
            throw new EntityNotFoundException("Person with this token is not found.");
        }
        daoPerson.deleteFriendshipByPersonId(person.getId());
        daoLike.deleteByPersonId(person.getId());
        daoComment.deleteByAuthorId(person.getId());
        daoPost.deleteByAuthorId(person.getId());
        daoPerson.delete(person.getId());
        return new MessageResponseDto();
    }

    public MessageResponseDto blockPersonForId(int id) {
        Person currentPerson = daoPerson.getAuthPerson();
        daoPerson.blockPersonForId(id, currentPerson.getId());
        String friendshipStatus = "";
        try {
            friendshipStatus = daoPerson.getFriendStatus(id, currentPerson.getId());
            if (friendshipStatus.equals(FriendshipStatus.FRIEND.toString())) {
                daoPerson.deleteFriendForID(id, currentPerson.getId());
            } else if (friendshipStatus.equals(FriendshipStatus.REQUEST.toString())) {
                daoPerson.deleteRequest(id, currentPerson.getId());
            }
            return new MessageResponseDto();
        } catch (EmptyResultDataAccessException exception) {
            return new MessageResponseDto();
        }
    }

    public MessageResponseDto unblockUser(int id) {
        daoPerson.unblockUser(id, daoPerson.getAuthPerson().getId());
        return new MessageResponseDto();
    }

    public MessageResponseDto checkOnline() {
        daoPerson.setLastOnlineTime(daoPerson.getAuthPerson().getId());
        return new MessageResponseDto();
    }
}
