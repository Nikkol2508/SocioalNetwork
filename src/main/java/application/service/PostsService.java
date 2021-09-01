package application.service;

import application.dao.*;
import application.models.*;
import application.models.dto.*;
import application.models.requests.CommentRequest;
import application.models.requests.LikeRequest;
import application.models.requests.PostRequest;
import application.models.requests.TagRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostsService {

    private final DaoPost daoPost;
    private final DaoPerson daoPerson;
    private final DaoComment daoComment;
    private final DaoLike daoLike;
    private final DaoTag daoTag;
    private String undefinedPostId;

    public PostDto getPostDto(int postId) {

        Post post = daoPost.getById(postId);
        int likes = daoLike.getCountLike(postId, "Post");
        Person person = daoPerson.getById(post.getAuthorId());
        PersonDto author = PersonDto.fromPerson(person);
        List<CommentDto> comments = getComments(postId);
        List<String> tags = daoTag.getTagsByPostId(postId);
        int myLike = daoLike.getMyLike(postId, "Post", daoPerson.getAuthPerson().getId());

        return PostDto.fromPost(post, likes, author, comments, tags, myLike);
    }

    public List<CommentDto> getComments(Integer postId) {
        List<CommentDto> commentDtoList = new ArrayList<>();

        for (Comment comment : daoComment.getCommentsByPostId(postId)) {
            Person person = daoPerson.getById(comment.getAuthorId());
            List<CommentDto> subCommentList = getSubComments(comment.getId());
            int myLike = daoLike.getMyLike(comment.getId(), "Comment", daoPerson.getAuthPerson().getId());
            CommentDto commentDto = CommentDto.fromComment(comment, person, subCommentList, myLike);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }

    public List<CommentDto> getSubComments(Integer parentId) {
        List<Comment> subComments = daoComment.getSubComment(parentId);
        List<CommentDto> subCommentsList = new ArrayList<>();

        if (subComments.size() > 0) {
            for (Comment subComment : subComments) {
                Person person = daoPerson.getById(subComment.getAuthorId());
                int myLike = daoLike.getMyLike(subComment.getId(), "Comment", daoPerson.getAuthPerson().getId());
                CommentDto commentDto = CommentDto.fromComment(subComment, person, null, myLike);
                subCommentsList.add(commentDto);
            }
        }
        return subCommentsList;
    }

    public GeneralResponse<PostDto> getPostResponse(int postId) {
        return new GeneralResponse<>(getPostDto(postId));
    }

    public GeneralListResponse<CommentDto> getCommentsResponse(String postId) {

        if (postId.equals("undefined")) {
            postId = undefinedPostId;
            undefinedPostId = null;
        }
        return new GeneralListResponse<>(getComments(Integer.valueOf(postId)));
    }

    public GeneralResponse<CommentDto> setComment(String postId, CommentRequest commentRequest) {
        Comment postComment = new Comment();
        postComment.setParentId(commentRequest.getParentId());
        postComment.setCommentText(commentRequest.getCommentText());
        if (postId.equals("undefined")) {
            postId = String.valueOf(daoComment.getPostIdByCommentId(commentRequest.getParentId()));
            undefinedPostId = postId;
        }
        postComment.setPostId(Integer.valueOf(postId));
        postComment.setTime(System.currentTimeMillis());
        Person currentPerson = daoPerson.getAuthPerson();
        postComment.setAuthorId(currentPerson.getId());
        daoComment.save(postComment);
        int likes = daoLike.getCountLike(postComment.getId(), "Comment");

        CommentDto commentDto = CommentDto.fromComment(postComment, currentPerson,
                getSubComments(commentRequest.getParentId()), likes);
        return new GeneralResponse<>(commentDto);
    }

    public GeneralResponse<CommentDto> editComment(CommentRequest commentRequest, String postId, int comment_id) {
        Comment postComment = daoComment.getById(comment_id);
        postComment.setCommentText(commentRequest.getCommentText());
        if (postComment.getParentId() == 0) {
            postComment.setParentId(null);
        }
        if (postId.equals("undefined")) {
            postId = String.valueOf(daoComment.getPostIdByCommentId(postComment.getId()));
            undefinedPostId = postId;
        }
        daoComment.update(postComment);
        int likes = daoLike.getCountLike(postComment.getId(), "Comment");
        CommentDto commentDto = CommentDto.fromComment(postComment, daoPerson.getAuthPerson(),
                getSubComments(postComment.getParentId()), likes);
        return new GeneralResponse<>(commentDto);
    }

    public GeneralResponse<HashMap<String, Integer>> deleteComment(String postId, int comment_id) {
        List<CommentDto> subComments = getSubComments(comment_id);
        if (subComments.size() != 0) {
            for (CommentDto subComment : subComments) {
                daoComment.delete(subComment.getId());
            }
        }
        if (postId.equals("undefined")) {
            postId = String.valueOf(daoComment.getPostIdByCommentId(comment_id));
            undefinedPostId = postId;
        }
        HashMap<String, Integer> response = new HashMap<>();
        response.put("id", comment_id);
        daoComment.delete(comment_id);
        return new GeneralResponse<>(response);
    }

    public GeneralResponse<LikeResponseDto> getLikes(int itemId, String type) {
        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(itemId, type);
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));
        return new GeneralResponse<>(likeResponseDto);
    }

    public GeneralResponse<Map<String, Boolean>> getLiked(int user_id, int itemId, String type) {
        Map<String, Boolean> isLiked = new HashMap<>();
        List<String> usersList = daoLike.getUsersLike(itemId, type);
        isLiked.put("likes", usersList.contains(String.valueOf(user_id)));

        return new GeneralResponse<>(isLiked);
    }

    public GeneralResponse<LikeResponseDto> setLikes(LikeRequest request) {

        Person currentPerson = daoPerson.getAuthPerson();

        if (!getLiked(currentPerson.getId(), request.getItem_id(), request.getType()).getData().get("likes")) {

            Like like = new Like();
            like.setItemId(request.getItem_id());
            like.setTime(System.currentTimeMillis());
            like.setPersonId(currentPerson.getId());
            like.setType(request.getType());
            daoLike.save(like);
        }

        if (request.getType().equals("Comment")) {
            undefinedPostId = String.valueOf(daoComment.getPostIdByCommentId(request.getItem_id()));
        } else undefinedPostId = String.valueOf(request.getItem_id());

        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(request.getItem_id(), request.getType());
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));

        return new GeneralResponse<>(likeResponseDto);
    }

    public GeneralResponse<Map<String, String>> deleteLike(int itemId, String type) {
        Person currentPerson = daoPerson.getAuthPerson();
        daoLike.delete(itemId, type, currentPerson.getId());
        if (type.equals("Comment")) {
            undefinedPostId = String.valueOf(daoComment.getPostIdByCommentId(itemId));
        } else undefinedPostId = String.valueOf(itemId);
        HashMap<String, String> deleteLikeResponse = new HashMap<>();
        deleteLikeResponse.put("likes", "1");
        return new GeneralResponse<>(deleteLikeResponse);
    }

    public GeneralListResponse<Tag> getTags() {
        return new GeneralListResponse<>(daoTag.getAll());
    }

    public GeneralResponse<Tag> setTag(TagRequest request) {
        Tag tag = daoTag.findTagByName(request.getTag());
        if (tag.equals(null)) {
            daoTag.save(request.getTag());
            tag = daoTag.findTagByName(request.getTag());
        }
        return new GeneralResponse<>(tag);
    }

    public void attachTag2Post(int tagId, int postId) {
        daoTag.attachTag2Post(tagId, postId);
    }

    public void detachTag2Post(int tagId, int postId) {
        daoTag.detachTag2Post(tagId, postId);
    }

    public GeneralResponse<HashMap<String, String>> deleteTag(int tagId) {
        daoTag.delete(tagId);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "ok");
        return new GeneralResponse<>(response);
    }

    public GeneralListResponse<PostDto> getPosts(String text, String author, Long dateFrom, Long dateTo) {

        val listPersonsId = daoPerson.getPersonsByFirstNameSurname(author)
                .stream()
                .map(Person::getId)
                .collect(Collectors.toList());

        val posts = listPersonsId.stream()
                .map(item -> getPosts(text, item, dateFrom, dateTo))
                .flatMap(List::stream).collect(Collectors.toList());

        return new GeneralListResponse<>(posts
                .stream()
                .map(item -> getPostDto(item.getId()))
                .collect(Collectors.toList()));
    }

    private List<Post> getPosts(String text, Integer authorId, Long dateFrom, Long dateTo) {
        return daoPost.getPosts(text, authorId, dateFrom, dateTo);
    }

    public GeneralResponse<PostDto> editPost(PostRequest request, int postId) {
        Post post = daoPost.getById(postId);
        post.setPostText(request.getPostText());
        post.setTitle(request.getTitle());
        List<String> oldTagList = daoTag.getTagsByPostId(postId);
        for (String tag : oldTagList) {
            daoTag.detachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        for (String tag : request.getTags()) {
            daoTag.save(tag);
            daoTag.attachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        daoPost.update(post);
        return new GeneralResponse<>(getPostDto(postId));
    }

    public GeneralResponse<MessageResponseDto> deletePost(int postId) {
        for (String tag : daoTag.getTagsByPostId(postId)) {
            daoTag.detachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        for (Like like : daoLike.getLikeByPost(postId, "Post")) {
            daoLike.delete(postId, "Post", like.getPersonId());
        }
        List<Comment> comments = daoComment.getCommentsByPostId(postId);
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                List<Comment> subComments = daoComment.getSubComment(comment.getId());
                if (subComments.size() != 0) {
                    for (Comment subComment : subComments) {
                        for (Like likeOnSubComment : daoLike.getLikeByPost(subComment.getId(), "Comment")) {
                            daoLike.delete(subComment.getId(), "Comment", likeOnSubComment.getPersonId());
                        }
                        daoComment.delete(subComment.getId());
                    }
                }
                for (Like likeOnComment : daoLike.getLikeByPost(comment.getId(), "Comment")) {
                    daoLike.delete(comment.getId(), "Comment", likeOnComment.getPersonId());
                }
                daoComment.delete(comment.getId());
            }
        }
        daoPost.delete(postId);
        return new GeneralResponse<>(new MessageResponseDto("ok"));
    }
}
