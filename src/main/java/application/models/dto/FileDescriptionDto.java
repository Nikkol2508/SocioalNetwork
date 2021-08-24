package application.models.dto;

import lombok.Data;

@Data
public class FileDescriptionDto {

  private String ownerId;
  private String fileName;//+
  private String relativeFilePath;//+
  private String rawFileURL;//+
  private String fileFormat;//+
  private int bytes; //+
  private String fileType;//+
  private long createdAt; //+
}
