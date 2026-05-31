package io.github.kmwh.tradeos_backend.quant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import io.github.kmwh.tradeos_backend.quant.dto.HmmPredictResponseDto;
import io.github.kmwh.tradeos_backend.quant.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.quant.repository.HmmHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmmPipelineService {

  private final RestClient restClient;
  private final HmmHistoryRepository hmmHistoryRepository;

  @Value("${external.ai.hmm-url}")
  private String fastApiHmmUrl;

  @Transactional
  public void fetchAndSaveHmmScore() {
    try {
      HmmPredictResponseDto response =
          restClient.get().uri(fastApiHmmUrl).retrieve().body(HmmPredictResponseDto.class);

      if (response != null) {
        HmmHistory history = HmmHistory.builder().timestamp(response.timestamp())
            .symbol(response.symbol()).trendScore(response.trendScore()).build();

        hmmHistoryRepository.save(history);
        log.info("성공: [시간: {}, 점수: {}]", history.getTimestamp(), history.getTrendScore());
      }
    } catch (Exception e) {
      log.error("실패: 통신 중 오류 발생", e);
    }
  }
}
