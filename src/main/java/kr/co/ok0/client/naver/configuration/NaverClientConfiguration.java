package kr.co.ok0.client.naver.configuration;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class NaverClientConfiguration {
  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
