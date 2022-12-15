package kr.co.ok0.client.telegram.configuration;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class TelegramClientConfiguration {
  private static final String CHAT_ID_PROPERTY_NAME = "chat_id";

  @Bean
  public RequestInterceptor requestInterceptor(@Value("${telegram-api.chat-id}") String chatId) {
    return template -> {
      RequestTemplate requestTemplate = new RequestTemplate();
      requestTemplate.query(CHAT_ID_PROPERTY_NAME, chatId);
    };
  }

  @Bean
  public Logger.Level feignLoggerLevel() {
    return Logger.Level.FULL;
  }
}
