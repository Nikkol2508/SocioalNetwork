package application.controllers;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.util.Arrays;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@WithUserDetails("vasy@yandex.ru")
class FeedsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeedsController feedsController;

    @Autowired
    private DataSource dataSource;

    @Test
    void testGetFeed() throws Exception {

        mockMvc.perform(get("/api/v1/feeds")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id", is(15)))
                .andExpect(jsonPath("$.data[0].time", is(1627200965000L)))
                .andExpect(jsonPath("$.data[0].author.id", is(4)))
                .andExpect(jsonPath("$.data[0].author.email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[0].author.phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].author.about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[0].author.city", is("Омск")))
                .andExpect(jsonPath("$.data[0].author.country", is("Россия")))
                .andExpect(jsonPath("$.data[0].author.first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[0].author.last_name", is("Петров")))
                .andExpect(jsonPath("$.data[0].author.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].author.birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[0].author.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].author.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].author.is_blocked", is(false)))
                .andExpect(jsonPath("$.data[0].title", is("Логирование")))
                .andExpect(jsonPath("$.data[0].likes", is(0)))
                .andExpect(jsonPath("$.data[0].my_like", is(0)))
                .andExpect(jsonPath("$.data[0].comments").isEmpty())
                .andExpect(jsonPath("$.data[0].post_text", containsString("Очень важно и нужно")))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[0].tags", is(Arrays.asList("Bug", "Fix"))))
                .andExpect(jsonPath("$.data.length()", is(15)));
    }
}