package application.controllers;

import application.models.City;
import application.models.Country;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
class PlatformControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationController notificationController;

    @Test
    void testGetLanguages() throws Exception {
        mockMvc.perform(get("/api/v1/platform/languages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title", is("Русский")));
    }

    @Test
    void testGetCountry() throws Exception {
        mockMvc.perform(get("/api/v1/platform/countries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title", is("Россия")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testGetCity() throws Exception {
        mockMvc.perform(get("/api/v1/platform/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title", is("Абакан")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testSetCity() throws Exception {
        City request = new City();
        request.setTitle("cerfon");
        mockMvc.perform(post("/api/v1/platform/cities").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testSetCountry() throws Exception {
        Country request = new Country();
        request.setTitle("resc");
        mockMvc.perform(post("/api/v1/platform/countries").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}
