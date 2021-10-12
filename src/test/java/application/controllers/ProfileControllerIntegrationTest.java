package application.controllers;

import application.models.requests.PersonSettingsDtoRequest;
import application.models.requests.PostRequest;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileController profileController;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    private static void setup(@Autowired DataSource dataSource) throws SQLException {

        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource("/test-data-for-profile-controller-test.sql"));
        }
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getProfileTest() throws Exception {

        validateResult(mockMvc.perform(get("/api/v1/users/me")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getPersonTest() throws Exception {

        validateResult(mockMvc.perform(get("/api/v1/users/1")));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getPersonTest2() throws Exception {

        validateErrorResult(mockMvc.perform(get("/api/v1/users/-15")),
                "/api/v1/users/-15",
                "Person with id -15 is not found.");
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getPersonTest3() throws Exception {

        validateErrorResult(mockMvc.perform(get("/api/v1/users/0")),
                "/api/v1/users/0",
                "Person with id 0 is not found.");
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getPersonTest4() throws Exception {

        validateErrorResult(mockMvc.perform(get("/api/v1/users/1000")),
                "/api/v1/users/1000",
                "Person with id 1000 is not found.");
    }

    private void validateResult(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error").value("Error"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.email", is("vasy@yandex.ru")))
                .andExpect(jsonPath("$.data.phone", is("89998887744")))
                .andExpect(jsonPath("$.data.about", is("Я Вася")))
                .andExpect(jsonPath("$.data.city", is("Москва")))
                .andExpect(jsonPath("$.data.country", is("Россия")))
                .andExpect(jsonPath("$.data.first_name", is("Вася")))
                .andExpect(jsonPath("$.data.last_name", is("Васичкин")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(964513590000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("petr@yandex.ru")
    void getWallTest1() throws Exception {

        mockMvc.perform(get("/api/v1/users/4/wall")
                        .param("offset", String.valueOf(5)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total", is(7)))
                .andExpect(jsonPath("$.offset", is(5)))
                .andExpect(jsonPath("$.perPage", is(5)))
                .andExpect(jsonPath("$.data[6].id", is(1)))
                .andExpect(jsonPath("$.data[6].time", is(1625735590000L)))
                .andExpect(jsonPath("$.data[6].author.id", is(4)))
                .andExpect(jsonPath("$.data[6].author.email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[6].author.phone", is("89998887744")))
                .andExpect(jsonPath("$.data[6].author.about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[6].author.city", is("Омск")))
                .andExpect(jsonPath("$.data[6].author.country", is("Россия")))
                .andExpect(jsonPath("$.data[6].author.first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[6].author.last_name", is("Петров")))
                .andExpect(jsonPath("$.data[6].author.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[6].author.birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[6].author.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[6].author.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[6].author.is_blocked", is(false)))
                .andExpect(jsonPath("$.data[6].title", is("Спор о первом языке програмирования")))
                .andExpect(jsonPath("$.data[6].likes", is(2)))
                .andExpect(jsonPath("$.data[6].my_like", is(0)))
                .andExpect(jsonPath("$.data[6].comments").hasJsonPath())
                .andExpect(jsonPath("$.data[6].comments.length()", is(1)))
                .andExpect(jsonPath("$.data[6].post_text",
                        containsString("Много текста про разные языки програмирования")))
                .andExpect(jsonPath("$.data[6].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[6].tags", is(List.of("beer"))))
                .andExpect(jsonPath("$.data.length()", is(7)));
    }

    @Test
    @WithUserDetails("cergei@yandex.ru")
    void getWallTest2() throws Exception {

        validateGetWallEmptyResponse(mockMvc.perform(get("/api/v1/users/5/wall")
                .param("offset", String.valueOf(5))));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getWallTest3() throws Exception {

        validateGetWallEmptyResponse(mockMvc.perform(get("/api/v1/users/1000/wall")
                .param("offset", String.valueOf(5))));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void getWallTest4() throws Exception {

        validateGetWallEmptyResponse(mockMvc.perform(get("/api/v1/users/-1000/wall")
                .param("offset", String.valueOf(5))));
    }

    private void validateGetWallEmptyResponse(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.offset", is(5)))
                .andExpect(jsonPath("$.perPage", is(5)))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest1() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "ома")))
                .andExpect(jsonPath("$.total", is(1))));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest2() throws Exception {

        isContainsPetr(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Пётр")))
                .andExpect(jsonPath("$.total", is(1))));
    }

    @Test
    @WithUserDetails("petr@yandex.ru")
    void searchPersonsByFirstOrLastNameTest3() throws Exception {

        isContainsPetr(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Пётр")))
                .andExpect(jsonPath("$.total", is(1))));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Not exist first name")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "123(-%$")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "    ")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest7() throws Exception {


        validateErrorResult(
                mockMvc.perform(get("/api/v1/users/search").param("first_or_last_name", "")),
                "/api/v1/users/search",
                "The search bar must be at least 2 characters long");
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest8() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                        .param("first_or_last_name", "ов"))
                .andExpect(jsonPath("$.total", is(7)))
                .andExpect(jsonPath("$.data[2].id", is(4)))
                .andExpect(jsonPath("$.data[2].email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[2].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[2].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[2].city", is("Омск")))
                .andExpect(jsonPath("$.data[2].country", is("Россия")))
                .andExpect(jsonPath("$.data[2].first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[2].last_name", is("Петров")))
                .andExpect(jsonPath("$.data[2].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[2].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[2].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[2].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[2].is_blocked", is(false)))));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest9() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Хомяков"))));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonsByFirstOrLastNameTest10() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Not exist last name")));
    }

    @Test
    @WithUserDetails("petr@yandex.ru")
    void searchPersonsByFirstOrLastNameTest11() throws Exception {

        isContainsPetr(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_or_last_name", "Петров")))
                .andExpect(jsonPath("$.total", is(1))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest1() throws Exception {

        isContainsPetr(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "Пёт")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest2() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "Хома")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest3() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "Not exist first name")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "   ")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "(?№*")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest6() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "А")))
                .andExpect(jsonPath("$.total", is(5)))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[1].city", is("Москва")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[1].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(5)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByFirstNameTest7() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest1() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "омяк")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest2() throws Exception {

        isContainsPetr(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "Петров")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest3() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "Not exist last name")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "   ")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "*&$#@")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByLastNameTest7() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("last_name", "Х")))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest1() throws Exception {

        dataContainsHomaAndPetr(validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search").param("country", "Россия"))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest2() throws Exception {

        dataContainsHomaAndPetr(validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search").param("country", "сия"))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest3() throws Exception {

        dataContainsHomaAndPetr(validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search").param("country", "Р"))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("country", "Not exist country")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("country", "   ")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("country", "")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCountryTest7() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("country", "!№;%:?")));
    }

    private void dataContainsHomaAndPetr(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(jsonPath("$.total", is(10)))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[1].city", is("Москва")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[1].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[3].id", is(4)))
                .andExpect(jsonPath("$.data[3].email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[3].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[3].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[3].city", is("Омск")))
                .andExpect(jsonPath("$.data[3].country", is("Россия")))
                .andExpect(jsonPath("$.data[3].first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[3].last_name", is("Петров")))
                .andExpect(jsonPath("$.data[3].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[3].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[3].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[3].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[3].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(10)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest1() throws Exception {

        dataContainsHoma(validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search").param("city", "Москва"))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest2() throws Exception {

        dataContainsHoma(validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search").param("city", "скв"))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest3() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "М")))
                .andExpect(jsonPath("$.total", is(5)))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[1].city", is("Москва")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[1].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[3].id", is(4)))
                .andExpect(jsonPath("$.data[3].email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[3].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[3].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[3].city", is("Омск")))
                .andExpect(jsonPath("$.data[3].country", is("Россия")))
                .andExpect(jsonPath("$.data[3].first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[3].last_name", is("Петров")))
                .andExpect(jsonPath("$.data[3].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[3].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[3].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[3].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[3].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(5)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "Not exist city")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "   ")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest7() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "№;%:?")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByCityTest8() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("city", "   ")));
    }

    private void dataContainsHoma(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[1].city", is("Москва")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[1].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(3)));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeFromTest1() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(
                mockMvc.perform(get("/api/v1/users/search")
                        .param("age_from", "100")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(15)))
                .andExpect(jsonPath("$.data[0].email", is("test31@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998881044")))
                .andExpect(jsonPath("$.data[0].about", is("I am Jon")))
                .andExpect(jsonPath("$.data[0].city", is("Sidney")))
                .andExpect(jsonPath("$.data[0].country", is("Australia")))
                .andExpect(jsonPath("$.data[0].first_name", is("Jon")))
                .andExpect(jsonPath("$.data[0].last_name", is("Dow")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(-1577934000000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeFromTest2() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "250")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeFromTest3() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeFromTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "     ")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeFromTest5() throws Exception {

        mockMvc.perform(get("/api/v1/users/search")
                        .param("age_from", "not a number"))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Content-Type"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeToTest1() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("age_to", "50")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeToTest2() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_to", "18")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeToTest3() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_to", "")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeToTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_to", "     ")));
    }

    @Test
    @WithUserDetails("robert@yandex.ru")
    void searchPersonByAgeToTest5() throws Exception {

        validateIncorrectRequest(mockMvc.perform(get("/api/v1/users/search")
                .param("age_to", "not a number")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest1() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "25")
                .param("age_to", "60")))
                .andExpect(jsonPath("$.total", is(2)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[1].id", is(6)))
                .andExpect(jsonPath("$.data[1].email", is("nik@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[1].city", is("Ногинск")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Николай")))
                .andExpect(jsonPath("$.data[1].last_name", is("Аксёнов")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(207131190000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(2)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest2() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "1")
                .param("age_to", "60")))
                .andExpect(jsonPath("$.total", is(10)))
                .andExpect(jsonPath("$.data[1].id", is(2)))
                .andExpect(jsonPath("$.data[1].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[1].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[1].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[1].city", is("Москва")))
                .andExpect(jsonPath("$.data[1].country", is("Россия")))
                .andExpect(jsonPath("$.data[1].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[1].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[1].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[1].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[1].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[1].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[1].is_blocked", is(false)))
                .andExpect(jsonPath("$.data[5].id", is(6)))
                .andExpect(jsonPath("$.data[5].email", is("nik@yandex.ru")))
                .andExpect(jsonPath("$.data[5].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[5].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[5].city", is("Ногинск")))
                .andExpect(jsonPath("$.data[5].country", is("Россия")))
                .andExpect(jsonPath("$.data[5].first_name", is("Николай")))
                .andExpect(jsonPath("$.data[5].last_name", is("Аксёнов")))
                .andExpect(jsonPath("$.data[5].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[5].birth_date", is(207131190000L)))
                .andExpect(jsonPath("$.data[5].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[5].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[5].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(10)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest3() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "26")
                .param("age_to", "27")))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(1)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "32")
                .param("age_to", "33")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "33")
                .param("age_to", "32")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "21")
                .param("age_to", "20")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest7() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "")
                .param("age_to", "")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest8() throws Exception {

        mockMvc.perform(get("/api/v1/users/search")
                        .param("age_from", "100")
                        .param("age_to", ""))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(15)))
                .andExpect(jsonPath("$.data[0].email", is("test31@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998881044")))
                .andExpect(jsonPath("$.data[0].about", is("I am Jon")))
                .andExpect(jsonPath("$.data[0].city", is("Sidney")))
                .andExpect(jsonPath("$.data[0].country", is("Australia")))
                .andExpect(jsonPath("$.data[0].first_name", is("Jon")))
                .andExpect(jsonPath("$.data[0].last_name", is("Dow")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(-1577934000000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest9() throws Exception {

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "")
                .param("age_to", "25")))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total", is(8)))
                .andExpect(jsonPath("$.data[2].id", is(4)))
                .andExpect(jsonPath("$.data[2].email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[2].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[2].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[2].city", is("Омск")))
                .andExpect(jsonPath("$.data[2].country", is("Россия")))
                .andExpect(jsonPath("$.data[2].first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[2].last_name", is("Петров")))
                .andExpect(jsonPath("$.data[2].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[2].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[2].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[2].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[2].is_blocked", is(false)))
                .andExpect(jsonPath("$.data.length()", is(8)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest10() throws Exception {

        validateIncorrectRequest(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "21")
                .param("age_to", "not a number")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest11() throws Exception {

        validateIncorrectRequest(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "not a number")
                .param("age_to", "not a number")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest12() throws Exception {

        validateIncorrectRequest(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "21")
                .param("age_to", "!@#$")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest13() throws Exception {

        mockMvc.perform(get("/api/v1/users/search")
                        .param("age_from", "26")
                        .param("age_to", "26"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAgeFromAndAgeToTest14() throws Exception {

        validateIncorrectRequest(mockMvc.perform(get("/api/v1/users/search")
                .param("age_from", "!@#$")
                .param("age_to", "47")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest1() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                        .param("first_name", "ома")
                        .param("last_name", "омяк")
                        .param("country", "ссия")
                        .param("city", "ква")
                        .param("age_from", "20")
                        .param("age_to", "30"))
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.data.length()", is(1)))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest2() throws Exception {

        isContainsHoma(validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(get("/api/v1/users/search")
                        .param("first_name", "а")
                        .param("last_name", "ов")
                        .param("country", "ссия")
                        .param("city", "к")
                        .param("age_from", "20")
                        .param("age_to", "50"))
                .andExpect(jsonPath("$.total", is(3)))
                .andExpect(jsonPath("$.data.length()", is(3)))));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest3() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "Not exist first name")
                .param("last_name", "омяк")
                .param("country", "ссия")
                .param("city", "ква")
                .param("age_from", "20")
                .param("age_to", "30")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest4() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "ома")
                .param("last_name", "Not exist last name")
                .param("country", "ссия")
                .param("city", "ква")
                .param("age_from", "20")
                .param("age_to", "30")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest5() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "ома")
                .param("last_name", "омяк")
                .param("country", "Not exist country")
                .param("city", "ква")
                .param("age_from", "20")
                .param("age_to", "30")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest6() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "ома")
                .param("last_name", "омяк")
                .param("country", "ссия")
                .param("city", "Not exist city")
                .param("age_from", "20")
                .param("age_to", "30")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest7() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "ома")
                .param("last_name", "омяк")
                .param("country", "ссия")
                .param("city", "ква")
                .param("age_from", "30")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest8() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "ома")
                .param("last_name", "омяк")
                .param("country", "ссия")
                .param("city", "ква")
                .param("age_from", "32")
                .param("age_to", "31")));
    }

    @Test
    @WithUserDetails("ivan@yandex.ru")
    void searchPersonByAllParamTest9() throws Exception {

        validateResultIncorrectSearch(mockMvc.perform(get("/api/v1/users/search")
                .param("first_name", "")
                .param("last_name", "")
                .param("country", "")
                .param("city", "")
                .param("age_from", "")
                .param("age_to", "")));
    }

    private void validateIncorrectRequest(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist("Content-Type"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$").doesNotExist());
    }

    private void isContainsPetr(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(jsonPath("$.data[0].id", is(4)))
                .andExpect(jsonPath("$.data[0].email", is("petr@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Немного обо мне")))
                .andExpect(jsonPath("$.data[0].city", is("Омск")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Пётр")))
                .andExpect(jsonPath("$.data[0].last_name", is("Петров")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(901355190000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    private void isContainsHoma(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(jsonPath("$.data[0].id", is(2)))
                .andExpect(jsonPath("$.data[0].email", is("homa@yandex.ru")))
                .andExpect(jsonPath("$.data[0].phone", is("89998887744")))
                .andExpect(jsonPath("$.data[0].about", is("Я Хомяков")))
                .andExpect(jsonPath("$.data[0].city", is("Москва")))
                .andExpect(jsonPath("$.data[0].country", is("Россия")))
                .andExpect(jsonPath("$.data[0].first_name", is("Хома")))
                .andExpect(jsonPath("$.data[0].last_name", is("Хомяков")))
                .andExpect(jsonPath("$.data[0].reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data[0].birth_date", is(806660790000L)))
                .andExpect(jsonPath("$.data[0].messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data[0].last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data[0].is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void setPostTest1() throws Exception {

        final int daysLater = 3;
        Long postDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .plusDays(daysLater).toInstant().toEpochMilli();
        List<String> tags = new ArrayList<>();
        tags.add("Some tag");
        PostRequest request = new PostRequest();
        request.setTitle("Some title");
        request.setPostText("Some text");
        request.setTags(tags);

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("publish_date", String.valueOf(postDate)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time", is(postDate)))
                .andExpect(jsonPath("$.data.authorId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Some title")))
                .andExpect(jsonPath("$.data.postText", is("Some text")))
                .andExpect(jsonPath("$.data.blocked", is(false))));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void setPostTest2() throws Exception {

        Long postDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<String> tags = new ArrayList<>();
        tags.add("Some tag");
        PostRequest request = new PostRequest();
        request.setTitle("Some title");
        request.setPostText("Some text");
        request.setTags(tags);

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("publish_date", String.valueOf(postDate)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time", is(postDate)))
                .andExpect(jsonPath("$.data.authorId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Some title")))
                .andExpect(jsonPath("$.data.postText", is("Some text")))
                .andExpect(jsonPath("$.data.blocked", is(false))));
    }

    @Test
    @WithUserDetails("vasy@yandex.ru")
    void setPostTest3() throws Exception {

        final int daysBefore = 3;
        Long postDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                .minusDays(daysBefore).toInstant().toEpochMilli();
        List<String> tags = new ArrayList<>();
        tags.add("Some tag");
        PostRequest request = new PostRequest();
        request.setTitle("Some title");
        request.setPostText("Some text");
        request.setTags(tags);

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(post("/api/v1/users/1/wall")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("publish_date", String.valueOf(postDate)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.time", is(postDate)))
                .andExpect(jsonPath("$.data.authorId", is(1)))
                .andExpect(jsonPath("$.data.title", is("Some title")))
                .andExpect(jsonPath("$.data.postText", is("Some text")))
                .andExpect(jsonPath("$.data.blocked", is(false))));
    }

    @Test
    @WithUserDetails("test11@yandex.ru")
    void updateProfileTest1() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setLastName("");
        request.setFirstName("");
        request.setCity("Zelenograd");
        request.setBirthDate("1940-01-01T00:00:00+03:00");
        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email", is("test11@yandex.ru")))
                .andExpect(jsonPath("$.data.city", is("Zelenograd")))
                .andExpect(jsonPath("$.data.first_name", is("Alex")))
                .andExpect(jsonPath("$.data.last_name", is("Bloh")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(-946782000000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false))));
    }

    @Test
    @WithUserDetails("test21@yandex.ru")
    void updateProfileTest2() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("Some name");
        request.setLastName("Some surname");
        request.setBirthDate("09.06.1988");
        request.setCity("Зеленоград");
        mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is("Date is not valid")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithUserDetails("test22@yandex.ru")
    void updateProfileTest3() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("");
        request.setLastName("");
        request.setCity("Boston");
        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email", is("test22@yandex.ru")))
                .andExpect(jsonPath("$.data.city", is("Boston")))
                .andExpect(jsonPath("$.data.first_name", is("Alex")))
                .andExpect(jsonPath("$.data.last_name", is("Bloh")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(0)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false))));
    }

    @Test

    @WithUserDetails("test12@yandex.ru")
    void updateProfileTest4() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setLastName("");
        request.setBirthDate("1950-01-01T00:00:00+03:00");

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email", is("test12@yandex.ru")))
                .andExpect(jsonPath("$.data.city").doesNotExist()))
                .andExpect(jsonPath("$.data.country").doesNotExist())
                .andExpect(jsonPath("$.data.phone").doesNotExist())
                .andExpect(jsonPath("$.data.photo").doesNotExist())
                .andExpect(jsonPath("$.data.first_name", is("Alex")))
                .andExpect(jsonPath("$.data.last_name", is("Bloh")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(-631162800000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("test23@yandex.ru")
    void updateProfileTest5() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("");
        request.setBirthDate("1948-01-01T00:00:00+03:00");
        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email", is("test23@yandex.ru")))
                .andExpect(jsonPath("$.data.first_name", is("Alex")))
                .andExpect(jsonPath("$.data.last_name", is("Bloh")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(-694321200000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false))));
    }

    @Test
    @WithUserDetails("test13@yandex.ru")
    void updateProfileTest6() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.data.email", is("test13@yandex.ru")))
                .andExpect(jsonPath("$.data.city").doesNotExist())
                .andExpect(jsonPath("$.data.country").doesNotExist())
                .andExpect(jsonPath("$.data.about").doesNotExist())
                .andExpect(jsonPath("$.data.phone").doesNotExist())
                .andExpect(jsonPath("$.data.first_name", is("Alex")))
                .andExpect(jsonPath("$.data.last_name", is("Bloh")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(-606798000000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("test24@yandex.ru")
    void updateProfileTest7() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file", "testImage.png", "image/png",
                "src/test/resources/testImage.png".getBytes());
        mockMvc.perform(multipart("/api/v1/storage").file(file).param("type", "IMAGE"));


        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("Some name");
        request.setLastName("Some surname");
        request.setBirthDate("1950-10-10T00:00:00+03:00");
        request.setCity("Bobryisk");
        request.setCountry("Belarus");
        request.setAbout("Some information");
        request.setPhone("79774743685");
        request.setPhotoId("1");

        validateResultCorrectSearchPersonOrSetPost(mockMvc.perform(put("/api/v1/users/me")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)))
                .andExpect(jsonPath("$.data.email", is("test24@yandex.ru")))
                .andExpect(jsonPath("$.data.city", is("Bobryisk")))
                .andExpect(jsonPath("$.data.country", is("Belarus")))
                .andExpect(jsonPath("$.data.about", is("Some information")))
                .andExpect(jsonPath("$.data.phone", is("79774743685")))
                .andExpect(jsonPath("$.data.first_name", is("Some name")))
                .andExpect(jsonPath("$.data.last_name", is("Some surname")))
                .andExpect(jsonPath("$.data.photo", containsString("testImage.png")))
                .andExpect(jsonPath("$.data.reg_date", is(1625127990000L)))
                .andExpect(jsonPath("$.data.birth_date", is(-606798000000L)))
                .andExpect(jsonPath("$.data.messages_permission", is("ALL")))
                .andExpect(jsonPath("$.data.last_online_time", is(1627200965049L)))
                .andExpect(jsonPath("$.data.is_blocked", is(false)));
    }

    @Test
    @WithUserDetails("test25@yandex.ru")
    void updateProfileTest8() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("S");
        mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is("/api/v1/users/me")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("First name has invalid characters or length is not between 2 and 50")))
                .andExpect(jsonPath("$.data").doesNotExist());

    }

    @Test
    @WithUserDetails("test26@yandex.ru")
    void updateProfileTest9() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setLastName("S");
        mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is("/api/v1/users/me")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Last name has invalid characters or length is not between 2 and 50")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithUserDetails("test27@yandex.ru")
    void updateProfileTest10() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setFirstName("Sooooooooooooooooooooooooooooooome naaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaame");
        mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is("/api/v1/users/me")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("First name has invalid characters or length is not between 2 and 50")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithUserDetails("test28@yandex.ru")
    void updateProfileTest11() throws Exception {

        PersonSettingsDtoRequest request = new PersonSettingsDtoRequest();
        request.setLastName("Sooooooooooooooooooooooooooooooome surnaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaame");
        mockMvc.perform(put("/api/v1/users/me")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is("/api/v1/users/me")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Last name has invalid characters or length is not between 2 and 50")))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    @WithUserDetails("test14@yandex.ru")
    void deleteProfileTest1() throws Exception {

        mockMvc.perform(delete("/api/v1/users/me"))
                .andDo(print());

        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is("/api/v1/users/me")))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description",
                        is("Person with email: test14@yandex.ru is not found.")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private ResultActions validateResultCorrectSearchPersonOrSetPost(ResultActions resultActions) throws Exception {

        return resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    private void validateErrorResult(
            ResultActions resultActions, String path, String errorDescription) throws Exception {

        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.path", is(path)))
                .andExpect(jsonPath("$.error", is("invalid_request")))
                .andExpect(jsonPath("$.error_description", is(errorDescription)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    private void validateResultIncorrectSearch(ResultActions resultActions) throws Exception {

        resultActions
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(authenticated())
                .andExpect(jsonPath("$.total", is(0)))
                .andExpect(jsonPath("$.error", is("Error")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}