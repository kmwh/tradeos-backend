package io.github.kmwh.tradeos_backend.report.controller;

import io.github.kmwh.tradeos_backend.report.dto.ReportResponseDto;
import io.github.kmwh.tradeos_backend.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

  private final ReportService reportService;

  // @PostMapping
  // public ResponseEntity<ReportResponseDto> generateReport(@AuthenticationPrincipal Long userId) {
  // return ResponseEntity.ok(tendencyReportService.generatePerformanceReport(userId));
  // }

  @GetMapping("/latest")
  public ResponseEntity<ReportResponseDto> getLatestReport(@AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(reportService.getLatestReport(userId));
  }

  @GetMapping("/history")
  public ResponseEntity<Page<ReportResponseDto>> getReportHistory(
      @AuthenticationPrincipal Long userId, Pageable pageable) {
    return ResponseEntity.ok(reportService.getReportHistory(userId, pageable));
  }
}
