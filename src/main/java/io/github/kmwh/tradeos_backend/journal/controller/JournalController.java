package io.github.kmwh.tradeos_backend.journal.controller;

import io.github.kmwh.tradeos_backend.global.dto.MessageResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalDetailResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalIdResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalListResponseDto;
import io.github.kmwh.tradeos_backend.journal.dto.JournalRequestDto;
import io.github.kmwh.tradeos_backend.journal.service.JournalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
public class JournalController {

  private final JournalService journalService;

  @PostMapping
  public ResponseEntity<JournalIdResponseDto> createJournal(@AuthenticationPrincipal Long userId,
      @Valid @RequestBody JournalRequestDto requestDto) {
    return ResponseEntity.ok(journalService.createJournal(userId, requestDto));
  }

  @GetMapping
  public ResponseEntity<Page<JournalListResponseDto>> getJournals(
      @AuthenticationPrincipal Long userId, Pageable pageable) {
    return ResponseEntity.ok(journalService.getJournals(userId, pageable));
  }

  @GetMapping("/{journalId}")
  public ResponseEntity<JournalDetailResponseDto> getJournalDetail(
      @AuthenticationPrincipal Long userId, @PathVariable("journalId") Long journalId) {
    return ResponseEntity.ok(journalService.getJournalDetail(userId, journalId));
  }

  @PutMapping("/{journalId}")
  public ResponseEntity<MessageResponseDto> updateJournal(@AuthenticationPrincipal Long userId,
      @PathVariable("journalId") Long journalId, @RequestBody JournalRequestDto requestDto) {
    journalService.updateJournal(userId, journalId, requestDto);
    return ResponseEntity.ok(new MessageResponseDto("매매 일지가 성공적으로 수정되었습니다."));
  }

  @DeleteMapping("/{journalId}")
  public ResponseEntity<MessageResponseDto> deleteJournal(@AuthenticationPrincipal Long userId,
      @PathVariable("journalId") Long journalId) {
    journalService.deleteJournal(userId, journalId);
    return ResponseEntity.ok(new MessageResponseDto("매매 일지가 삭제되었습니다."));
  }
}
