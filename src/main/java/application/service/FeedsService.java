package application.service;

import application.dao.DaoPerson;
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
    private final DaoPerson daoPerson;

    public List<PostDto> getFeed() {

        List<PostDto> postDtoList = new ArrayList<>();

        //Здесь может быть лучше получать только список ID а не все посты
        for (Post post : daoPost.getAll()) {
            PostDto postDto = postsService.getPostDto(post.getId());
            if (daoPerson.isPersonBlockedByAnotherPerson(daoPerson.getAuthPerson().getId(), postDto.getAuthor().getId())
                    || postDto.getAuthor().isBlocked()) {
                continue;
            }
            postDtoList.add(postDto);
        }
        return postDtoList;
    }
}
