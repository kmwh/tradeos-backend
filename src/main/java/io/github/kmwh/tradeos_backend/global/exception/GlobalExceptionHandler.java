package io.github.kmwh.tradeos_backend.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.warn("잘못된 요청: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<?> handleIllegalStateException(IllegalStateException ex) {
    log.warn("상태 예외: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
    // 🌟 치명적 수정: 이제 백엔드에 진짜 에러 원인이 출력됩니다!
    log.error("서버 내부 오류가 발생했습니다.", ex);

    return ResponseEntity.internalServerError()
        .body(Map.of("error", "서버 내부 오류가 발생했습니다. " + ex.getMessage()));
  }
}
