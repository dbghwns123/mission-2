package com.wiseSaying_11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WiseSayingService {

  private final WiseSayingRepository repository;

  // 메모리 상태
  private final List<WiseSaying> quotes = new ArrayList<>();
  private int nextId = 1;

  public WiseSayingService(WiseSayingRepository repository) {
    this.repository = repository;
  }

  /* 초기 로딩: 파일DB → 메모리, nextId 복원 */
  public void init() throws IOException {
    quotes.clear();
    quotes.addAll(repository.loadAll());

    // lastId.txt와 파일에서 읽은 maxId 중 더 큰 값 + 1
    int lastIdFromFile = repository.loadLastId();
    int maxIdInQuotes = 0;
    for (WiseSaying q : quotes) {
      if (q.getId() > maxIdInQuotes) maxIdInQuotes = q.getId();
    }
    nextId = Math.max(lastIdFromFile, maxIdInQuotes) + 1;
  }

  /* 등록 */
  public WiseSaying register(String content, String author) throws IOException {
    WiseSaying ws = new WiseSaying(nextId++, author, content);
    quotes.add(ws);
    repository.save(ws);
    repository.saveLastId(ws.getId()); // 번호 재사용 방지용 lastId 유지
    return ws;
  }

  /* 전체 조회 (읽기 전용 뷰) */
  public List<WiseSaying> findAll() {
    return Collections.unmodifiableList(quotes);
  }

  /* 단건 조회 (컨트롤러의 프롬프트에 사용) */
  public WiseSaying findById(int id) {
    for (WiseSaying q : quotes) {
      if (q.getId() == id) return q;
    }
    return null;
  }

  /* 수정 */
  public WiseSaying update(int id, String newContent, String newAuthor) throws IOException {
    WiseSaying target = findById(id);
    if (target == null) return null;
    target.setContent(newContent);
    target.setAuthor(newAuthor);
    repository.save(target); // 같은 {id}.json 덮어쓰기
    return target;
  }

  /* 삭제 */
  public boolean delete(int id) throws IOException {
    WiseSaying target = findById(id);
    if (target == null) return false;
    quotes.remove(target);
    repository.delete(id);
    return true;
  }

  /* 빌드(data.json) */
  public void buildDataJson() throws IOException {
    repository.buildDataJson(quotes);
  }
}

