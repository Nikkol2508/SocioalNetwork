package application.controllers;

import application.models.requests.AuthDtoRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthController authController;

    @Autowired
    private DataSource dataSource;

    @Test
    public void loginSuccess() throws Exception {

        AuthDtoRequest request = new AuthDtoRequest();
        request.setEmail("vasy@yandex.ru");
        request.setPassword("12345678");
        mockMvc.perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(964513590000L)))
                .andExpect(jsonPath("$.data.email", is("vasy@yandex.ru")))
                .andExpect(jsonPath("$.data.phone", is("89998887744")))
                .andExpect(jsonPath("$.data.about", is("Я Вася")))
                .andExpect(jsonPath("$.data.city", is("Москва")))
                .andExpect(jsonPath("$.data.country", is("Россия")))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false)))
                .andExpect(jsonPath("$.data.token", notNullValue()));
    }

    @Test
    public void loginEmailNotExistsFailed() throws Exception {

        AuthDtoRequest request = new AuthDtoRequest();
        request.setEmail("emailnotexist@ya.ru");
        request.setPassword("12345678");
        mockMvc.perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Invalid username or password")));
    }

    @Test
    public void loginWrongPasswordFailed() throws Exception {

        AuthDtoRequest request = new AuthDtoRequest();
        request.setEmail("vasy@yandex.ru");
        request.setPassword("87654321");
        mockMvc.perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Invalid username or password")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    public void logoutSuccess() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}
