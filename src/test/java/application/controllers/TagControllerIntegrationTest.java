package application.controllers;

import application.models.requests.TagRequest;
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

import javax.sql.DataSource;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@WithUserDetails("vasy@yandex.ru")
public class TagControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagController tagController;

    @Autowired
    private DataSource dataSource;

    @Test
    void getTags() throws Exception {
        mockMvc.perform(get("/api/v1/tags").param("offset", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].tag", is("Java")));
    }

    @Test
    void setTag1() throws Exception {
        TagRequest request = new TagRequest();
        request.setTag("Kafka");
        mockMvc.perform(post("/api/v1/tags").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tag", is("Kafka")));
    }

    @Test
    void setTag2() throws Exception {
        TagRequest request = new TagRequest();
        request.setTag("Java");
        mockMvc.perform(post("/api/v1/tags").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tag", is("Java")));
    }

    @Test
    void deleteTag() throws Exception {
        mockMvc.perform(delete("/api/v1/tags").param("id", String.valueOf(5))).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}
