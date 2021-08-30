package application.dao.mappers;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class IdMapper implements RowMapper<Integer>{

  @Override
  public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {

    return rs.getInt("id");
  }
}

