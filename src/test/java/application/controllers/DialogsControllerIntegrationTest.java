package application.controllers;

import application.models.dto.UserIdsDto;
import application.models.requests.DialogCreateDtoRequest;
import application.models.requests.MessageSendDtoRequest;
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
import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.assertj.core.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithUserDetails("vasy@yandex.ru")
public class DialogsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DialogsController dialogsController;

    @Autowired
    private DataSource dataSource;

    @Test
    @Order(1)
    public void getDialogsSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs")).andExpect(jsonPath("$..data.length()")
                .value(1));
    }

    @Test
    @Order(2)
    public void createDialogSuccess() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(3));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    @Order(3)
    public void createExistingDialogSuccess() throws Exception {

        DialogCreateDtoRequest request = new DialogCreateDtoRequest();
        request.setUsersIds(List.of(2));
        mockMvc.perform(post("/api/v1/dialogs").content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @Order(6)
    public void getMessagesInDialogSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/messages")).andExpect(status().isOk())
                .andExpect(jsonPath("$..data.length()").value(4));
    }

    @Test
    @Order(5)
    public void getCountUnreadedSuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/unreaded")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.count").value(1));
    }

    @Test
    public void deleteDialogSuccess() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/2")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
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
    @WithUserDetails("homa@yandex.ru")
    @Order(4)
    public void sendMessageSuccess() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST");
        mockMvc.perform(post("/api/v1/dialogs/1/messages").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message_text").value("TEST"))
                .andExpect(jsonPath("$.data.read_status").value("SENT"))
                .andExpect(jsonPath("$.data.author_id").value(2))
                .andExpect(jsonPath("$.data.recipient_id").value(1));

    }

    @Test
    @Order(8)
    public void deleteMessageSuccess() throws Exception {

        mockMvc.perform(delete("/api/v1/dialogs/1/messages/4")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message_id").value(4));
    }

    @Test
    @Order(7)
    public void editMessageSuccess() throws Exception {

        MessageSendDtoRequest request = new MessageSendDtoRequest("TEST2");
        mockMvc.perform(put("/api/v1/dialogs/1/messages/4").content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message_text").value("TEST2"));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    @Order(9)
    public void readMessageSuccess() throws Exception {

        mockMvc.perform(put("/api/v1/dialogs/2/messages/1/read")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }

    @Test
    public void getActivitySuccess() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.last_activity").value(1627200965049L));
    }

    @Test
    public void changeTypingStatusSuccess() throws Exception {

        mockMvc.perform(post("/api/v1/dialogs/1/activity/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.message").value("ok"));
    }
}