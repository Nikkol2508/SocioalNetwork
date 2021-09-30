package application.service;

import application.dao.DaoCity;
import application.models.City;
import application.models.Country;
import application.models.Language;
import application.models.dto.MessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlatformService {
    private final DaoCity daoCity;

    public List<Language> getLanguage() {

        List<Language> languageList = new ArrayList<>();
        languageList.add(new Language(1, "Русский"));
        languageList.add(new Language(2, "English"));
        return languageList;
    }

    public List<Country> getCountry() {

        return daoCity.getAllCountry();
    }

    public List<City> getCity() {

        return daoCity.getAllCity();
    }

    public MessageResponseDto setUserCity(String city) {
        daoCity.saveCity(city);
        return new MessageResponseDto();
    }

    public MessageResponseDto setCountry(String country) {
        daoCity.setCountry(country);
        return new MessageResponseDto();
    }
}
