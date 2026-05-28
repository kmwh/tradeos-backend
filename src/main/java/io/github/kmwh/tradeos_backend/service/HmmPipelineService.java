package io.github.kmwh.tradeos_backend.service;

import io.github.kmwh.tradeos_backend.entity.HmmHistory;
import io.github.kmwh.tradeos_backend.repository.HmmHistoryRepository;
import io.github.kmwh.tradeos_backend.dto.HmmPredictResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class HmmPipelineService {

    private final RestClient restClient;
    private final HmmHistoryRepository hmmHistoryRepository;

    private static final String FASTAPI_URL = "http://localhost:8000/api/v1/hmm/predict?symbol=BTC/USDT";

    @Transactional
    public void fetchAndSaveHmmScore() {
        try {
            HmmPredictResponseDto response = restClient.get()
                    .uri(FASTAPI_URL)
                    .retrieve()
                    .body(HmmPredictResponseDto.class);

            if (response != null) {
                HmmHistory history = HmmHistory.builder()
                        .timestamp(response.getTimestamp())
                        .symbol(response.getSymbol())
                        .trendScore(response.getTrend_score())
                        .build();

                hmmHistoryRepository.save(history);
                log.info("성공: [시간: {}, 점수: {}]", history.getTimestamp(), history.getTrendScore());
            }
        } catch (Exception e) {
            log.error("실패: 통신 중 오류 발생", e);
        }
    }
}