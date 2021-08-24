package application.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

public class IdMapper implements RowMapper<Integer>{

  @Override
  public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {

    return rs.getInt("id");
  }
}

