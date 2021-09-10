package application.models;

import lombok.Data;

@Data
public class FileDescription {

  private int id;
  private int ownerId;
  private String fileName;
  private String relativeFilePath;
  private String rawFileURL;
  private String fileFormat;
  private int bytes;
  private String fileType;
  private byte[] data;
  private long createdAt;
}
