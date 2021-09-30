package application.dao;

import application.dao.mappers.FileDescriptionMapper;
import application.dao.mappers.PathMapper;
import application.models.FileDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoFile {

    private final JdbcTemplate jdbcTemplate;

    public void save(FileDescription fileDescription) {

        log.info("save(): start():");
        log.debug("save(): fileDescription = {}", fileDescription);
        String sqlInsertFileDescription = "INSERT INTO image (owner_id, name, path," +
                " url, format, bytes, type, time, data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlInsertFileDescription, fileDescription.getOwnerId(), fileDescription.getFileName(),
                fileDescription.getRelativeFilePath(), fileDescription.getRawFileURL(), fileDescription.getFileFormat(),
                fileDescription.getBytes(), fileDescription.getFileType(), fileDescription.getCreatedAt(), fileDescription.getData());
        log.info("save(): finish():");
    }

    public String getPath(int id) {

        log.info("getPath(): start():");
        log.debug("getPath(): id = {}", id);
        String sqlGetPathById = "SELECT path FROM image WHERE id = ?";
        String path = jdbcTemplate.query(sqlGetPathById, new Object[]{id}, new PathMapper()).stream().findAny()
                .orElse(null);
        log.debug("getPath(): path = {}", path);
        log.info("getPath(): finish():");
        return path;
    }

    public FileDescription saveAndReturn(FileDescription fileDescription) {

        log.info("saveAndReturn(): start():");
        log.debug("saveAndReturn(): fileDescription = {}", fileDescription);
        SimpleJdbcInsert sji = new SimpleJdbcInsert(jdbcTemplate).withTableName("image").usingGeneratedKeyColumns("id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("owner_id", fileDescription.getOwnerId());
        parameters.put("name", fileDescription.getFileName());
        parameters.put("path", fileDescription.getRelativeFilePath());
        parameters.put("url", fileDescription.getRawFileURL());
        parameters.put("format", fileDescription.getFileFormat());
        parameters.put("bytes", fileDescription.getBytes());
        parameters.put("type", fileDescription.getFileType());
        parameters.put("time", System.currentTimeMillis());
        parameters.put("data", fileDescription.getData());
        FileDescription returnedDescription = getById(sji.executeAndReturnKey(parameters).intValue());
        //log.debug("saveAndReturn(): returnedDescription = {}", returnedDescription);
        log.info("saveAndReturn(): finish():");
        return returnedDescription;
    }

    public FileDescription getById(int id) {
        log.info("getById(): start():");
        log.debug("getById(): id = {}", id);
        String query = "SELECT * FROM image WHERE id = ?";
        FileDescription fileDescription = jdbcTemplate.queryForObject(query, new Object[]{id}, new FileDescriptionMapper());
        //log.debug("getById(): fileDescription = {}", fileDescription);
        log.info("getById(): finish():");
        return fileDescription;
    }

    public FileDescription getByImageName(String name) {
        log.info("getByImageName(): start():");
        log.debug("getByImageName(): name = {}", name);
        String query = "SELECT * FROM image WHERE name = ?";
        FileDescription fileDescription = jdbcTemplate.queryForObject(query, new Object[]{name}, new FileDescriptionMapper());
        //log.debug("getByImageName(): fileDescription = {}", fileDescription);
        log.info("getByImageName(): finish():");
        return fileDescription;
    }

    public void deleteImage(int personId) {
        log.info("deleteImage(): start():");
        log.debug("deleteImage(): personId = {}", personId);
        jdbcTemplate.update("DELETE FROM image where owner_id = ?", personId);
        log.info("deleteImage(): finish():");
    }

//    public void deleteByPersonId(int personId, String path) throws NullPointerException {
//
//        String query = "DELETE FROM image WHERE owner_id = ?";
//        File file = new File(path);
//        if (file.) {
//            file.delete();
//            jdbcTemplate.update(query, personId);
//        }
//    }
}


