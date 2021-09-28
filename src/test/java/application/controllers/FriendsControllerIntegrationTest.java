package application.controllers;

import application.models.requests.FriendsDtoRequest;
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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

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
public class FriendsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountController accountController;

    @BeforeAll
    private static void setup(@Autowired DataSource dataSource) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data-for-friends-controller-test.sql"));
        }
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getFriends() throws Exception {
        FriendsDtoRequest request = new FriendsDtoRequest();
        mockMvc.perform(get("/api/v1/friends").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getRequests() throws Exception {
        FriendsDtoRequest request = new FriendsDtoRequest();
        mockMvc.perform(get("/api/v1/friends/request").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(3)))
                .andExpect(jsonPath("$.data[0].first_name", is("Иван")))
                .andExpect(jsonPath("$.data[0].last_name", is("Иванов")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[0].email", is("ivan@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void addFriendForId() throws Exception {
        mockMvc.perform(post("/api/v1/friends/5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void deleteFriendForId() throws Exception {
        mockMvc.perform(delete("/api/v1/friends/10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("petr@yandex.ru")
    void getRecommendations() throws Exception {
        FriendsDtoRequest request = new FriendsDtoRequest();
        mockMvc.perform(get("/api/v1/friends/recommendations").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].first_name", is("Вася")))
                .andExpect(jsonPath("$.data[0].last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(964513590000L)))
                .andExpect(jsonPath("$.data[0].email", is("vasy@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Вася")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }
}
