package application.service;

import application.models.City;
import application.models.Country;
import application.models.Language;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PlatformService {

    public List<Language> getLanguage() {

        List<Language> languageList = new ArrayList<>();
        languageList.add(new Language(1, "Русский"));
        languageList.add(new Language(2, "English"));
        return languageList;
    }

    public List<Country> getCountry(String country) {

        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1, "Россия"));
        countryList.add(new Country(2, "Italy"));
        return countryList;
    }

    public List<City> getCity(Integer countryId, String country) {

        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1, "Москва"));
        cityList.add(new City(2, "Омск"));
        cityList.add(new City(3, "Уфа"));
        cityList.add(new City(4, "Ростов"));
        cityList.add(new City(11, "Roma"));
        return cityList;
    }

}
