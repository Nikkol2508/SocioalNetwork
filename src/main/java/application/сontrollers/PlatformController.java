package application.Controllers;

import application.responses.LanguageResponse;
import application.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final LanguageResponse languageResponse;
    private final PlatformService platformService;

    @GetMapping("/languages")
    private ResponseEntity<LanguageResponse> getLanguages() {
        return ResponseEntity.ok(platformService.getLanguage());
    }
}
