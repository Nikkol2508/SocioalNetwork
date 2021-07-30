package application.controllers;

import application.models.City;
import application.models.Country;
import application.models.Language;
import application.responses.GeneralListResponse;
import application.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/languages")
    private ResponseEntity<GeneralListResponse<Language>> getLanguages() {
        return ResponseEntity.ok(platformService.getLanguage());
    }

    @GetMapping("/countries")
    private ResponseEntity<GeneralListResponse<Country>> getCountry(@RequestParam String country, @RequestParam Integer offset, @RequestParam Integer itemPerPage) {
        return ResponseEntity.ok(platformService.getCountry(country, offset, itemPerPage));
    }

    @GetMapping("/cities")
    private ResponseEntity<GeneralListResponse<City>> getLCity(@RequestParam Integer countryId, @RequestParam String country, @RequestParam Integer offset, @RequestParam Integer itemPerPage) {
        return ResponseEntity.ok(platformService.getCity(countryId, country, offset, itemPerPage));
    }
}
