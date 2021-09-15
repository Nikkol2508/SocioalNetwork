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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@WithUserDetails("vasy@yandex.ru")
public class DialogsControllerIntegrationTest {

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
                jsonPath(prefix + ".id").value(person.getId()),
                jsonPath(prefix + ".email").value(person.getEmail()),
                jsonPath(prefix + ".photo").doesNotExist(),
                jsonPath(prefix + ".first_name").value(person.getFirstName()),
                jsonPath(prefix + ".last_name").value(person.getLastName()),
                jsonPath(prefix + ".last_online_time").value(person.getLastOnlineTime())
        );
    }

    @Test
    @WithUserDetails("homa@yandex.ru")
    public void getDialogsSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.perPage").value(20))
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$..data[0].unread_count").value(1))
                .andExpect(jsonPath("$..data[0].id").value(1))
                .andExpect(jsonPath("$..data[0].recipient.id").value(1))
                .andExpect(jsonPath("$..data[0].recipient.email").value("vasy@yandex.ru"))
                .andExpect(jsonPath("$..data[0].recipient.photo").doesNotExist())
                .andExpect(jsonPath("$..data[0].recipient.first_name").value("Вася"))
                .andExpect(jsonPath("$..data[0].recipient.last_name").value("Васичкин"))
                .andExpect(jsonPath("$..data[0].recipient.last_online_time").value(1627200965049L));
    }

    @Test
    public void createDialogSuccess() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(4));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(6));
    }

    @Test
    public void createExistingDialogSuccess() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(2));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithUserDetails("homa@yandex.ru")
    public void getMessagesInDialogSuccess() throws Exception {
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
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total").value(3))
                .andExpect(jsonPath("$.perPage").value(20))
                .andExpect(jsonPath("$.offset").value(0))
                .andExpect(jsonPath("$..data[0].id").value(1))
                .andExpect(jsonPath("$..data[0].time").value(1629463864000L))
                .andExpect(matchPersonDialogsDto("$..data[0].author", person1))
                .andExpect(matchPersonDialogsDto("$..data[0].recipient", person2))
                .andExpect(jsonPath("$..data[0].isSentByMe").value(false))
                .andExpect(jsonPath("$..data[0].message_text").value("Привет :)"))
                .andExpect(jsonPath("$..data[0].read_status").value("READ"))
                .andExpect(jsonPath("$..data[1].id").value(2))
                .andExpect(jsonPath("$..data[1].time").value(1629463924000L))
                .andExpect(matchPersonDialogsDto("$..data[1].author", person2))
                .andExpect(matchPersonDialogsDto("$..data[1].recipient", person1))
                .andExpect(jsonPath("$..data[1].isSentByMe").value(true))
                .andExpect(jsonPath("$..data[1].message_text").value("Привет!"))
                .andExpect(jsonPath("$..data[1].read_status").value("READ"));
        mockMvc.perform(get("/api/v1/dialogs/1/messages")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[2].read_status").value("READ"));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    public void getCountUnreadedSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/unreaded")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    @WithUserDetails("ilia@yandex.ru")
    public void deleteDialogSuccess() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(5));
    }

    @Test
    public void addUserInDialogSuccess() throws Exception {

        UserIdsDto request = new UserIdsDto(List.of(3, 4));
        mockMvc.perform(put("/api/v1/dialogs/1/users").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_ids[0]").value(3))
                .andExpect(jsonPath("$.data.user_ids[1]").value(4));
    }

    @Test
    public void deleteUsersInDialogSuccess() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/1/users/3,4,5")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user_ids[0]").value(3))
                .andExpect(jsonPath("$.data.user_ids[1]").value(4))
                .andExpect(jsonPath("$.data.user_ids[2]").value(5));
    }

    @Test
    public void getLinkToJoinDialogSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/users/invite")).andExpect(status().isOk());
    }

    @Test
    public void joinDialogByLinkSuccess() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/1/users/join")).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("nik@yandex.ru")
    public void sendMessageSuccess() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST");
        mockMvc.perform(post("/api/v1/dialogs/4/messages").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.message_text").value("TEST"))
                .andExpect(jsonPath("$.data.read_status").value("SENT"))
                .andExpect(jsonPath("$.data.author_id").value(6))
                .andExpect(jsonPath("$.data.recipient_id").value(7));
    }

    @Test
    @WithUserDetails("nik@yandex.ru")
    public void deleteMessageSuccess() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/4/messages/6")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message_id").value(6));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    public void editMessageSuccess() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST3(2)");
        mockMvc.perform(put("/api/v1/dialogs/4/messages/7").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.time").isNotEmpty())
                .andExpect(jsonPath("$.data.message_text").value("TEST3(2)"))
                .andExpect(jsonPath("$.data.read_status").value("SENT"))
                .andExpect(jsonPath("$.data.author_id").value(7))
                .andExpect(jsonPath("$.data.recipient_id").value(6));
    }

    @Test
    @WithUserDetails("dmitriy@yandex.ru")
    public void readMessageSuccess() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/4/messages/7/read")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    public void getActivitySuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.last_activity").value(1627200965049L));
    }

    @Test
    public void changeTypingStatusSuccess() throws Exception {

        mockMvc.perform(post("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }
}