package com.wiseSaying_11;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class WiseSayingRepository {

  private final Path baseDir = Paths.get("db", "wiseSaying");
  private final Path lastIdPath = baseDir.resolve("lastId.txt");
  private final Path dataJsonPath = baseDir.resolve("data.json");

  public WiseSayingRepository() {
    try {
      Files.createDirectories(baseDir);
    } catch (IOException e) {
      throw new RuntimeException("저장 폴더 생성 실패: " + e.getMessage(), e);
    }
  }

  /* 파일 → 메모리 */
  public List<WiseSaying> loadAll() throws IOException {
    List<WiseSaying> result = new ArrayList<>();
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir, "*.json")) {
      for (Path p : stream) {
        if (p.getFileName().toString().equals("data.json")) continue;
        String json = Files.readString(p);
        result.add(WiseSaying.fromJson(json));
      }
    }
    return result;
  }

  public int loadLastId() {
    try {
      if (Files.exists(lastIdPath)) {
        String s = Files.readString(lastIdPath).trim();
        if (!s.isEmpty()) return Integer.parseInt(s);
      }
    } catch (IOException | NumberFormatException ignored) {}
    return 0;
  }

  public void saveLastId(int lastId) throws IOException {
    Files.writeString(lastIdPath, String.valueOf(lastId));
  }

  /* 단건 저장/갱신 */
  public void save(WiseSaying ws) throws IOException {
    Path filePath = baseDir.resolve(ws.getId() + ".json");
    Files.writeString(filePath, ws.toJson());
  }

  /* 단건 삭제 */
  public void delete(int id) throws IOException {
    Path filePath = baseDir.resolve(id + ".json");
    Files.deleteIfExists(filePath);
  }

  /* data.json 빌드 */
  public void buildDataJson(List<WiseSaying> quotes) throws IOException {
    StringBuilder sb = new StringBuilder();
    sb.append("[\n");
    for (int i = 0; i < quotes.size(); i++) {
      WiseSaying q = quotes.get(i);
      sb.append("  ").append(q.toJson().trim().replace("\n", "\n  "));
      if (i < quotes.size() - 1) sb.append(",");
      sb.append("\n");
    }
    sb.append("]\n");
    Files.writeString(dataJsonPath, sb.toString());
  }
}

