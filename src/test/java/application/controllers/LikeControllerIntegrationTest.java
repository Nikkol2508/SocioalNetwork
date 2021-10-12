package application.controllers;

import application.models.requests.LikeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
public class LikeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LikeController likeController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testGetLiked() throws Exception {
        mockMvc.perform(get("/api/v1/liked")
                        .param("user_id", String.valueOf(1))
                        .param("item_id", String.valueOf(4))
                        .param("type", "Post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes", is(true)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testGetLikes() throws Exception {
        mockMvc.perform(get("/api/v1/likes")
                        .param("item_id", String.valueOf(1))
                        .param("type", "Post"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes", is("2")))
                .andExpect(jsonPath("$.data.users[0]", is("1")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testSetLike() throws Exception {
        LikeRequest request = new LikeRequest();
        request.setType("Post");
        request.setItemId(2);
        mockMvc.perform(put("/api/v1/likes").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes", is("2")))
                .andExpect(jsonPath("$.data.users[0]", is("6")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testDeleteLike() throws Exception {
        mockMvc.perform(delete("/api/v1/likes")
                .param("item_id", String.valueOf(11))
                .param("type", "Post")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likes", is("1")));
    }
}
