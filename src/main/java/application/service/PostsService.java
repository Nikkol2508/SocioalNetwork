package application.service;

import application.dao.*;
import application.models.*;
import application.models.dto.*;
import application.models.requests.CommentRequest;
import application.models.requests.LikeRequest;
import application.models.requests.PostRequest;
import application.models.requests.TagRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsService {

    private final DaoPost daoPost;
    private final DaoPerson daoPerson;
    private final DaoComment daoComment;
    private final DaoLike daoLike;
    private final DaoTag daoTag;
    private final DaoNotification daoNotification;
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

        if (!subComments.isEmpty()) {
            for (Comment subComment : subComments) {
                Person person = daoPerson.getById(subComment.getAuthorId());
                int myLike = daoLike.getMyLike(subComment.getId(), "Comment", daoPerson.getAuthPerson().getId());
                CommentDto commentDto = CommentDto.fromComment(subComment, person, null, myLike);
                subCommentsList.add(commentDto);
            }
        }
        return subCommentsList;
    }

    public PostDto getPostResponse(int postId) {

        return getPostDto(postId);
    }

    public List<CommentDto> getCommentsResponse(String postId) {

        if (postId.equals("undefined")) {
            undefinedPostId = "0";
            postId = undefinedPostId;

        }
        return getComments(Integer.valueOf(postId));
    }

    public CommentDto setComment(String postId, CommentRequest commentRequest) {

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
        int comId = daoComment.save(postComment);
        Person person = daoPerson.getById(daoPost.getById(postComment.getPostId()).getAuthorId());
        daoNotification.addNotification(person.getId(), daoPerson.getAuthPerson().getId(), postComment.getTime(),
                comId, person.getEmail(), postComment.getParentId() == null
                        ? NotificationType.POST_COMMENT.toString() : NotificationType.COMMENT_COMMENT.toString(),
                postComment.getCommentText());
        int likes = daoLike.getCountLike(postComment.getId(), "Comment");

        return CommentDto.fromComment(postComment, currentPerson, getSubComments(commentRequest.getParentId()), likes);
    }

    public CommentDto editComment(CommentRequest commentRequest, String postId, int commentId) {

        Comment postComment = daoComment.getById(commentId);
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
        return CommentDto.fromComment(postComment, daoPerson.getAuthPerson(), getSubComments(postComment.getParentId()),
                likes);
    }

    public Map<String, Integer> deleteComment(String postId, int commentId) {

        List<CommentDto> subComments = getSubComments(commentId);
        if (!subComments.isEmpty()) {
            for (CommentDto subComment : subComments) {
                daoComment.delete(subComment.getId());
            }
        }
        if (postId.equals("undefined")) {
            postId = String.valueOf(daoComment.getPostIdByCommentId(commentId));
            undefinedPostId = postId;
        }
        HashMap<String, Integer> response = new HashMap<>();
        response.put("id", commentId);
        daoComment.delete(commentId);
        return response;
    }

    public LikeResponseDto getLikes(int itemId, String type) {

        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(itemId, type);
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));
        return likeResponseDto;
    }

    public Map<String, Boolean> getLiked(int userId, int itemId, String type) {

        Map<String, Boolean> isLiked = new HashMap<>();
        List<String> usersList = daoLike.getUsersLike(itemId, type);
        isLiked.put("likes", usersList.contains(String.valueOf(userId)));

        return isLiked;
    }

    public LikeResponseDto setLikes(LikeRequest request) {

        Person currentPerson = daoPerson.getAuthPerson();

        if (!getLiked(currentPerson.getId(), request.getItemId(), request.getType()).get("likes")) {
            Like like = new Like();
            like.setItemId(request.getItemId());
            like.setTime(System.currentTimeMillis());
            like.setPersonId(currentPerson.getId());
            like.setType(request.getType());
            daoLike.save(like);
        }
        undefinedPostId = request.getType().equals("Comment") ? String.valueOf(daoComment
                .getPostIdByCommentId(request.getItemId())) : String.valueOf(request.getItemId());
        LikeResponseDto likeResponseDto = new LikeResponseDto();
        List<String> userList = daoLike.getUsersLike(request.getItemId(), request.getType());
        likeResponseDto.setUsers(userList);
        likeResponseDto.setLikes(String.valueOf(userList.size()));

        return likeResponseDto;
    }

    public Map<String, String> deleteLike(int itemId, String type) {

        Person currentPerson = daoPerson.getAuthPerson();
        daoLike.delete(itemId, type, currentPerson.getId());
        undefinedPostId = type.equals("Comment") ? String.valueOf(daoComment.getPostIdByCommentId(itemId))
                : String.valueOf(itemId);
        HashMap<String, String> deleteLikeResponse = new HashMap<>();
        deleteLikeResponse.put("likes", "1");
        return deleteLikeResponse;
    }

    public List<Tag> getTags() {

        return daoTag.getAll();
    }

    public boolean saveTag(String tagName) {

        Tag tag = daoTag.findTagByName(tagName);
        if (tag == null) {
            daoTag.save(tagName);
            return true;
        } else {
            return false;
        }
    }

    public Tag setTag(TagRequest request) {

        saveTag(request.getTag());
        return daoTag.findTagByName(request.getTag());
    }

    public Map<String, String> deleteTag(int tagId) {

        daoTag.delete(tagId);
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "ok");
        return response;
    }

    public List<PostDto> searchPosts(String text, String author, Long dateFrom, Long dateTo, List<String> tags) {

        List<Post> posts = daoPost.getPosts(text, author, dateFrom, dateTo, tags);
        return posts.stream().map(item -> getPostDto(item.getId())).collect(Collectors.toList());
    }

    public PostDto editPost(PostRequest request, int postId) {

        Post post = daoPost.getById(postId);
        post.setPostText(request.getPostText());
        post.setTitle(request.getTitle());
        List<String> oldTagList = daoTag.getTagsByPostId(postId);
        for (String tag : oldTagList) {
            daoTag.detachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        attachTags2Post(request.getTags(), postId);
        daoPost.update(post);

        return getPostDto(postId);
    }

    public void attachTags2Post(List<String> tags, int postId) {
        Set<String> setTags = new HashSet<>(tags);
        for (String tag : setTags) {
            saveTag(tag);
            daoTag.attachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
    }

    public MessageResponseDto deletePost(int postId) {

        for (String tag : daoTag.getTagsByPostId(postId)) {
            daoTag.detachTag2Post(daoTag.findTagByName(tag).getId(), postId);
        }
        for (Like like : daoLike.getLikeByPost(postId, "Post")) {
            daoLike.delete(postId, "Post", like.getPersonId());
        }
        List<Comment> comments = daoComment.getCommentsByPostId(postId);
        if (!comments.isEmpty()) {
            for (Comment comment : comments) {
                List<Comment> subComments = daoComment.getSubComment(comment.getId());
                if (!subComments.isEmpty()) {
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
        return new MessageResponseDto();
    }
}
