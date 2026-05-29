package io.github.kmwh.tradeos_backend.quant.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import io.github.kmwh.tradeos_backend.quant.service.HmmPipelineService;

@Slf4j
@Component
@RequiredArgsConstructor
public class HmmScheduler {

  private final HmmPipelineService hmmPipelineService;

  // 4시간마다 실행
  // @Scheduled(cron = "0 0 0,4,8,12,16,20 * * *")

  // 테스트로 1분마다 실행
  @Scheduled(cron = "0 * * * * *")
  public void runHmmPipeline() {
    log.info("===== HMM 데이터 수집 스케줄러 작동 시작 =====");
    hmmPipelineService.fetchAndSaveHmmScore();
  }
}
