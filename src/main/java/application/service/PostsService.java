package application.service;

import application.dao.*;
import application.models.*;
import application.requests.CommentRequest;
import application.requests.LikeRequest;
import application.requests.TagRequest;
import application.responses.GeneralListResponse;
import application.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final DaoPost daoPost;
    private final DaoPerson daoPerson;
    private final DaoComment daoComment;
    private final DaoLike daoLike;
    private final DaoTag daoTag;
//    private final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    public PostDto getPostDto(int postId) {

        PostDto postDto = new PostDto();
        Post post = daoPost.get(postId);
        postDto.setId(post.getId());
        postDto.setPostText(post.getPostText());
        postDto.setTitle(post.getTitle());
        postDto.setBlocked(post.isBlocked());

        postDto.setLikes(daoLike.getCountLike(postId));

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

        postDto.setComments(getComments(postId));

        postDto.setTags(daoTag.getTagsByPostId(postId));

        return postDto;
    }

    public List<CommentDto> getComments(Integer postId) {
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (Comment comment : daoComment.getCommentsByPostId(postId)) {
            CommentDto commentDto = new CommentDto();
            commentDto.setParentId(comment.getParentId());
            commentDto.setCommentText(comment.getCommentText());
            commentDto.setId(comment.getId());
            commentDto.setPostId(String.valueOf(comment.getPostId()));
            commentDto.setTime(comment.getTime());
            commentDto.setAuthor(daoPerson.get(comment.getAuthorId()));
            commentDto.setBlocked(comment.isBlocked());
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    public GeneralResponse<PostDto> getPostResponse(int postId) {
        return new GeneralResponse<>(getPostDto(postId));
    }

    public GeneralListResponse<CommentDto> getCommentsResponse(Integer postId) {

        List<CommentDto> commentDtoList = new ArrayList<>();

        for (Comment comment : daoComment.getCommentsByPostId(postId)) {
            CommentDto commentDto = new CommentDto();
            commentDto.setParentId(comment.getParentId());
            commentDto.setCommentText(comment.getCommentText());
            commentDto.setId(comment.getId());
            commentDto.setPostId(String.valueOf(comment.getPostId()));
            commentDto.setTime(comment.getTime());
            commentDto.setAuthor(daoPerson.get(comment.getAuthorId()));
            commentDto.setBlocked(comment.isBlocked());
            commentDtoList.add(commentDto);
        }

        return new GeneralListResponse<>(commentDtoList);
    }

    public GeneralResponse<Comment> setComment(Integer postId, CommentRequest commentRequest) {
        Comment postComment = new Comment();
        postComment.setParentId(commentRequest.getParent_id());
        postComment.setCommentText(commentRequest.getComment_text());
        postComment.setPostId(postId);
        postComment.setTime(System.currentTimeMillis());
        postComment.setAuthorId(6);
        daoComment.save(postComment);
        return new GeneralResponse<>(postComment);
    }

    public GeneralResponse<LikeResponseDto> getLikes(int itemId, String type) {
        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(itemId);
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));
        return new GeneralResponse<>(likeResponseDto);
    }

    public GeneralResponse<Map<String, Boolean>> getLiked(int user_id, int itemId, String type) {
        Map<String, Boolean> isLiked = new HashMap<>();
        List<String> usersList = daoLike.getUsersLike(itemId);
        isLiked.put("likes", usersList.contains(String.valueOf(user_id)));

        return new GeneralResponse<>(isLiked);
    }

    public GeneralResponse<LikeResponseDto> setLikes(LikeRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentPerson = daoPerson.getByEmail(authentication.getName());
        if(getLiked(currentPerson.getId(), request.getItem_id(), "Post").getData().get("likes")) {

            Like like = new Like();
            like.setPostId(request.getItem_id());
            like.setTime(System.currentTimeMillis());
            like.setPersonId(currentPerson.getId());
            daoLike.save(like);
        }
        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(request.getItem_id());
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));

        return new GeneralResponse<>(likeResponseDto);
    }

    public GeneralListResponse<Tag> getTags() {
        return new GeneralListResponse<>(daoTag.getAll());
    }

    public GeneralResponse<Tag> setTag(TagRequest request) {
        Tag tag = new Tag();
        tag.setTag(request.getTag());
        daoTag.save(tag);
        //надо прикрепить к посту в tag2post
        return new GeneralResponse<>(tag);
    }

    public GeneralResponse<HashMap<String, String>> deleteTag(int tagId) {
// Здесь ДАО метод удаления
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "ok");
        return new GeneralResponse<>(response);
    }
}
