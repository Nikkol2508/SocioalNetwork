package application.service;

import application.models.*;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostsService {

    public GeneralListResponse<Post> getPosts() {
        GeneralListResponse<Post> response = new GeneralListResponse<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        response.setTotal(10);
        response.setOffset(0);
        response.setPerPage(10);
        List<Post> postList = new ArrayList<>();
        Post post = new Post();
        post.setId(1);
        post.setPostText("Вау! Это текст");
        post.setTitle("Это заголовок, Вау!");
        post.setBlocked(false);
        post.setLikes(20);
        Person person = new Person();
        person.setId(2);
        person.setFirstName("Борис");
        person.setLastName("Булкин");
        person.setRegDate(System.currentTimeMillis() - 567);
        person.setBirthDate(System.currentTimeMillis() - 1997);
        person.setEmail("gsdfhgsh@skdjfhskdj.ru");
        person.setPhone("9163332211");
        person.setPhoto("");
        person.setAbout("Немного обо мне");
        person.setCity("Москва");
        person.setCountry("Россия");
        person.setMessagesPermission("All");
        person.setLastOnlineTime(System.currentTimeMillis() - 40);
        person.setBlocked(false);
        post.setAuthor(person);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId(1);
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);
        post.setComments(comments);

        postList.add(post);
        response.setData(postList);
        return response;
    }

    public GeneralResponse<Post> getPost(int id) {
        GeneralResponse<Post> response = new GeneralResponse<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        Post post = new Post();
        post.setId(1);
        post.setPostText("Вау! Это текст");
        post.setTitle("Это заголовок, Вау!");
        post.setBlocked(false);
        post.setLikes(20);
        Person person = new Person();
        person.setId(2);
        person.setFirstName("Борис");
        person.setLastName("Булкин");
        person.setRegDate(System.currentTimeMillis() - 567);
        person.setBirthDate(System.currentTimeMillis() - 1997);
        person.setEmail("gsdfhgsh@skdjfhskdj.ru");
        person.setPhone("9163332211");
        person.setPhoto("");
        person.setAbout("Немного обо мне");
        person.setCity("Москва");
        person.setCountry("Россия");
        person.setMessagesPermission("All");
        person.setLastOnlineTime(System.currentTimeMillis() - 40);
        person.setBlocked(false);
        post.setAuthor(person);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId(1);
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);
        post.setComments(comments);

        response.setData(post);
        return response;
    }

    public GeneralListResponse<Comment> getComments(int id) {
        GeneralListResponse<Comment> response = new GeneralListResponse<>();
        response.setError("");
        response.setTimestamp(System.currentTimeMillis());
        response.setTotal(10);
        response.setOffset(0);
        response.setPerPage(10);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId(1);
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);
        response.setData(comments);
        return response;
    }

}
