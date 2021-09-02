package application.service;

import application.dao.DaoFile;
import application.dao.DaoPerson;
import application.models.FileDescription;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final DaoFile daoFile;
    private final DaoPerson daoPerson;

    public FileDescription saveFileInStorage(String type, MultipartFile file) {
        FileDescription fileDto = new FileDescription();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String path = "src/main/resources/public/storage/" + file.getOriginalFilename();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(path));
                stream.write(bytes);
                stream.close();

                fileDto.setOwnerId(daoPerson.getPersonIdByEmail(
                        SecurityContextHolder.getContext().getAuthentication().getName()));
                fileDto.setName(file.getOriginalFilename());
                String servletPath = "storage/" + file.getOriginalFilename();
                fileDto.setRelativeFilePath(servletPath);
                fileDto.setRawFileURL("какой то урл");
                fileDto.setFormat(file.getContentType());
                fileDto.setBytes(bytes.length);
                fileDto.setType(type);
                fileDto = daoFile.saveAndReturn(fileDto);
                return fileDto;
            } catch (Exception e) {
                return null;
            }
        } else {
            return fileDto;
        }
    }
}
