package application.service;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

@Slf4j
@Service
public class DropboxService {

    public static Calendar calendar = Calendar.getInstance();
    public static int daysBeforeDeletion = -5;
    private static final String ACCESS_TOKEN = "j45ajYtbzMUAAAAAAAAAARzdRDRVoUgtUPodzflFti0_KGFyh6xvANTFallXvuOa";

    private static DbxClientV2 getDbxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        return new DbxClientV2(config, ACCESS_TOKEN);
    }

    public static void saveInDropbox() throws IOException, DbxException {

        File dir = new File("logs/toDropbox");
        for (File file : dir.listFiles()) {
            try (InputStream in = new FileInputStream("logs/toDropbox/" + file.getName())) {
                getDbxClient().files().uploadBuilder("/logs/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + file.getName())
                        .uploadAndFinish(in);
                Files.delete(Paths.get("logs/toDropbox/" + file.getName()));
            }
        }
    }

    public static void deleteFromDropbox() {

        calendar.roll(Calendar.DAY_OF_MONTH, daysBeforeDeletion);
        try {
            getDbxClient().files().deleteV2("/logs/" + calendar.get(Calendar.DAY_OF_MONTH));
        } catch (DbxException e) {
            log.error("File not found: stackTrace {}", (Object) e.getStackTrace());
        }
    }

    public void saveImageToDropbox(MultipartFile file, String name) throws IOException, DbxException {

        getDbxClient().files().uploadBuilder("/storage/" + name)
                .uploadAndFinish(file.getInputStream());
    }

    public void deleteImageFromDropbox(String fileName) {
        try {
            getDbxClient().files().deleteV2("/" + fileName);
        } catch (DbxException e) {
            log.error("File not found: stackTrace {}", (Object) e.getStackTrace());
        }
    }

    public byte[] getImageFromDropbox(String fileName) throws DbxException, IOException {
        return getDbxClient().files().download("/storage/" + fileName).getInputStream().readAllBytes();
    }

}
