package application.service;

import application.dao.DaoFile;
import application.dao.DaoPerson;
import application.models.FileDescription;
import com.dropbox.core.DbxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final DaoFile daoFile;
    private final DaoPerson daoPerson;
    private final DropboxService dropboxService;

    public FileDescription saveFileInStorage(String type, MultipartFile file) throws IOException, DbxException {
        FileDescription fileDto = new FileDescription();
        if (!file.isEmpty()) {
            if (daoPerson.getAuthPerson().getPhoto() != null){
            dropboxService.deleteImageFromDropbox(daoPerson.getAuthPerson().getPhoto());
            daoFile.deleteImage(daoPerson.getAuthPerson().getId());
            }
            fileDto.setOwnerId(daoPerson.getAuthPerson().getId());
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            dropboxService.saveImageToDropbox(file, fileName);
            fileDto.setFileName(fileName);
            fileDto.setRelativeFilePath("storage/" + fileName);
            fileDto.setRawFileURL(null);
            fileDto.setFileFormat(file.getContentType());
            fileDto.setBytes(file.getBytes().length);
            fileDto.setFileType(type);

            fileDto = daoFile.saveAndReturn(fileDto);
        }
        return fileDto;
    }

    public FileDescription getImage(String photoId) {
        return daoFile.getByImageName(photoId);
    }
}
