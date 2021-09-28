package application.controllers;

import application.models.dto.PersonDialogsDto;
import application.models.dto.UserIdsDto;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
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
import org.springframework.test.web.servlet.ResultMatcher;

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
@WithUserDetails("vasy@yandex.ru")
class DialogsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DialogsController dialogsController;

    @BeforeAll
    private static void setup(@Autowired DataSource dataSource) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("test-data-for-dialogs-controller-test.sql"));
        }
    }

    private static ResultMatcher matchPersonDialogsDto(String prefix, PersonDialogsDto person) {

        return ResultMatcher.matchAll(
                jsonPath(prefix + ".id", is(person.getId())),
                jsonPath(prefix + ".email", is(person.getEmail())),
                jsonPath(prefix + ".photo").doesNotExist(),
                jsonPath(prefix + ".first_name", is(person.getFirstName())),
                jsonPath(prefix + ".last_name", is(person.getLastName())),
                jsonPath(prefix + ".last_online_time", is(person.getLastOnlineTime()))
        );
    }

    @Test
    @WithUserDetails("homa@yandex.ru")
    void testGetDialogs() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.perPage", is(20)))
                .andExpect(jsonPath("$.offset", is(0)))
                .andExpect(jsonPath("$.data[0].unread_count", is(in(new Integer[]{0, 1}))))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].recipient.id", is(1)))
                .andExpect(jsonPath("$.data[0].recipient.email", is("vasy@yandex.ru")))
                .andExpect(jsonPath("$.data[0].recipient.photo").doesNotExist())
                .andExpect(jsonPath("$.data[0].recipient.first_name", is("Вася")))
                .andExpect(jsonPath("$.data[0].recipient.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data[0].recipient.last_online_time", is(1627200965049L)));
    }

    @Test
    void testCreateDialog1() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(4));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id", is(7)));
    }

    @Test
    void testCreateDialog2() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(2));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id", is(1)));
    }

    @Test
    @WithUserDetails("homa@yandex.ru")
    void testGetMessagesInDialog() throws Exception {

        PersonDialogsDto person1 = new PersonDialogsDto();
        person1.setLastName("Васичкин");
        person1.setFirstName("Вася");
        person1.setId(1);
        person1.setEmail("vasy@yandex.ru");
        person1.setLastOnlineTime(1627200965049L);
        PersonDialogsDto person2 = new PersonDialogsDto();
        person2.setLastName("Хомяков");
        person2.setFirstName("Хома");
        person2.setId(2);
        person2.setEmail("homa@yandex.ru");
        person2.setLastOnlineTime(1627200965049L);
        mockMvc.perform(get("/api/v1/dialogs/1/messages")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.perPage", is(20)))
                .andExpect(jsonPath("$.offset", is(0)))
                .andExpect(jsonPath("$.data[0].id", is(1)))
                .andExpect(jsonPath("$.data[0].time", is(1629463864000L)))
                .andExpect(matchPersonDialogsDto("$.data[0].author", person1))
                .andExpect(matchPersonDialogsDto("$.data[0].recipient", person2))
                .andExpect(jsonPath("$.data[0].isSentByMe", is(false)))
                .andExpect(jsonPath("$.data[0].message_text", is("Привет :)")))
                .andExpect(jsonPath("$.data[0].read_status", is("READ")))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].time", is(1629463924000L)))
                .andExpect(matchPersonDialogsDto("$.data[1].author", person2))
                .andExpect(matchPersonDialogsDto("$.data[1].recipient", person1))
                .andExpect(jsonPath("$.data[1].isSentByMe", is(true)))
                .andExpect(jsonPath("$.data[1].message_text", is("Привет!")))
                .andExpect(jsonPath("$.data[1].read_status", is("READ")));
        mockMvc.perform(get("/api/v1/dialogs/1/messages")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[2].read_status", is("READ")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void testGetCountUnreaded() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/unreaded")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.count", is(1)));
    }

    @Test
    @WithUserDetails("ilia@yandex.ru")
    void testDeleteDialog1() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id", is(5)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testDeleteDialog2() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/3")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/3")))
                .andExpect(jsonPath("$.error", is("unauthorized")))
                .andExpect(jsonPath("$.error_description",
                        containsString("You can't delete this dialog")));
    }

    @Test
    void testAddUserInDialog() throws Exception {

        UserIdsDto request = new UserIdsDto(List.of(3, 4));
        mockMvc.perform(put("/api/v1/dialogs/1/users").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_ids[0]", is(3)))
                .andExpect(jsonPath("$.data.user_ids[1]", is(4)));
    }

    @Test
    void testDeleteUsersInDialog() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/1/users/3,4,5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_ids[0]", is(3)))
                .andExpect(jsonPath("$.data.user_ids[1]", is(4)))
                .andExpect(jsonPath("$.data.user_ids[2]", is(5)));
    }

    @Test
    void testGetLinkToJoinDialog() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/users/invite")).andExpect(status().isOk());
    }

    @Test
    void testJoinDialogByLink() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/1/users/join")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("nik@yandex.ru")
    void testSendMessage1() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST");
        mockMvc.perform(post("/api/v1/dialogs/4/messages").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.message_text", is("TEST")))
                .andExpect(jsonPath("$.data.read_status", is("SENT")))
                .andExpect(jsonPath("$.data.author_id", is(6)))
                .andExpect(jsonPath("$.data.recipient_id", is(7)));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void testSendMessage2() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST");
        mockMvc.perform(post("/api/v1/dialogs/6/messages").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.message_text", is("TEST")))
                .andExpect(jsonPath("$.data.read_status", is("SENT")))
                .andExpect(jsonPath("$.data.author_id", is(10)))
                .andExpect(jsonPath("$.data.recipient_id", is(9)));
    }

    @Test
    @WithUserDetails("nik@yandex.ru")
    void testSendMessage3() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST");
        mockMvc.perform(post("/api/v1/dialogs/20/messages").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/20/messages")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Dialog with id = 20 is not exist")));
    }

    @Test
    @WithUserDetails("nik@yandex.ru")
    void testDeleteMessage1() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/4/messages/6")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message_id", is(6)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testDeleteMessage2() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/3/messages/5")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/3/messages/5")))
                .andExpect(jsonPath("$.error", is("unauthorized")))
                .andExpect(jsonPath("$.error_description",
                        containsString("You can't delete this message")));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    void testEditMessage1() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST3(2)");
        mockMvc.perform(put("/api/v1/dialogs/4/messages/7").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id", is(7)))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.message_text", is("TEST3(2)")))
                .andExpect(jsonPath("$.data.read_status", is("SENT")))
                .andExpect(jsonPath("$.data.author_id", is(7)))
                .andExpect(jsonPath("$.data.recipient_id", is(6)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testEditMessage2() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST3(2)");
        mockMvc.perform(put("/api/v1/dialogs/3/messages/5").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/3/messages/5")))
                .andExpect(jsonPath("$.error", is("unauthorized")))
                .andExpect(jsonPath("$.error_description",
                        containsString("You can't edit this message")));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    void testEditMessage3() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST3(2)");
        mockMvc.perform(put("/api/v1/dialogs/4/messages/20").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/4/messages/20")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        containsString("Message with id = 20 is not exist")));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    void testReadMessage1() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/4/messages/7/read")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void testReadMessage2() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/3/messages/5/read")).andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/3/messages/5/read")))
                .andExpect(jsonPath("$.error", is("unauthorized")))
                .andExpect(jsonPath("$.error_description",
                        containsString("You can't make this message read")));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    void testReadMessage3() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/4/messages/20/read")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.path", is("/api/v1/dialogs/4/messages/20/read")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        containsString("Message with id = 20 is not exist")));
    }

    @Test
    void testGetActivity() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.last_activity", is(1627200965049L)));
    }

    @Test
    void testChangeTypingStatus() throws Exception {

        mockMvc.perform(post("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.message", is("ok")));
    }
}