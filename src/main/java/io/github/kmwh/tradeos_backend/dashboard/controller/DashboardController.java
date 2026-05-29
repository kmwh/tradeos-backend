package io.github.kmwh.tradeos_backend.dashboard.controller;

import io.github.kmwh.tradeos_backend.dashboard.dto.DashboardMetricsResponseDto;
import io.github.kmwh.tradeos_backend.dashboard.dto.PnlChartResponseDto;
import io.github.kmwh.tradeos_backend.dashboard.service.DashboardService;
import io.github.kmwh.tradeos_backend.quant.dto.HmmChartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

  private final DashboardService dashboardService;

  @GetMapping("/metrics")
  public ResponseEntity<DashboardMetricsResponseDto> getDashboardMetrics(
      @AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(dashboardService.getDashboardMetrics(userId));
  }

  @GetMapping("/charts/pnl")
  public ResponseEntity<List<PnlChartResponseDto>> getPnlChart(
      @AuthenticationPrincipal Long userId) {
    return ResponseEntity.ok(dashboardService.getPnlChartData(userId));
  }

  @GetMapping("/charts/hmm")
  public ResponseEntity<List<HmmChartDto>> getHmmChart() {
    return ResponseEntity.ok(dashboardService.getHmmChartData());
  }
}
