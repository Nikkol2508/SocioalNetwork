package application.service;

import application.responses.LanguageResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class PlatformService {

    private final LanguageResponse languageResponse;

    public PlatformService(LanguageResponse languageResponse) {
        this.languageResponse = languageResponse;
    }

    public LanguageResponse getLanguage() {

        languageResponse.setError("");
        languageResponse.setTimestamp(System.currentTimeMillis());
        languageResponse.setTotal(0);
        languageResponse.setOffset(0);
        languageResponse.setPerPage(20);
        HashMap<Integer, String> dataObject = new HashMap<>();
        dataObject.put(1, "руссо");
        ArrayList<HashMap> data = new ArrayList<>();
        data.add(dataObject);
        languageResponse.setData(data);

        return languageResponse;
    }

}
