package application.service;

import application.dao.DaoPost;
import application.models.Post;
import application.models.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedsService {

    private final DaoPost daoPost;
    private final PostsService postsService;

    public List<PostDto> getFeed() {

        List<PostDto> postDtoList = new ArrayList<>();

        //Здесь может быть лучше получать только список ID а не все посты
        for (Post post : daoPost.getAll()){
            PostDto postDto = postsService.getPostDto(post.getId());
            postDtoList.add(postDto);
        }
        return postDtoList;
    }
}
