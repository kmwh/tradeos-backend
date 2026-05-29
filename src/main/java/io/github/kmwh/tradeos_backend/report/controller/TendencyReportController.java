package io.github.kmwh.tradeos_backend.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.github.kmwh.tradeos_backend.report.dto.TendencyReportResponseDto;
import io.github.kmwh.tradeos_backend.report.service.TendencyReportService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class TendencyReportController {

  private final TendencyReportService tendencyReportService;

  // 테스트용 수동 생성 요청
  @PostMapping("/tendency")
  public ResponseEntity<TendencyReportResponseDto> generateTendencyReport(
      @AuthenticationPrincipal Long userId, @RequestParam(defaultValue = "WEEKLY") String period) {

    TendencyReportResponseDto report = tendencyReportService.generateTendencyReport(userId, period);
    return ResponseEntity.ok(report);
  }

  @GetMapping("/tendency/latest")
  public ResponseEntity<TendencyReportResponseDto> getLatestTendencyReport(
      @AuthenticationPrincipal Long userId, @RequestParam(defaultValue = "WEEKLY") String period) {

    TendencyReportResponseDto report =
        tendencyReportService.getLatestTendencyReport(userId, period);
    return ResponseEntity.ok(report);
  }

  @GetMapping("/tendency/history")
  public ResponseEntity<List<TendencyReportResponseDto>> getTendencyReportHistory(
      @AuthenticationPrincipal Long userId) {

    List<TendencyReportResponseDto> history =
        tendencyReportService.getTendencyReportHistory(userId);
    return ResponseEntity.ok(history);
  }
}
