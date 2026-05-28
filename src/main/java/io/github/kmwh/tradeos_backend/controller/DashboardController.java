package io.github.kmwh.tradeos_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "대시보드 API")
public class DashboardController {

    @Operation(summary = "대시보드 메트릭스 조회")
    @GetMapping("/metrics")
    public ResponseEntity<?> getDashboardMetrics() {
        return ResponseEntity.ok("Dashboard Metrics");
    }
}