package application.controllers;

import application.models.FileDescription;
import application.models.responses.GeneralResponse;
import application.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {

  private final StorageService storageService;

  @PostMapping
  public ResponseEntity<GeneralResponse<FileDescription>> handleFileUpload(
      @RequestParam("type") String type,
      @RequestPart("file") MultipartFile file) {

    return ResponseEntity.ok(storageService.saveFileInStorage(type, file));
    }
  }

