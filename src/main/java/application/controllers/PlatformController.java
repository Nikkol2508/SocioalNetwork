package application.controllers;

import application.models.Language;
import application.responses.GeneralListResponse;
import application.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/languages")
    private ResponseEntity<GeneralListResponse<Language>> getLanguages() {
        return ResponseEntity.ok(platformService.getLanguage());
    }
}
