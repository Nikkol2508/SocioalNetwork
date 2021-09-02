package application.models;

import lombok.Data;

@Data
public class FileDescription {

  private int id;
  private int ownerId;
  private String name;
  private String relativeFilePath;
  private String rawFileURL;
  private String format;
  private int bytes;
  private String type;
  private long time;
}
