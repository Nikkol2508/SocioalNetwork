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
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.first_name").value("Вася"))
                .andExpect(jsonPath("$.data.last_name").value("Васичкин"))
                .andExpect(jsonPath("$.data.reg_date").value(1625127990000L))
                .andExpect(jsonPath("$.data.birth_date").value(964513590000L))
                .andExpect(jsonPath("$.data.email").value("vasy@yandex.ru"))
                .andExpect(jsonPath("$.data.phone").value("89998887744"))
                .andExpect(jsonPath("$.data[0].recipient.photo").doesNotExist())
                .andExpect(jsonPath("$.data.about").value("Я Вася"))
                .andExpect(jsonPath("$.data.city").value("Москва"))
                .andExpect(jsonPath("$.data.country").value("Россия"))
                .andExpect(jsonPath("$.data.messages_permission").value("ALL"))
                .andExpect(jsonPath("$.data.last_online_time").value(1627200965049L))
                .andExpect(jsonPath("$.data.is_blocked").value(false))
                .andExpect(jsonPath("$.data.token").isNotEmpty());
    }

    @Test
    public void loginEmailNotExistsFailed() throws Exception {

        AuthDtoRequest request = new AuthDtoRequest();
        request.setEmail("emailnotexist@ya.ru");
        request.setPassword("12345678");
        mockMvc.perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Invalid username or password"));
    }

    @Test
    public void loginWrongPasswordFailed() throws Exception {

        AuthDtoRequest request = new AuthDtoRequest();
        request.setEmail("vasy@yandex.ru");
        request.setPassword("87654321");
        mockMvc.perform(post("/api/v1/auth/login").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description").value("Invalid username or password"));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    public void logoutSuccess() throws Exception {

        mockMvc.perform(post("/api/v1/auth/logout")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }
}
