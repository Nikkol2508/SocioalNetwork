package application.dao.mappers;

import application.models.City;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CityMapper implements RowMapper<City> {
    @Override
    public City mapRow(ResultSet rs, int rowNum) throws SQLException {
        City city = new City();
        city.setId(rs.getInt("id"));
        city.setTitle(rs.getString("name"));
        return city;
    }
}
