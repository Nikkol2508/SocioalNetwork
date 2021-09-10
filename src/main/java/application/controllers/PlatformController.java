package application.controllers;

import application.models.City;
import application.models.Country;
import application.models.Language;
import application.models.responses.GeneralListResponse;
import application.service.PlatformService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/platform")
@RequiredArgsConstructor
public class PlatformController {

    private final PlatformService platformService;

    @GetMapping("/languages")
    private ResponseEntity<GeneralListResponse<Language>> getLanguages(
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService.getLanguage(), offset, itemPerPage));
    }

    @GetMapping("/countries")
    private ResponseEntity<GeneralListResponse<Country>> getCountry(
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService.getCountry(country), offset, itemPerPage));
    }

    @GetMapping("/cities")
    private ResponseEntity<GeneralListResponse<City>> getLCity(@RequestParam Integer countryId,
                                                               @RequestParam String country,
                                                               @RequestParam Integer offset,
                                                               @RequestParam Integer itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService
                .getCity(countryId, country), offset, itemPerPage));
    }
}
