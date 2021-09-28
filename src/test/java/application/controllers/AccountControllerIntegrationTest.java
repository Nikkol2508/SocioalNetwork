package application.controllers;

import application.models.NotificationType;
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
import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
class AccountControllerIntegrationTest {

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
    void testRegister1() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test1@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    void testRegister2() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("The user with this email is already registered")));
    }

    @Test
    void testRegister3() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test2@test.ru");
        request.setPasswd2("87654321");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Passwords are not equals")));
    }

    @Test
    void testRegister4() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("notValidEmail");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Email is not valid")));
    }

    @Test
    void testRegister5() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("1234567");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("The password length must not be less than 8 characters")));
    }

    @Test
    void testRegister6() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("F");
        request.setLastName("Last");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("First name has invalid characters or length is not between 2 and 50")));
    }

    @Test
    void testRegister7() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("L");
        request.setCode("1234");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Last name has invalid characters or length is not between 2 and 50")));
    }

    @Test
    void testRegister8() throws Exception {

        RegistrationDtoRequest request = new RegistrationDtoRequest();
        request.setEmail("test@test.ru");
        request.setPasswd2("12345678");
        request.setPasswd1("12345678");
        request.setFirstName("First");
        request.setLastName("Last");
        request.setCode("1");
        mockMvc.perform(post("/api/v1/account/register").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/register")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("The code must consist of 4 digits")));
    }

    @Test
    void testSetPassword1() throws Exception {

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
    void testSetPassword2() throws Exception {

        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("1");
        String token = "uniqueToken";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/password/set")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("The password length must not be less than 8 characters")));
    }

    @Test
    void testSetPassword3() throws Exception {

        SetPasswordDtoRequest request = new SetPasswordDtoRequest();
        request.setPassword("12345678");
        String token = "tokenIsNotExist";
        mockMvc.perform(put("/api/v1/account/password/set").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/password/set")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("This link is no longer active, check your mail to find actual link")));
    }

    @Test
    void testSetEmail1() throws Exception {

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
    void testSetEmail2() throws Exception {

        ShiftEmailDtoRequest request = new ShiftEmailDtoRequest();
        request.setEmail("homa@yandex.ru");
        String token = "tokenIsNotExist";
        mockMvc.perform(put("/api/v1/account/email").header("Referer", "=" + token)
                        .content(objectMapper.writeValueAsString(request)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/email")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("This link is no longer active, check your mail to find actual link")));
    }

    @Test
    void testRecoverPassword1() throws Exception {

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
    void testRecoverPassword2() throws Exception {

        RecoverPassDtoRequest request = new RecoverPassDtoRequest();
        request.setEmail("ivan@yandex.ru");
        mockMvc.perform(put("/api/v1/account/password/recovery")
                        .header("Request URL", "http://test.ru/")
                        .header("Referer", "http://test.ru/?settings").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    void testRecoverPassword3() throws Exception {

        RecoverPassDtoRequest request = new RecoverPassDtoRequest();
        request.setEmail("emailNotExist@test.ru");
        mockMvc.perform(put("/api/v1/account/password/recovery")
                        .header("Request URL", "http://test.ru/")
                        .header("Referer", "http://test.ru/").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/account/password/recovery")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Person with email: " + request.getEmail() + " cannot be found")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void testChangeEmail() throws Exception {

        mockMvc.perform(put("/api/v1/account/shift-email")
                        .header("Request URL", "http://test.ru/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void testGetAccountNotificationsSettings() throws Exception {

        mockMvc.perform(get("/api/v1/account/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data[?(@.type=='POST_COMMENT')].enable", is(List.of(true))))
                .andExpect(jsonPath("$.data[?(@.type=='COMMENT_COMMENT')].enable", is(List.of(true))))
                .andExpect(jsonPath("$.data[?(@.type=='FRIEND_REQUEST')].enable", is(List.of(true))))
                .andExpect(jsonPath("$.data[?(@.type=='MESSAGE')].enable", is(List.of(true))))
                .andExpect(jsonPath("$.data[?(@.type=='FRIEND_BIRTHDAY')].enable", is(List.of(true))))
                .andExpect(jsonPath("$.data[?(@.type=='POST')].enable", is(List.of(true))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void testSetAccountNotificationsSettings() throws Exception {

        NotificationRequest request = new NotificationRequest();
        request.setNotificationType(NotificationType.POST);
        request.setEnable(true);
        mockMvc.perform(put("/api/v1/account/notifications").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}
