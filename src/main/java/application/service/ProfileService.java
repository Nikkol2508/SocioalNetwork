package application.service;

import application.dao.*;
import application.models.NotificationType;
import application.models.Person;
import application.models.Post;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.requests.PersonSettingsDtoRequest;
import application.models.requests.PostRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private final DaoTag daoTag;
    private final DaoFile daoFile;
    private final DaoNotification daoNotification;

    public PersonDto getPerson(int id) {

        Person person = daoPerson.getById(id);
        return PersonDto.fromPerson(person);
    }

    public PersonDto getProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        PersonDto personDto = PersonDto.fromPerson(person);
        personDto.setToken(personDto.getToken());

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

    public List<PersonDto> getPersons(String firstOrLastName, String firstName, String lastName, Long ageFrom,
                                                     Long ageTo, String country, String city)
            throws EntityNotFoundException {

        if (firstOrLastName != null) {
            return daoPerson.getPersonsByFirstNameSurname(firstOrLastName).stream().map(PersonDto::fromPerson)
                    .collect(Collectors.toList());
        }
        val listPersons = daoPerson.getPersons(firstName, lastName, ageFrom, ageTo, country, city);
        return listPersons.stream().map(PersonDto::fromPerson).collect(Collectors.toList());
    }

    public Post setPost(int authorId, Long publishDate, PostRequest postRequest) {

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setPostText(postRequest.getPostText());
        post.setTime(publishDate == null ? System.currentTimeMillis() : publishDate);
        post.setBlocked(false);
        post.setAuthorId(authorId);
        int postId = daoPost.savePost(post).getId();

            daoNotification.addNotificationsForFriends(daoPerson.getFriends(authorId).stream()
                    .map(Person::getId).collect(Collectors.toList()),
                    daoPerson.getAuthPerson().getId(), post.getTime(),
                    post.getId(), daoPerson.getById(post.getAuthorId()).getEmail(), NotificationType.POST.toString(),
                    post.getTitle());

        postsService.attachTags2Post(postRequest.getTags(), postId);
        return post;
    }

    public PersonDto changeProfile(PersonSettingsDtoRequest request) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        if (person == null) {
            throw new EntityNotFoundException("Person with this token is not found.");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long birthDate = dateFormat.parse(request.getBirthDate()).getTime();
        String firstName = request.getFirstName().isBlank() ? person.getFirstName() : request.getFirstName();
        String lastName = request.getLastName().isBlank() ? person.getLastName() : request.getLastName();
        daoPerson.updatePersonData(person.getId(), firstName, lastName, birthDate, request.getPhone(),
                daoFile.getPath(Integer.parseInt(request.getPhotoId())), request.getCity(), request.getCountry(),
                request.getAbout());
        return PersonDto.fromPerson(person);
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
}
