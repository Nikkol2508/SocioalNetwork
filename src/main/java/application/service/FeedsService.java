package application.service;

import application.models.*;
import application.responses.GeneralListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedsService {
    public GeneralListResponse<Post> getFeed(){


        List<Post> postList = new ArrayList<>();

        Post post1 = new Post();
        post1.setId(1);
        post1.setTime(System.currentTimeMillis() - 1000);

        PersonDto author = new PersonDto();
        author.setId(2);
        author.setFirstName("Борис");
        author.setLastName("Булкин");
        author.setRegDate(System.currentTimeMillis() - 3000);
        author.setBirthDate(System.currentTimeMillis() - 9000);
        author.setEmail("gsdfhgsh@skdjfhskdj.ru");
        author.setPhone("9163332211");
        author.setPhoto("");
        author.setAbout("Немного обо мне");
        City city = new City(1, "Москва");
        author.setCity(city.getTitle());
        Country country = new Country(1, "Россия");
        author.setCountry(country.getTitle());
        author.setMessagesPermission("All");
        author.setLastOnlineTime(System.currentTimeMillis() - 40);
        author.setBlocked(false);
        post1.setAuthor(author);

        post1.setTitle("Как написать BACKEND соцсети за одну ночь без кофе и смс");
        post1.setPostText("Никак");
        post1.setBlocked(false);
        post1.setLikes(50);

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(2);
        comment.setPostId("");
        comment.setTime(System.currentTimeMillis()-5);
        comment.setAuthorId(1);
        comment.setBlocked(false);

        comments.add(comment);
        post1.setComments(comments);

        postList.add(post1);
        GeneralListResponse<Post> response = new GeneralListResponse<>(postList);
        response.setTotal(10);
        response.setOffset(-1);
        response.setPerPage(20);

        return response;
    }
}
