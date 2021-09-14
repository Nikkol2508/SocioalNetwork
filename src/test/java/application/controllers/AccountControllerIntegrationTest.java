package application.controllers;

import application.models.requests.RecoverPassDtoRequest;
import application.models.requests.RegistrationDtoRequest;
import application.models.requests.SetPasswordDtoRequest;
import application.models.requests.ShiftEmailDtoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@Sql(value = "/create-token-test-user.sql", executionPhase = BEFORE_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountController accountController;

    @Autowired
    private DataSource dataSource;

    @Test
    @Order(1)
    public void registerUserSuccess() throws Exception {
        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @Order(2)
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
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description")
                        .value("The user with this email is already registered"));
    }

    @Test
    @Order(3)
    public void registerPasswordsAreNotEqualsFailed() throws Exception {
        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("87654321");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description")
                        .value("Passwords are not equals"));
    }


    @Test
    @Order(4)
    public void setPasswordSuccess() throws Exception {
        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("87654321");
        String token = "uniqueToken";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @Order(5)
    public void setPasswordNotValidFailed() throws Exception {
        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("1");
        String token = "uniqueToken";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid_request"))
                .andExpect(jsonPath("$.error_description")
                        .value("Password is not valid."));
    }

    @Test
    @Order(6)
    public void setEmailSuccess() throws Exception {
        ShiftEmailDtoRequest request = new ShiftEmailDtoRequest();
        request.setEmail("test@test.ru");
        String token = "uniqueToken";
        mockMvc.perform(put("/api/v1/account/email").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @Order(7)
    public void recoverPasswordSuccess() throws Exception {
        RecoverPassDtoRequest request = new RecoverPassDtoRequest();
        request.setEmail("test@test.ru");
        mockMvc.perform(put("/api/v1/account/password/recovery")
                        .header("Request URL", "http://test.ru/")
                        .header("Referer", "").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @WithUserDetails("test@test.ru")
    @Order(8)
    public void changeEmailSuccess() throws Exception {
        mockMvc.perform(put("/api/v1/account/shift-email")
                        .header("Request URL", "http://test.ru/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    @WithUserDetails("test@test.ru")
    @Order(9)
    public void getAccountNotifications() throws Exception {
        mockMvc.perform(get("/api/v1/account/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
