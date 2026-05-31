package io.github.kmwh.tradeos_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class TradeosBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(TradeosBackendApplication.class, args);
  }

}
