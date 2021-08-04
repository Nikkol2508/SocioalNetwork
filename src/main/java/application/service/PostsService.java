package application.service;

import application.dao.DaoPerson;
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
public class PostsService {

    private final DaoPost daoPost;
    private final DaoPerson daoPerson;

    public GeneralListResponse<PostDto> getPosts() {

        List<PostDto> postList = new ArrayList<>();
        PostDto post = new PostDto();
        post.setId(1);
        post.setPostText("Вау! Это текст");
        post.setTitle("Это заголовок, Вау!");
        post.setBlocked(false);
        post.setLikes(20);
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
        personDto.setCity("Москва");
        personDto.setCountry("Россия");
        personDto.setMessagesPermission("All");
        personDto.setLastOnlineTime(System.currentTimeMillis() - 40);
        personDto.setBlocked(false);
        post.setAuthor(personDto);
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId("");
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);
        post.setComments(comments);

        postList.add(post);
        return new GeneralListResponse<>(postList);
    }

    public GeneralResponse<PostDto> getPost(int id) {

        PostDto post = new PostDto();
        Post post4Id = daoPost.getPostById(id);
        post.setId(post4Id.getId());
        post.setPostText(post4Id.getPostText());
        post.setTitle(post4Id.getTitle());
        post.setTime(post4Id.getTime());
        post4Id.setAuthorId(daoPerson.get(2).getId());

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId("");
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);
        post.setComments(comments);

        return new GeneralResponse<>(post);
    }

    public GeneralListResponse<Comment> getComments(int id) {

        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setParentId(0);
        comment.setCommentText("полезно");
        comment.setId(1);
        comment.setPostId("");
        comment.setTime(System.currentTimeMillis());
        comment.setAuthorId(1);
        comment.setBlocked(false);
        comments.add(comment);

        return new GeneralListResponse<>(comments);
    }

}
