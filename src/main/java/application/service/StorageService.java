package application.service;

import application.dao.DaoFile;
import application.dao.DaoPerson;
import application.models.FileDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final DaoFile daoFile;
    private final DaoPerson daoPerson;

    public FileDescription saveFileInStorage(String type, MultipartFile file) throws IOException {
        FileDescription fileDto = new FileDescription();
        if (!file.isEmpty()) {
            daoFile.deleteImage(daoPerson.getAuthPerson().getId());
            fileDto.setOwnerId(daoPerson.getAuthPerson().getId());
            String fileName = file.getOriginalFilename().replace(".", "") + System.currentTimeMillis();
            fileDto.setFileName(fileName);
            fileDto.setRelativeFilePath("storage/" + fileName);
            fileDto.setRawFileURL("какой то урл");
            fileDto.setFileFormat(file.getContentType());
            fileDto.setBytes(file.getBytes().length);
            fileDto.setFileType(type);
            fileDto.setData(file.getBytes());
            fileDto = daoFile.saveAndReturn(fileDto);
            return fileDto;
        } else {
            return fileDto;
        }
    }

    public FileDescription getImage(String photoId) throws IOException {
        return daoFile.getByImageName(photoId);
    }
}
