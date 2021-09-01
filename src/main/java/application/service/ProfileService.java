package application.service;

import application.dao.*;
import application.models.Person;
import application.models.Post;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.requests.PersonSettingsDtoRequest;
import application.models.requests.PostRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
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

    public GeneralResponse<PersonDto> getPerson(int id) {

        Person person = daoPerson.getById(id);
        return new GeneralResponse<>(PersonDto.fromPerson(person));
    }

    public GeneralResponse<PersonDto> getProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        PersonDto personDto = PersonDto.fromPerson(person);
        personDto.setToken(personDto.getToken());

        return new GeneralResponse<>(personDto);
    }

    public GeneralListResponse<PostDto> getWall(int id) {

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
        return new GeneralListResponse<>(postDtoList);
    }

    public GeneralListResponse<PersonDto> getPersons(String firstName, String lastName, Long ageFrom,
                                                     Long ageTo, String country, String city)
            throws EntityNotFoundException {

        val listPersons = daoPerson.getPersons(firstName, lastName, ageFrom, ageTo, country, city);

        return new GeneralListResponse<>(listPersons
                .stream()
                .map(PersonDto::fromPerson)
                .collect(Collectors.toList()));
    }

    public GeneralResponse<Post> setPost(int authorId, Long publishDate, PostRequest postRequest) {
        Post addPost = new Post();
        addPost.setTitle(postRequest.getTitle());
        addPost.setPostText(postRequest.getPostText());
        addPost.setTime(publishDate == null ? System.currentTimeMillis() : publishDate);
        addPost.setBlocked(false);
        addPost.setAuthorId(authorId);
        int postId = daoPost.savePost(addPost).getId();
        for (String tag : postRequest.getTags()) {
            daoTag.save(tag);
            daoTag.attachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        return new GeneralResponse<>(addPost);
    }

    public ResponseEntity<GeneralResponse<PersonDto>> changeProfile(PersonSettingsDtoRequest request)
            throws ParseException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Person person = daoPerson.getByEmail(email);
        if (person == null) {
            throw new EntityNotFoundException("Person with email: " + email + " not found");
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        long birthDate = dateFormat.parse(request.getBirthDate()).getTime();
        if (request.getFirstName().isBlank() || request.getLastName().isBlank()) {
            return ResponseEntity.badRequest().body(new GeneralResponse<>(PersonDto.fromPerson(person)));
        } else {
            daoPerson.updatePersonData(person.getId(), request.getFirstName(), request.getLastName(),
                    birthDate, request.getPhone(), daoFile.getPath(Integer.parseInt(request.getPhotoId())),
                    request.getCity(), request.getCountry(), request.getAbout());
            return ResponseEntity.ok(new GeneralResponse<>(PersonDto.fromPerson(person)));
        }
    }


    public ResponseEntity<GeneralResponse<MessageResponseDto>> deleteProfile() {
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
        return ResponseEntity.ok(new GeneralResponse<>(new MessageResponseDto("ok")));
    }
}
