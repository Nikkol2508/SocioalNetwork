package application.controllers;

import application.models.FileDescription;
import application.models.responses.GeneralResponse;
import application.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;
    private final ResourceLoader resourceLoader;

    @PostMapping("/api/v1/storage")
    public ResponseEntity<GeneralResponse<FileDescription>> handleFileUpload(
            @RequestParam("type") String type,
            @RequestPart("file") MultipartFile file) throws IOException {

        return ResponseEntity.ok(new GeneralResponse<>(storageService.saveFileInStorage(type, file)));
    }

    @GetMapping("storage/{imageName}")
    public ResponseEntity<Object> show(@PathVariable("imageName") String imageName) throws IOException {
        FileDescription image = storageService.getImage(imageName);
        return ResponseEntity.ok().contentType(MediaType.valueOf(image.getFileFormat())).body(image.getData());
    }
}


