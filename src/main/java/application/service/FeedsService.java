package application.service;

import application.dao.DaoPerson;
import application.dao.DaoPost;
import application.models.Person;
import application.models.PersonDto;
import application.models.Post;
import application.models.PostDto;
import application.responses.GeneralListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedsService {
    private final DaoPost daoPost;
    private final DaoPerson daoPerson;

    public GeneralListResponse<PostDto> getFeed(){
        List<Post> postList = new ArrayList<>();
        List<PostDto> postDtoList = new ArrayList<>();

        for (Post post : daoPost.getAll()){
            PostDto postDto = new PostDto();
            postDto.setId(post.getId());
            postDto.setTime(post.getTime());
            postDto.setTitle(post.getTitle());
            postDto.setPostText(post.getPostText());
            postDto.setBlocked(post.isBlocked());
            postDto.setLikes(15);
            postDto.setComments(new ArrayList<>());

            Person person = daoPerson.get(post.getAuthorId());
            PersonDto author = new PersonDto();

            author.setId(person.getId());
            author.setFirstName(person.getFirstName());
            author.setLastName(person.getLastName());
            author.setRegDate(person.getRegDate());
            author.setBirthDate(person.getBirthDate());
            author.setEmail(person.getEmail());
            author.setPhone(person.getPhone());
            author.setPhoto(person.getPhoto());
            author.setAbout(person.getAbout());
            author.setCity(person.getCity());
            author.setCountry(person.getCountry());
            author.setMessagesPermission("ALL");
            author.setLastOnlineTime(person.getLastOnlineTime());
            author.setBlocked(person.isBlocked());
            postDto.setAuthor(author);
            postDtoList.add(postDto);
        }
        return new GeneralListResponse<>(postDtoList);
    }
}
