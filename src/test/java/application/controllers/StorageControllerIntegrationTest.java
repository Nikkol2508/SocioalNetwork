package application.controllers;

import application.models.FileDescription;
import application.service.StorageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithUserDetails("vasy@yandex.ru")
class StorageControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StorageController storageController;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StorageService storageService;

    @Test
    void testPutImage() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testImage.png", "image/png",
                "src/test/resources/testImage.png".getBytes());

        mockMvc.perform(multipart("/api/v1/storage").file(file).param("type", "IMAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.ownerId", is(1)))
                .andExpect(jsonPath("$.data.fileName", containsString("testImage.png")))
                .andExpect(jsonPath("$.data.relativeFilePath", containsString("testImage.png")))
                .andExpect(jsonPath("$.data.rawFileURL").isEmpty())
                .andExpect(jsonPath("$.data.bytes").isNotEmpty())
                .andExpect(jsonPath("$.data.fileType", is("IMAGE")))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    void testPutEmptyImage() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testImage.png", "image/png", (byte[]) null);

        mockMvc.perform(multipart("/api/v1/storage").file(file).param("type", "IMAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.timestamp", not(0)))
                .andExpect(jsonPath("$.data.id",is(0)))
                .andExpect(jsonPath("$.data.ownerId", is(0)))
                .andExpect(jsonPath("$.data.fileName").isEmpty())
                .andExpect(jsonPath("$.data.relativeFilePath").isEmpty())
                .andExpect(jsonPath("$.data.rawFileURL").isEmpty())
                .andExpect(jsonPath("$.data.bytes",is(0)))
                .andExpect(jsonPath("$.data.fileType").isEmpty())
                .andExpect(jsonPath("$.data.createdAt",is(0)));
    }

    @Test
    void testGetImage() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testImage.png", "image/png",
                "src/test/resources/testImage.png".getBytes());

        FileDescription fileDescription = storageService.saveFileInStorage("IMAGE",file);

        mockMvc.perform(get("/" + fileDescription.getRelativeFilePath())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(file.getBytes()));
    }

    @Test
    public void testGetImageProfileSuccess() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testProfileImage.png", "image/png",
                "src/test/resources/testProfileImage.png".getBytes());

        FileDescription fileDescription = storageService.saveFileInStorage("IMAGE",file);

        mockMvc.perform(get("/" + fileDescription.getRelativeFilePath())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(file.getBytes()));
    }
}
