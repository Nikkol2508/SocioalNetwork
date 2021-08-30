package application.dao;

import application.dao.mappers.IdMapper;
import application.dao.mappers.PathMapper;
import application.models.FileDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DaoFile implements Dao<FileDescription> {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void save(FileDescription fileDescription) {
    String sqlInsertFileDescription = "INSERT INTO image (owner_id, name, path," +
        " url, format, bytes, type, time) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    jdbcTemplate.update(sqlInsertFileDescription,
        fileDescription.getOwnerId(),
        fileDescription.getFileName(),
        fileDescription.getRelativeFilePath(),
        fileDescription.getRawFileURL(),
        fileDescription.getFileFormat(),
        fileDescription.getBytes(),
        fileDescription.getFileType(),
        fileDescription.getCreatedAt());
  }

  public String getPath(int id) {
    String sqlGetPathById = "SELECT path FROM image WHERE id = ?";

    return jdbcTemplate.query(sqlGetPathById,  new Object[]{id}, new PathMapper())
        .stream().findAny().orElse(null);
  }

  public FileDescription saveAndReturn(FileDescription fileDescription) {
    save(fileDescription);
    String sqlGetIdByName = "SELECT id FROM image WHERE name = ?";
    fileDescription.setId(jdbcTemplate.query(sqlGetIdByName, new Object[]{fileDescription.getFileName()}, new IdMapper())
        .stream().findAny().orElse(null));
    return fileDescription;
  }

  @Override
  public FileDescription getById(int id) {
    return null;
  }

  @Override
  public List<FileDescription> getAll() {
    return null;
  }



  @Override
  public void update(FileDescription fileDescription) {

  }

  @Override
  public void delete(int id) {

  }
}


