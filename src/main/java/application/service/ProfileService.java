package application.service;

import application.dao.DaoPost;
import application.models.*;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final DaoPost daoPost;

    public GeneralResponse<PersonDto> getPerson(){
        PersonDto personDto = new PersonDto();
        personDto.setId(2);
        personDto.setFirstName("Борис");
        personDto.setLastName("Булкин");
        personDto.setRegDate(System.currentTimeMillis() - 567);
        personDto.setBirthDate(System.currentTimeMillis() - 1997);
        personDto.setEmail("gsdfhgsh@skdjfhskdj.ru");
        personDto.setPhone("9163332211");
        personDto.setPhoto("");
        personDto.setAbout("Немного обо мне");

        City city = new City(1, "Москва");
        personDto.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        personDto.setCountry(country.getTitle());
        personDto.setMessagesPermission("All");
        personDto.setLastOnlineTime(System.currentTimeMillis() - 40);
        personDto.setBlocked(false);
        GeneralResponse<PersonDto> response = new GeneralResponse<>(personDto);
        return response;
    }

    public GeneralListResponse<PostDto> getUserPosts(int id) {
        List <Post> postList = daoPost.getUserPost(id);
        List <PostDto> postDtoList = new ArrayList<>();
        for (Post post : postList) {
            PostDto postDto = new PostDto();
            postDto.setId(post.getId());
            postDto.setTitle(post.getTitle());
            postDto.setTime(post.getTime());
            postDto.setPostText(post.getPostText());
            postDtoList.add(postDto);
        }

        GeneralListResponse<PostDto> userPostResponse = new GeneralListResponse<>(postDtoList);
        userPostResponse.setTotal(0);
        userPostResponse.setOffset(0);
        userPostResponse.setPerPage(20);

        return userPostResponse;
    }
}
