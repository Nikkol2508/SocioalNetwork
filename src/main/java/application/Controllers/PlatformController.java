package application.Controllers;

import application.responses.LanguageResponse;
import application.service.PlatformService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/platform")
public class PlatformController {

    private final LanguageResponse languageResponse;
    private final PlatformService platformService;

    public PlatformController(LanguageResponse languageResponse, PlatformService platformService) {
        this.languageResponse = languageResponse;
        this.platformService = platformService;
    }

    @GetMapping("/languages")
    private ResponseEntity<LanguageResponse> getLanguages() {
        return new ResponseEntity<>(platformService.getLanguage(), HttpStatus.OK);
    }
}
