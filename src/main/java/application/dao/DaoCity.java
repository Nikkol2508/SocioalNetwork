package application.dao;

import application.dao.mappers.CityMapper;
import application.dao.mappers.CountryMapper;
import application.models.City;
import application.models.Country;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DaoCity {
    private final JdbcTemplate jdbcTemplate;

    public List<City> getAllCity() {
        String sql = "SELECT * FROM city";
        return jdbcTemplate.query(sql, new CityMapper());
    }

    public void saveCity(String city) {
        String sql = "INSERT INTO city (name) VALUES (?)";
        jdbcTemplate.update(sql, city);
    }

    public void setCountry(String country) {
        String sql = "INSERT INTO country (name) VALUES (?)";
        jdbcTemplate.update(sql, country);
    }

    public List<Country> getAllCountry() {
        String sql = "SELECT * FROM country";
        return jdbcTemplate.query(sql, new CountryMapper());
    }
}
