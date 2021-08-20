package application.service;

import application.dao.DaoComment;
import application.dao.DaoLike;
import application.dao.DaoPerson;
import application.dao.DaoPost;
import application.models.Person;
import application.models.Post;
import application.models.dto.MessageRequestDto;
import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.requests.PersonSettingsDtoRequest;
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

        for (Post post : daoPost.getAll()) {
            PostDto postDto = postsService.getPostDto(post.getId());
            postDto.setType("POSTED");
            postDtoList.add(postDto);
        }
        return new GeneralListResponse<>(postDtoList);
    }

    public GeneralListResponse<PersonDto> getPersons(String firstName, String lastName, Long ageFrom, Long ageTo, String country, String city) throws EntityNotFoundException {

        val listPersons = daoPerson.getPersons(firstName, lastName, ageFrom, ageTo, country, city);

        return new GeneralListResponse<>(listPersons
                .stream()
                .map(PersonDto::fromPerson)
                .collect(Collectors.toList()));
    }

    public ResponseEntity<GeneralResponse<PersonDto>> changeProfile(PersonSettingsDtoRequest request) throws ParseException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        if (person == null) {
            throw new EntityNotFoundException("Person with this token is not found.");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Long birthDate = dateFormat.parse(request.getBirth_date()).getTime();
        if (request.getFirst_name().isBlank() || request.getLast_name().isBlank()) {
            daoPerson.updatePersonData(person.getId(), person.getFirstName(), person.getLastName(),
                    birthDate, request.getPhone(), person.getPhoto(), request.getCity(),
                    request.getCountry(), request.getAbout());
            return ResponseEntity.ok(new GeneralResponse<>(PersonDto.fromPerson(person)));
        }
        else daoPerson.updatePersonData(person.getId(), request.getFirst_name(), request.getLast_name(),
                birthDate, request.getPhone(), person.getPhoto(), request.getCity(),
                request.getCountry(), request.getAbout());
        return ResponseEntity.ok(new GeneralResponse<>(PersonDto.fromPerson(person)));
    }

    public ResponseEntity<GeneralResponse<MessageRequestDto>> deleteProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        if (person == null) {
            throw new EntityNotFoundException("Person with this token is not found.");
        }
        daoPerson.deleteFriendshipByPersonId(person.getId());
        daoLike.deleteByPersonId(person.getId());
        daoComment.deleteByAuthorId(person.getId());
        daoPost.deleteByAuthorId(person.getId());
        daoPerson.delete(person);
        return ResponseEntity.ok(new GeneralResponse<MessageRequestDto>(new MessageRequestDto("ok")));
    }
}
