package io.github.kmwh.tradeos_backend.report.controller;

import io.github.kmwh.tradeos_backend.report.dto.ReportResponseDto;
import io.github.kmwh.tradeos_backend.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService tendencyReportService;

  @PostMapping
  public ResponseEntity<ReportResponseDto> generateReport(@AuthenticationPrincipal Long userId,
      @RequestParam(value = "period", required = false) String period) {
    return ResponseEntity.ok(tendencyReportService.generatePerformanceReport(userId));
  }

  @GetMapping("/latest")
  public ResponseEntity<ReportResponseDto> getLatestReport(@AuthenticationPrincipal Long userId,
      @RequestParam(value = "period", required = false) String period) {
    return ResponseEntity.ok(tendencyReportService.getLatestReport(userId));
  }

  @GetMapping("/history")
  public ResponseEntity<List<ReportResponseDto>> getReportHistory(
      @AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(tendencyReportService.getReportHistory(userId));
  }
}
