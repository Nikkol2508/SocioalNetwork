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
    private String subCommentParentId;

    public PostDto getPostDto(int postId) {

        Post post = daoPost.get(postId);
        int likes = daoLike.getCountLike(postId);
        Person person = daoPerson.get(post.getAuthorId());
        PersonDto author = PersonDto.fromPerson(person);
        List<CommentDto> comments = getComments(postId);
        List<String> tags = daoTag.getTagsByPostId(postId);
        PostDto postDto = PostDto.fromPost(post, likes, author, comments, tags);

        return postDto;
    }

    public List<CommentDto> getComments(Integer postId) {
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (Comment comment : daoComment.getCommentsByPostId(postId)) {
            Person person = daoPerson.get(comment.getAuthorId());
            List<CommentDto> subCommentList = getSubComments(comment.getId());
            CommentDto commentDto = CommentDto.fromComment(comment, person, subCommentList);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    public List<CommentDto> getSubComments(Integer parentId) {
        List<Comment> subComments = daoComment.getSubComment(parentId);
        List<CommentDto> subCommentsList = new ArrayList<>();

         if(subComments.size() > 0) {
            for(Comment subComment : subComments) {
                Person person = daoPerson.get(subComment.getAuthorId());
                CommentDto commentDto = CommentDto.fromComment(subComment, person, null);
                subCommentsList.add(commentDto);
            }
        }
        return subCommentsList;
    }

    public GeneralResponse<PostDto> getPostResponse(int postId) {

        return new GeneralResponse<>(getPostDto(postId));
    }

    public GeneralListResponse<CommentDto> getCommentsResponse(Integer postId) {

        return new GeneralListResponse<>(getComments(postId));
    }

    public GeneralListResponse getSubCommentsResponse() {


        return new GeneralListResponse<>(getComments(Integer.valueOf(subCommentParentId)));
    }

    public GeneralResponse<Comment> setComment(String postId, CommentRequest commentRequest) {
        Comment postComment = new Comment();
        postComment.setParentId(commentRequest.getParent_id());
        postComment.setCommentText(commentRequest.getComment_text());
        if(postId.equals("undefined")) {
            postId = String.valueOf(daoComment.getPostIdByCommentId(commentRequest.getParent_id()));
            subCommentParentId = postId;
        }
        postComment.setPostId(Integer.valueOf(postId));
        postComment.setTime(System.currentTimeMillis());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentPerson = daoPerson.getByEmail(authentication.getName());
        postComment.setAuthorId(currentPerson.getId());
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
        if(!getLiked(currentPerson.getId(), request.getItem_id(), request.getType()).getData().get("likes")) {

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

    public GeneralResponse<Map<String, String>> deleteLike(int itemId, String type) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Person currentPerson = daoPerson.getByEmail(authentication.getName());
        daoLike.delete(itemId, currentPerson.getId());
        HashMap<String, String> deleteLikeResponse = new HashMap<>();
        deleteLikeResponse.put("likes", "1");
        return new GeneralResponse<>(deleteLikeResponse);
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
