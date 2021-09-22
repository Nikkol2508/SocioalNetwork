package application.controllers;

import application.dao.DaoFile;
import application.models.FileDescription;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.sql.DataSource;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.OPENTABLE;
import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@AutoConfigureEmbeddedDatabase(provider = OPENTABLE, refresh = AFTER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WithUserDetails("vasy@yandex.ru")
public class StorageControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StorageController storageController;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DaoFile daoFile;

    @Test
    public void putImageSuccess() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testImage.png", "image/png",
                "src/test/resources/testImage.png".getBytes());

        mockMvc.perform(multipart("/api/v1/storage").file(file).param("type", "IMAGE"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.ownerId").value(1))
                .andExpect(jsonPath("$.data.fileName")
                        .value(containsString("testImagepng")))
                .andExpect(jsonPath("$.data.relativeFilePath")
                        .value(containsString("storage/testImagepng")))
                .andExpect(jsonPath("$.data.rawFileURL").isEmpty())
                .andExpect(jsonPath("$.data.bytes").isNotEmpty())
                .andExpect(jsonPath("$.data.fileType").value("IMAGE"))
                .andExpect(jsonPath("$.data.createdAt").isNotEmpty());
    }

    @Test
    public void getImageSuccess() throws Exception {
        MockMultipartFile file
                = new MockMultipartFile(
                "file", "testImage.png", "image/png",
                "src/test/resources/testImage.png".getBytes());

        FileDescription fileDescription = new FileDescription();
        fileDescription.setOwnerId(1);
        fileDescription.setFileName("testImage");
        fileDescription.setRelativeFilePath("storage/testImage");
        fileDescription.setRawFileURL("url");
        fileDescription.setFileFormat(file.getContentType());
        fileDescription.setBytes(file.getBytes().length);
        fileDescription.setFileType("IMAGE");
        fileDescription.setData(file.getBytes());
        daoFile.saveAndReturn(fileDescription);

        mockMvc.perform(get("/storage/testImage"))
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
    }
}
