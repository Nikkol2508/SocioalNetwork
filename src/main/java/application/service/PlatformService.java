package application.service;

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

}
