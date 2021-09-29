package application.controllers;

import application.models.requests.CommentRequest;
import application.models.requests.PostRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@WithUserDetails("vasy@yandex.ru")
public class PostControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostsController postsController;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    private static void setup(@Autowired DataSource dataSource) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data-for-post-controller-test.sql"));
        }
    }


    private void testPostSearch(ResultActions resultActions) throws Exception {

        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNumber())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.offset", is(0)))
                .andExpect(jsonPath("$.perPage", is(20)))
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].time", is(1625815590000L)))
                .andExpect(jsonPath("$.data[0].title", is("Язык програмирования \"C#\"")))
                .andExpect(jsonPath("$.data[0].comments").isEmpty())
                .andExpect(jsonPath("$.data[0].tags[0]", is("Bug")))
                .andExpect(jsonPath("$.data[0].likes", is(2)))
                .andExpect(jsonPath("$.data[0].my_like", is(1)))
                .andExpect(jsonPath("$.data[0].post_text",
                        is("<p>Много текста про C# <br>Ещё ного текста про C# <br>" +
                                "Много текста про C# <br>Тоже можно учить</p>")))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[0].author.id", is(7)))
                .andExpect(jsonPath("$.data[0].author.email", is("dmitriy@yandex.ru")))
                .andExpect(jsonPath("$.data[0].author.phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].author.about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[0].author.city", is("Тагил")))
                .andExpect(jsonPath("$.data[0].author.country", is("Россия")))
                .andExpect(jsonPath("$.data[0].author.first_name", is("Дминрий")))
                .andExpect(jsonPath("$.data[0].author.last_name", is("Скороход")))
                .andExpect(jsonPath("$.data[0].author.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].author.birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[0].author.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].author.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].author.is_blocked", is(false)));
    }

    @Test
    public void testSearchPostBySearchFieldByPostTitle() throws Exception {
        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Язык програмирования \"C#\"")));
    }

    @Test
    public void testSearchPostBySearchFieldByPostText() throws Exception {
        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Ещё ного текста про C#")));
    }

    @Test
    public void testSearchPostBySearchAndAuthorFields() throws Exception {
        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Язык")
                .param("author", "Скороход")));
    }

    @Test
    public void testSearchPostBySearchAndDateFields1() throws Exception {
        long currentTime = System.currentTimeMillis();

        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Тоже можно учить")
                .param("date_from", Long.toString(currentTime - 31536000000L))
                .param("date_to", Long.toString(currentTime))));
    }

    @Test
    public void testSearchPostBySearchAndDateFields2() throws Exception {
        long currentTime = System.currentTimeMillis();

        mockMvc.perform(get("/api/v1/post")
                .param("text", "Язык програмирования")
                .param("date_from", Long.toString(currentTime - 2678400000L))
                .param("date_to", Long.toString(currentTime)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNumber())
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.offset", is(0)))
                .andExpect(jsonPath("$.perPage", is(20)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testSearchPostByEmptyText() throws Exception {

        mockMvc.perform(get("/api/v1/post")
                .param("text", ""))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.path", is("/api/v1/post")))
                .andExpect(jsonPath("$.error_description",
                        is("The search bar must be at least 2 characters long")));
    }

    @Test
    public void testSearchPostBySearchAndTagFields() throws Exception {
        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Язык")
                .param("tags", "Bug")));
    }

    @Test
    public void testSearchPostByALLFields() throws Exception {
        long currentTime = System.currentTimeMillis();

        testPostSearch(mockMvc.perform(get("/api/v1/post")
                .param("text", "Язык")
                .param("tags", "Bug")
                .param("date_from", Long.toString(currentTime - 31536000000L))
                .param("date_to", Long.toString(currentTime))
                .param("author", "Скороход")));
    }

    @Test
    public void testGetPost() throws Exception {
        mockMvc.perform(get("/api/v1/post/15")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id", is(15)))
                .andExpect(jsonPath("$.data.time", is(1627200965000L)))
                .andExpect(jsonPath("$.data.author.id", is(4)))
                .andExpect(jsonPath("$.data.author.email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data.author.phone", is("89998887744")))
                .andExpect(jsonPath("$.data.author.about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data.author.city", is("Омск")))
                .andExpect(jsonPath("$.data.author.country", is("Россия")))
                .andExpect(jsonPath("$.data.author.first_name", is("Пётр")))
                .andExpect(jsonPath("$.data.author.last_name", is("Петров")))
                .andExpect(jsonPath("$.data.author.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.author.birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data.author.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.author.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.author.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.title", is("Логирование")))
                .andExpect(jsonPath("$.data.likes", is(0)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.comments").isEmpty())
                .andExpect(jsonPath("$.data.post_text", containsString("Очень важно и нужно")))
                .andExpect(jsonPath("$.data.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.tags", is(Arrays.asList("Bug", "Fix"))));
    }

    @Test
    public void testGetComments1() throws Exception {
        mockMvc.perform(get("/api/v1/post/1/comments")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.offset").isNotEmpty())
                .andExpect(jsonPath("$.perPage").isNotEmpty())
                .andExpect(jsonPath("$.data[0].author.first_name", is("Николай")))
                .andExpect(jsonPath("$.data[0].author.last_name", is("Аксёнов")))
                .andExpect(jsonPath("$.data[0].author.id", is(6)))
                .andExpect(jsonPath("$.data[0].author.photo").isEmpty())
                .andExpect(jsonPath("$.data[0].blocked", is(false)))
                .andExpect(jsonPath("$.data[0].comment_text",
                        is("Чтобы выбрать, надо попробовать и понять, что ближе")))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].likes", is(5)))
                .andExpect(jsonPath("$.data[0].my_like", is(0)))
                .andExpect(jsonPath("$.data[0].parentId", is(0)))
                .andExpect(jsonPath("$.data[0].postId", is("1")))
                .andExpect(jsonPath("$.data[0].time").isNotEmpty())

                .andExpect(jsonPath("$.data[0].sub_comments[0].author.first_name", is("Иван")))
                .andExpect(jsonPath("$.data[0].sub_comments[0].author.last_name", is("Иванов")))
                .andExpect(jsonPath("$.data[0].sub_comments[0].author.id", is(3)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].author.photo").isEmpty())
                .andExpect(jsonPath("$.data[0].sub_comments[0].blocked", is(false)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].comment_text",
                        is("Но с чего то надо начать")))
                .andExpect(jsonPath("$.data[0].sub_comments[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].likes", is(5)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].my_like", is(0)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].parentId", is(2)))
                .andExpect(jsonPath("$.data[0].sub_comments[0].postId", is("1")))

                .andExpect(jsonPath("$.data.length()", is(2)));
    }

    @Test
    public void testGetComments2() throws Exception {
        mockMvc.perform(get("/api/v1/post/undefined/comments")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.offset").isNotEmpty())
                .andExpect(jsonPath("$.perPage").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    public void testPostComment1() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCommentText("test comment");
        request.setParentId(null);

        mockMvc.perform(post("/api/v1/post/1/comments").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.photo").isEmpty())
                .andExpect(jsonPath("$.data.blocked", is(false)))
                .andExpect(jsonPath("$.data.comment_text",
                        is("test comment")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.likes", is(5)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.parentId").isEmpty())
                .andExpect(jsonPath("$.data.postId", is("1")))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.sub_comments").isEmpty());
    }

    @Test
    public void testPostComment2() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCommentText("test comment");
        request.setParentId(1);

        mockMvc.perform(post("/api/v1/post/undefined/comments").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.photo").isEmpty())
                .andExpect(jsonPath("$.data.blocked", is(false)))
                .andExpect(jsonPath("$.data.comment_text",
                        is("test comment")))
                .andExpect(jsonPath("$.data.id").isNumber())
                //тест на кол-во лайков не отрабатывает, считает неккоректно
                .andExpect(jsonPath("$.data.likes", is(5)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.parentId",is(1)))
                .andExpect(jsonPath("$.data.postId", is("3")))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.sub_comments[0].author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.sub_comments[0].author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.sub_comments[0].author.id", is(1)))
                .andExpect(jsonPath("$.data.sub_comments[0].author.photo").isEmpty())
                .andExpect(jsonPath("$.data.sub_comments[0].blocked", is(false)))
                .andExpect(jsonPath("$.data.sub_comments[0].comment_text",
                        is("test comment")))
                .andExpect(jsonPath("$.data.sub_comments[0].id", is(15)))
                .andExpect(jsonPath("$.data.sub_comments[0].likes", is(5)))
                .andExpect(jsonPath("$.data.sub_comments[0].my_like", is(0)))
                .andExpect(jsonPath("$.data.sub_comments[0].parentId", is(1)))
                .andExpect(jsonPath("$.data.sub_comments[0].postId", is("3")));
    }

    @Test
    public void testPostSubComment() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCommentText("test SubComment");
        request.setParentId(2);

        mockMvc.perform(post("/api/v1/post/1/comments").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.photo").isEmpty())
                .andExpect(jsonPath("$.data.blocked", is(false)))
                .andExpect(jsonPath("$.data.comment_text",
                        is("test SubComment")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.likes", is(5)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.parentId", is(2)))
                .andExpect(jsonPath("$.data.postId", is("1")))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.sub_comments[0].author.first_name", is("Иван")))
                .andExpect(jsonPath("$.data.sub_comments[0].author.last_name", is("Иванов")))
                .andExpect(jsonPath("$.data.sub_comments[0].author.id", is(3)))
                .andExpect(jsonPath("$.data.sub_comments[0].author.photo").isEmpty())
                .andExpect(jsonPath("$.data.sub_comments[0].blocked", is(false)))
                .andExpect(jsonPath("$.data.sub_comments[0].comment_text",
                        is("Но с чего то надо начать")))
                .andExpect(jsonPath("$.data.sub_comments[0].id", is(3)))
                .andExpect(jsonPath("$.data.sub_comments[0].likes", is(5)))
                .andExpect(jsonPath("$.data.sub_comments[0].my_like", is(0)))
                .andExpect(jsonPath("$.data.sub_comments[0].parentId", is(2)))
                .andExpect(jsonPath("$.data.sub_comments[0].postId", is("1")));
    }

    @Test
    public void testEditComment1() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCommentText("test edit comment");
        request.setParentId(null);

        mockMvc.perform(put("/api/v1/post/1/comments/11").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.photo").isEmpty())
                .andExpect(jsonPath("$.data.blocked", is(false)))
                .andExpect(jsonPath("$.data.comment_text",
                        is("test edit comment")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.likes", is(5)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.parentId").isEmpty())
                .andExpect(jsonPath("$.data.postId", is("1")))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.sub_comments").isEmpty());
    }

    @Test
    public void testEditComment2() throws Exception {
        CommentRequest request = new CommentRequest();
        request.setCommentText("test edit comment");
        request.setParentId(1);

        mockMvc.perform(put("/api/v1/post/undefined/comments/11").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.photo").isEmpty())
                .andExpect(jsonPath("$.data.blocked", is(false)))
                .andExpect(jsonPath("$.data.comment_text",
                        is("test edit comment")))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.likes", is(5)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.parentId").isEmpty())
                .andExpect(jsonPath("$.data.postId", is("1")))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.sub_comments").isEmpty());
    }

    @Test
    public void testEditPost() throws Exception {
        PostRequest request = new PostRequest();
        request.setTitle("test edit post title");
        request.setPostText("test edit post text");
        ArrayList<String> tags = new ArrayList<>();
        tags.add("test tag1");
        tags.add("test tag2");
        request.setTags(tags);

        mockMvc.perform(put("/api/v1/post/16").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(16)))
                .andExpect(jsonPath("$.data.time", is(1629464930000L)))
                .andExpect(jsonPath("$.data.author.id", is(1)))
                .andExpect(jsonPath("$.data.author.email", is("vasy@yandex.ru")))
                .andExpect(jsonPath("$.data.author.phone", is("89998887744")))
                .andExpect(jsonPath("$.data.author.about", is("Я Вася")))
                .andExpect(jsonPath("$.data.author.city", is("Москва")))
                .andExpect(jsonPath("$.data.author.country", is("Россия")))
                .andExpect(jsonPath("$.data.author.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.author.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.author.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.author.birth_date", is(964513590000L)))
                .andExpect(jsonPath("$.data.author.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.author.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.author.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.title", is("test edit post title")))
                .andExpect(jsonPath("$.data.likes", is(0)))
                .andExpect(jsonPath("$.data.my_like", is(0)))
                .andExpect(jsonPath("$.data.comments").isEmpty())
                .andExpect(jsonPath("$.data.post_text", containsString("test edit post text")))
                .andExpect(jsonPath("$.data.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.tags", is(Arrays.asList("test tag1", "test tag2"))));
    }

    @Test
    public void testDeletePost1() throws Exception {

        mockMvc.perform(delete("/api/v1/post/13")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message", is("ok")))
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }



    @Test
    public void testDeletePostComment1() throws Exception {

        mockMvc.perform(delete("/api/v1/post/11/comments/9")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(9)))
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    public void testDeletePostComment2() throws Exception {

        mockMvc.perform(delete("/api/v1/post/undefined/comments/10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(10)))
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }
}
