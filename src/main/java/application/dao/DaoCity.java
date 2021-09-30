package application.dao;

import application.dao.mappers.CityMapper;
import application.dao.mappers.CountryMapper;
import application.models.City;
import application.models.Country;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DaoCity {
    private final JdbcTemplate jdbcTemplate;

    public List<City> getAllCity() {
        log.info("getAllCity(): start():");
        String sql = "SELECT * FROM city";
        List<City> cityList = jdbcTemplate.query(sql, new CityMapper());
        log.debug("getAllCity(): cityList = {}", cityList);
        log.info("getAllCity(): finish():");
        return cityList;
    }

    public void saveCity(String city) {
        log.info("saveCity(): start():");
        log.debug("saveCity(): city = {}", city);
        String sql = "INSERT INTO city (name) VALUES (?)";
        jdbcTemplate.update(sql, city);
        log.info("saveCity(): finish():");
    }

    public void setCountry(String country) {
        log.info("setCountry(): start():");
        log.debug("setCountry(): country = {}", country);
        String sql = "INSERT INTO country (name) VALUES (?)";
        jdbcTemplate.update(sql, country);
        log.info("setCountry(): finish():");
    }

    public List<Country> getAllCountry() {
        log.info("getAllCountry(): start():");
        String sql = "SELECT * FROM country";
        List<Country> countryList = jdbcTemplate.query(sql, new CountryMapper());
        log.debug("getAllCountry(): countryList = {}", countryList);
        log.info("getAllCountry(): finish():");
        return countryList;
    }
}
