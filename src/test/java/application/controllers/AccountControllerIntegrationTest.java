package application.controllers;

import application.models.requests.*;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountController accountController;

    @BeforeAll
    private static void setup(@Autowired DataSource dataSource) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data-for-account-controller-test.sql"));
        }
    }

    @Test
    public void registerUserSuccess() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test1@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    public void registerUserEmailExistsFailed() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("The user with this email is already registered")));
    }

    @Test
    public void registerPasswordsAreNotEqualsFailed() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test2@test.ru");
        request.setPasswd2("87654321");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Passwords are not equals")));
    }


    @Test
    public void setPasswordSuccess() throws Exception {

        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("87654321");
        String token = "uniqueTokenForVasy";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    public void setPasswordNotValidFailed() throws Exception {

        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("1");
        String token = "uniqueToken";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Password is not valid.")));
    }

    @Test
    public void setEmailSuccess() throws Exception {

        ShiftEmailDtoRequest request = new ShiftEmailDtoRequest();
        request.setEmail("homa@yandex.ru");
        String token = "uniqueTokenForHoma";
        mockMvc.perform(put("/api/v1/account/email").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    public void recoverPasswordSuccess() throws Exception {

        RecoverPassDtoRequest request = new RecoverPassDtoRequest();
        request.setEmail("ivan@yandex.ru");
        mockMvc.perform(put("/api/v1/account/password/recovery")
                        .header("Request URL", "http://test.ru/")
                        .header("Referer", "").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    public void changeEmailSuccess() throws Exception {

        mockMvc.perform(put("/api/v1/account/shift-email")
                        .header("Request URL", "http://test.ru/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    public void getAccountNotificationsSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/account/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    public void setAccountNotifications() throws Exception {
        NotificationRequest request = new NotificationRequest();
        request.setNotificationType("POST");
        request.setEnable(true);
        mockMvc.perform(put("/api/v1/account/notifications").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}
