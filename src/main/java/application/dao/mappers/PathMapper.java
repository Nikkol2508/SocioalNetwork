package application.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class PathMapper implements RowMapper<String> {


  @Override
  public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    return rs.getString("path");
  }
}
