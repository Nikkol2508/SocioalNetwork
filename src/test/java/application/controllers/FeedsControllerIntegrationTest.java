package application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@WithUserDetails("vasy@yandex.ru")
public class FeedsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FeedsController feedsController;

    @Autowired
    private DataSource dataSource;

    @Test
    void getFeedSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/feeds")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data[0].id").value(15))
                .andExpect(jsonPath("$.data[0].time").value(1627200965000L))
                .andExpect(jsonPath("$.data[0].author.id").value(4))
                .andExpect(jsonPath("$.data[0].author.email").value("petr@yandex.ru"))
                .andExpect(jsonPath("$.data[0].author.phone").value("89998887744"))
                .andExpect(jsonPath("$.data[0].author.about").value("Немного обо мне"))
                .andExpect(jsonPath("$.data[0].author.city").value("Омск"))
                .andExpect(jsonPath("$.data[0].author.country").value("Россия"))
                .andExpect(jsonPath("$.data[0].author.first_name").value("Пётр"))
                .andExpect(jsonPath("$.data[0].author.last_name").value("Петров"))
                .andExpect(jsonPath("$.data[0].author.reg_date").value(1625127990000L))
                .andExpect(jsonPath("$.data[0].author.birth_date").value(1625127990000L))
                .andExpect(jsonPath("$.data[0].author.messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data[0].author.last_online_time").value(1627200965049L))
                .andExpect(jsonPath("$.data[0].author.is_blocked").value(false))
                .andExpect(jsonPath("$.data[0].title").value("Логирование"))
                .andExpect(jsonPath("$.data[0].likes").value(0))
                .andExpect(jsonPath("$.data[0].my_like").value(0))
                .andExpect(jsonPath("$.data[0].comments").isEmpty())
                .andExpect(jsonPath("$.data[0].post_text", is("aa")));
    }
}