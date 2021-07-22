package application.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DaoPost {

//
//    public Optional<Post> get(int id) {
//        Optional<Post> postOptional = null;
//        return postOptional;
//    }
//
//    public List<Post> getAll() {
//       Iterable<Post> postIterable = null;
//        ArrayList<Post> postList = new ArrayList<>();
//        postIterable.forEach(postList::add);
//        return postList;
//    }
//
//    public int save(Post post,int authorId, String text, String title, Date time, Boolean isBlocked) {
//        post.setTitle(title);
//        post.setPostText(text);
//        post.setTime(time);
//        post.setAuthorId(authorId);
//        post.setBlocked(isBlocked);
//        Post newPost = null;
//        return post.getId();
//    }
//
//    public void update(@PathVariable int id, String text, String title) {
//        Optional<Post> postOptional = null;
//        Post changedPost = postOptional.get();
//        if (!postOptional.isPresent()) {
//            System.out.println("not found");
//        } else {
//            changedPost.setPostText(text);
//            changedPost.setTitle(title);
//            changedPost.setDate();
//            postRepository.save(changedPost);
//        }
//    }
//
//    public void delete(int id) {
//        postRepository.deleteById(id);
//    }
//
//    public void deleteGoalList() {
//        postRepository.deleteAll();
//    }
}
