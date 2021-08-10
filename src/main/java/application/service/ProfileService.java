package application.service;

import application.dao.DaoPerson;
import application.dao.DaoPost;
import application.exceptions.PersonNotFindException;
import application.models.Person;
import application.models.PersonDto;
import application.models.Post;
import application.models.PostDto;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import application.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DaoPerson daoPerson;
    private final PostsService postsService;
    private final DaoPost daoPost;
    private final JwtTokenProvider jwtTokenProvider;

    public GeneralResponse<PersonDto> getPerson(int id) {

        Person person = daoPerson.get(id);
        return new GeneralResponse<>(PersonDto.fromPerson(person));
    }

    public GeneralResponse<PersonDto> getProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        PersonDto personDto = PersonDto.fromPerson(person);

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

    public GeneralListResponse<PersonDto> getPersons(String firstName, String lastName, Long ageFrom, Long ageTo, String country, String city) throws PersonNotFindException {

        val listPersons = daoPerson.getPersons(firstName, lastName, ageFrom, ageTo, country, city);

        if (listPersons.isEmpty()) {
            throw new PersonNotFindException();
        }
        return new GeneralListResponse<>(listPersons
                .stream()
                .map(PersonDto::fromPerson)
                .collect(Collectors.toList()));
    }
}
