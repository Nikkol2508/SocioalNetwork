package application.controllers;

import application.models.Post;
import application.models.dto.MessageResponseDto;
import application.models.dto.PersonDto;
import application.models.dto.PostDto;
import application.models.requests.PersonSettingsDtoRequest;
import application.models.requests.PostRequest;
import application.models.responses.GeneralListResponse;
import application.models.responses.GeneralResponse;
import application.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<GeneralResponse<PersonDto>> getProfile() {

        log.info("getProfile(): start():");
        log.debug("getProfile()");
        GeneralResponse<PersonDto> profile = new GeneralResponse<>(profileService.getProfile());
        log.debug("getProfile(): {}", profile);
        log.info("getProfile(): finish():");
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse<PersonDto>> getPerson(@PathVariable int id) {

        log.info("getPerson(id): start():");
        log.debug("getPerson(id), id = {}", id);
        GeneralResponse<PersonDto> profile = new GeneralResponse<>(profileService.getPerson(id));
        log.debug("getPerson(id): , response = {}", id, profile);
        log.info("getPerson(id = {}): finish():", id);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<GeneralListResponse<PostDto>> getWall(
            @PathVariable int id,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "5", required = false) int itemPerPage) {

        log.info("getWall(id = {}): start():", id);
        log.debug("getWall(id), id = {}", id);
        GeneralListResponse<PostDto> response = new GeneralListResponse<>
                (profileService.getWall(id), offset, itemPerPage);
        log.debug("getWall(id = {}), response = {}", id, response);
        log.info("getPWall(id = {}): finish():", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<GeneralListResponse<PersonDto>> getPersons(
            @RequestParam(value = "first_or_last_name", required = false) String firstOrLastName,
            @RequestParam(value = "first_name", required = false) String firstName,
            @RequestParam(value = "last_name", required = false) String lastName,
            @RequestParam(value = "age_from", required = false) Long ageFrom,
            @RequestParam(value = "age_to", required = false) Long ageTo,
            @RequestParam(value = "country", required = false) String country,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "itemPerPage", defaultValue = "20", required = false) int itemPerPage) {

        log.info("searchPerson: start():");
        log.debug("searchPerson: request: firstOrLastName = {}, firstName = {}, lastName = {}, " +
                "ageFrom = {}, ageTo = {}, country = {}, city = {}", firstOrLastName, firstName, lastName,
                ageFrom, ageTo, country, city);
        GeneralListResponse<PersonDto> response = new GeneralListResponse<>
                (profileService.getPersons(firstOrLastName, firstName, lastName, ageFrom, ageTo, country, city),
                        offset, itemPerPage);
        log.debug("searchPerson: response = {}", response);
        log.info("searchPerson: finish():");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<GeneralResponse<PersonDto>> updateProfile(
            @RequestBody PersonSettingsDtoRequest request) throws ParseException, InterruptedException {

        log.info("changeProfile: start():");
        log.debug("changeProfile: request = {}", request);
        GeneralResponse<PersonDto> response = new GeneralResponse<>(profileService.changeProfile(request));
        log.debug("changeProfile: response = {}", response);
        log.info("changeProfile: finish():");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/wall")
    public ResponseEntity<GeneralResponse<Post>> addPost(
            @PathVariable int id,
            @RequestParam(value = "publish_date", required = false) Long publishDate,
            @RequestBody PostRequest postRequest) {

        log.info("setPost: start():");
        log.debug("setPost: id = {}, postRequest = {}, publishDate = {}", id, postRequest, publishDate);
        GeneralResponse<Post> response = new GeneralResponse<>(profileService.setPost(id, publishDate, postRequest));
        log.debug("setPost: response = {}", response);
        log.info("setPost: finish():");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<GeneralResponse<MessageResponseDto>> deleteProfile() {

        log.info("deleteProfile: start():");
        log.debug("deleteProfile: ");
        GeneralResponse<MessageResponseDto> response = new GeneralResponse<>(profileService.deleteProfile());
        log.debug("deleteProfile: response = {}", response);
        log.info("deleteProfile: finish():");

        return ResponseEntity.ok(new GeneralResponse<>(profileService.deleteProfile()));
    }
}
