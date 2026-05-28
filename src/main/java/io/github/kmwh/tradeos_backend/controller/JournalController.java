package io.github.kmwh.tradeos_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journals")
@Tag(name = "매매 일지 API")
public class JournalController {

    @Operation(summary = "매매 일지 목록 조회")
    @GetMapping
    public ResponseEntity<?> getJournals() {
        return ResponseEntity.ok("Journal List");
    }

    @Operation(summary = "매매 일지 단일 조회")
    @GetMapping("/{journalId}")
    public ResponseEntity<?> getJournalDetail(@PathVariable Long journalId) {
        return ResponseEntity.ok("Journal Detail " + journalId);
    }

    @Operation(summary = "매매 일지 작성")
    @PostMapping
    public ResponseEntity<?> createJournal(@RequestBody Object requestDto) {
        return ResponseEntity.ok("Journal Created");
    }

    @Operation(summary = "AI 매매 피드백 요청")
    @PostMapping("/{journalId}/analyze")
    public ResponseEntity<?> requestAiAnalysis(@PathVariable Long journalId) {
        return ResponseEntity.ok("AI Analysis Requested");
    }
}