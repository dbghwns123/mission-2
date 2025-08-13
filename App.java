package com.wiseSaying_11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {

  private final BufferedReader br;
  private final WiseSayingController controller;

  public App() {
    this.br = new BufferedReader(new InputStreamReader(System.in));

    // 의존성 조립 (App이 조립만, 로직 없음)
    WiseSayingRepository repository = new WiseSayingRepository();
    WiseSayingService service = new WiseSayingService(repository);
    this.controller = new WiseSayingController(br, service);
  }

  public void run() {
    System.out.println("== 명언 앱 ==");

    // 초기 로딩: 파일DB → 메모리
    try {
      controller.init(); // service.init()까지 호출됨
    } catch (IOException e) {
      System.out.println("초기 로딩 실패: " + e.getMessage());
      return;
    }

    while (true) {
      System.out.print("명령) ");
      String cmd;
      try {
        cmd = br.readLine();
      } catch (IOException e) {
        System.out.println("입력 오류");
        break;
      }
      if (cmd == null) break;
      cmd = cmd.trim();

      if (cmd.equals("종료")) break;

      // App은 “이 명령이 컨트롤러로 갈 일인가?”만 판단 → 넘김
      if (cmd.equals("등록")
          || cmd.equals("목록")
          || cmd.startsWith("삭제?id=")
          || cmd.startsWith("수정?id=")
          || cmd.equals("빌드")) {
        controller.handle(cmd);
      } else {
        System.out.println("지원하지 않는 명령입니다.");
      }
    }
  }
}
