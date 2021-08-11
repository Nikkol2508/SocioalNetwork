package application.service;

import application.models.City;
import application.models.Country;
import application.models.Language;
import application.models.responses.GeneralListResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PlatformService {

    public GeneralListResponse<Language> getLanguage() {

        List<Language> languageList = new ArrayList<>();
        languageList.add(new Language(1, "Русский"));
        languageList.add(new Language(2, "English"));
        GeneralListResponse<Language> languageResponse = new GeneralListResponse<>(languageList);
        languageResponse.setTotal(0);
        languageResponse.setOffset(0);
        languageResponse.setPerPage(20);
        languageResponse.setData(languageList);

        return languageResponse;
    }

    public GeneralListResponse<Country> getCountry(String country, Integer offset, Integer itemPerPage) {

        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1, "Россия"));
        countryList.add(new Country(2, "Italy"));
        GeneralListResponse<Country> countryResponse = new GeneralListResponse<>(countryList);
        countryResponse.setTotal(0);
        countryResponse.setOffset(0);
        countryResponse.setPerPage(20);

        return countryResponse;
    }

    public GeneralListResponse<City> getCity(Integer countryId, String country, Integer offset, Integer itemPerPage) {

        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1, "Москва"));
        cityList.add(new City(2, "Омск"));
        cityList.add(new City(3, "Уфа"));
        cityList.add(new City(4, "Ростов"));
        cityList.add(new City(11, "Roma"));
        GeneralListResponse<City> cityResponse = new GeneralListResponse<>(cityList);
        cityResponse.setTotal(0);
        cityResponse.setOffset(0);
        cityResponse.setPerPage(20);

        return cityResponse;
    }

}
