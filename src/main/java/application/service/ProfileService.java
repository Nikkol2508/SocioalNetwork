package application.service;

import application.dao.DaoPerson;
import application.dao.DaoPost;
import application.models.*;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DaoPerson daoPerson;
    private final PostsService postsService;
    private final DaoPost daoPost;

    public GeneralResponse<PersonDto> getPerson(int id){

        Person person = daoPerson.get(id);
            PersonDto personDto = new PersonDto();
            personDto.setId(id);
            personDto.setFirstName(person.getFirstName());
            personDto.setLastName(person.getLastName());
            personDto.setRegDate(person.getRegDate());
            personDto.setBirthDate(person.getBirthDate());
            personDto.setEmail(person.getEmail());
            personDto.setPhone(person.getPhone());
            personDto.setPhoto(person.getPhoto());
            personDto.setAbout(person.getAbout());
            personDto.setCity(person.getCity());
            personDto.setCountry(person.getCountry());
            personDto.setMessagesPermission(PermissionMessagesType.ALL.toString());
            personDto.setLastOnlineTime(person.getLastOnlineTime());
            personDto.setBlocked(person.isBlocked());
            //personDto.setToken("kjhfgkfkjh");
            return new GeneralResponse<>(personDto);
    }

    public GeneralResponse<PersonDto> getProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person person = daoPerson.getByEmail(authentication.getName());
        PersonDto personDto = new PersonDto();

        personDto.setId(person.getId());
        personDto.setFirstName(person.getFirstName());
        personDto.setLastName(person.getLastName());
        personDto.setRegDate(person.getRegDate());
        personDto.setBirthDate(person.getBirthDate());
        personDto.setEmail(person.getEmail());
        personDto.setPhone(person.getPhone());
        personDto.setPhoto(person.getPhoto());
        personDto.setAbout(person.getAbout());
        personDto.setCity(person.getCity());
        personDto.setCountry(person.getCountry());
        personDto.setMessagesPermission("ALL");
        personDto.setLastOnlineTime(person.getLastOnlineTime());
        personDto.setBlocked(person.isBlocked());
        personDto.setToken("kjhfgkfkjh");
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
}
