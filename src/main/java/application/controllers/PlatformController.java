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
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService.getCountry(country), offset, itemPerPage));
    }

    @GetMapping("/cities")
    public ResponseEntity<GeneralListResponse<City>> getLCity(@RequestParam Integer countryId,
                                                               @RequestParam String country,
                                                               @RequestParam Integer offset,
                                                               @RequestParam Integer itemPerPage) {

        return ResponseEntity.ok(new GeneralListResponse<>(platformService
                .getCity(countryId, country), offset, itemPerPage));
    }

    @PostMapping("/cities")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setCity(@RequestParam(value = "city") String city) {
        return ResponseEntity.ok(new GeneralResponse<>(platformService.setUserCity(city)));
    }

    @PostMapping("/countries")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> setCountry
            (@RequestParam(value = "country") String country){
        return ResponseEntity.ok(new GeneralResponse<>(platformService.setCountry(country)));
    }
}
