package application.controllers;

import application.models.FileDescription;
import application.models.responses.GeneralResponse;
import application.service.DropboxService;
import application.service.StorageService;
import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;
    private final DropboxService dropboxService;

    @PostMapping("/api/v1/storage")
    public ResponseEntity<GeneralResponse<FileDescription>> handleFileUpload(
            @RequestParam("type") String type,
            @RequestPart("file") MultipartFile file) throws IOException, DbxException {

        log.info("saveFileInStorage(): start():");
        log.debug("saveFileInStorage(): type = {}, file = {}", type, file);
        GeneralResponse<FileDescription> response = new GeneralResponse<>(storageService.saveFileInStorage(type, file));
        log.debug("saveFileInStorage(): response = {}", response);
        log.info("saveFileInStorage(): finish():");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/storage/{imageName}")
    public ResponseEntity<Object> getImage(@PathVariable("imageName") String imageName)
            throws IOException, DbxException {
        return ResponseEntity.ok().contentType(MediaType.valueOf(storageService.getImage(imageName).getFileFormat()))
                .body(dropboxService.getImageFromDropbox(imageName));
    }

    @GetMapping("/profile/storage/{imageName}")
    public ResponseEntity<Object> getImageInProfile(@PathVariable("imageName") String imageName)
            throws IOException, DbxException {
        return ResponseEntity.ok().contentType(MediaType.valueOf(storageService.getImage(imageName).getFileFormat()))
                .body(dropboxService.getImageFromDropbox(imageName));
    }
}


