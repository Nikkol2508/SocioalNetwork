package application.service;

import application.models.City;
import application.models.Country;
import application.models.Language;
import application.responses.GeneralListResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PlatformService {

    public GeneralListResponse<Language> getLanguage() {

        GeneralListResponse<Language> languageResponse = new GeneralListResponse<>();
        languageResponse.setError("");
        languageResponse.setTimestamp(System.currentTimeMillis());
        languageResponse.setTotal(0);
        languageResponse.setOffset(0);
        languageResponse.setPerPage(20);
        List<Language> languageList = new ArrayList<>();
        languageList.add(new Language(1, "Русский"));
        languageList.add(new Language(2, "English"));
        languageResponse.setData(languageList);

        return languageResponse;
    }

    public GeneralListResponse<Country> getCountry(String country, Integer offset, Integer itemPerPage) {

        GeneralListResponse<Country> countryResponse = new GeneralListResponse<>();
        countryResponse.setError("");
        countryResponse.setTimestamp(System.currentTimeMillis());
        countryResponse.setTotal(0);
        countryResponse.setOffset(0);
        countryResponse.setPerPage(20);
        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1, "Россия"));
        countryList.add(new Country(2, "Italy"));
        countryResponse.setData(countryList);

        return countryResponse;
    }

    public GeneralListResponse<City> getSity(Integer countryId, String country, Integer offset, Integer itemPerPage) {

        GeneralListResponse<City> cityResponse = new GeneralListResponse<>();
        cityResponse.setError("");
        cityResponse.setTimestamp(System.currentTimeMillis());
        cityResponse.setTotal(0);
        cityResponse.setOffset(0);
        cityResponse.setPerPage(20);
        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1, "Москва"));
        cityList.add(new City(2, "Омск"));
        cityList.add(new City(3, "Уфа"));
        cityList.add(new City(4, "Ростов"));
        cityList.add(new City(11, "Roma"));
        cityResponse.setData(cityList);

        return cityResponse;
    }

}
