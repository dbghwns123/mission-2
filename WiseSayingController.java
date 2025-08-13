package com.wiseSaying_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;

public class WiseSayingController {

  private final BufferedReader br;
  private final WiseSayingService service;

  public WiseSayingController(BufferedReader br, WiseSayingService service) {
    this.br = br;
    this.service = service;
  }

  // 앱 시작 시 파일에서 로딩
  public void init() throws IOException {
    service.init(); // 파일DB → 메모리, nextId 복원
  }

  public void handle(String cmd) {
    try {
      if (cmd.equals("등록")) {
        handleRegister();
      } else if (cmd.equals("목록")) {
        handleList();
      } else if (cmd.startsWith("삭제?id=")) {
        handleDelete(cmd);
      } else if (cmd.startsWith("수정?id=")) {
        handleEdit(cmd);
      } else if (cmd.equals("빌드")) {
        handleBuild();
      }
    } catch (IOException e) {
      System.out.println("처리 중 오류: " + e.getMessage());
    }
  }

  private void handleRegister() throws IOException {
    System.out.print("명언 : ");
    String content = br.readLine();

    System.out.print("작가 : ");
    String author = br.readLine();

    WiseSaying saved = service.register(content, author);
    System.out.println(saved.getId() + "번 명언이 등록되었습니다.");
  }

  private void handleList() {
    System.out.println("번호 / 작가 / 명언");
    System.out.println("---------------------------");
    List<WiseSaying> all = service.findAll();

    // 최신 등록이 위로 나오도록 역순 출력
    ListIterator<WiseSaying> it = all.listIterator(all.size());
    while (it.hasPrevious()) {
      WiseSaying q = it.previous();
      System.out.println(q.getId() + " / " + q.getAuthor() + " / " + q.getContent());
    }
  }

  private void handleDelete(String cmd) throws IOException {
    int id = parseId(cmd);
    if (id < 0) {
      System.out.println("id 파라미터가 올바르지 않습니다. 예) 삭제?id=1");
      return;
    }
    boolean ok = service.delete(id);
    if (ok) System.out.println(id + "번 명언이 삭제되었습니다.");
    else    System.out.println(id + "번 명언은 존재하지 않습니다.");
  }

  private void handleEdit(String cmd) throws IOException {
    int id = parseId(cmd);
    if (id < 0) {
      System.out.println("id 파라미터가 올바르지 않습니다. 예) 수정?id=2");
      return;
    }
    WiseSaying found = service.findById(id);
    if (found == null) {
      System.out.println(id + "번 명언은 존재하지 않습니다.");
      return;
    }

    System.out.println("명언(기존) : " + found.getContent());
    System.out.print("명언 : ");
    String newContent = br.readLine();

    System.out.println("작가(기존) : " + found.getAuthor());
    System.out.print("작가 : ");
    String newAuthor = br.readLine();

    service.update(id, newContent, newAuthor);
    // 별도 메시지 요구사항 없었지만 친절 메시지 넣고 싶다면 가능
    // System.out.println(id + "번 명언이 수정되었습니다.");
  }

  private void handleBuild() throws IOException {
    service.buildDataJson();
    System.out.println("data.json 파일의 내용이 갱신되었습니다.");
  }

  private int parseId(String cmd) {
    try {
      int p = cmd.indexOf("id=");
      if (p < 0) return -1;
      return Integer.parseInt(cmd.substring(p + 3).trim());
    } catch (Exception e) {
      return -1;
    }
  }
}
