package application.controllers;

import application.models.City;
import application.models.Country;
import application.models.Language;
import application.models.dto.MessageResponseDto;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
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
    public ResponseEntity<GeneralListResponse<Language>> getLanguages(
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService.getLanguage(), offset, itemPerPage));
    }

    @GetMapping("/countries")
    public ResponseEntity<GeneralListResponse<Country>> getCountry(
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService.getCountry(),
                offset, itemPerPage));
    }

    @GetMapping("/cities")
    public ResponseEntity<GeneralListResponse<City>> getAllCity(
            @RequestParam(value = "offset", required = false, defaultValue = "0") Integer offset,
            @RequestParam(value = "itemPerPage", required = false, defaultValue = "20") Integer itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService
                .getCity(), offset, itemPerPage));
    }


    @PostMapping("/cities")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setCity(@RequestBody City city) {
        return ResponseEntity.ok(new GeneralResponse<>(platformService.setUserCity(city.getTitle())));
    }

    @PostMapping("/countries")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setCountry(@RequestBody Country country) {
        return ResponseEntity.ok(new GeneralResponse<>(platformService.setCountry(country.getTitle())));
    }
}
