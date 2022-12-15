package kr.co.ok0.client.telegram;

import kr.co.ok0.client.telegram.configuration.TelegramClientConfiguration;
import kr.co.ok0.client.telegram.dto.TelegramSendMessageReqI;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
  name = "telegram-api",
  url = "${telegram-api.host}",
  configuration = {TelegramClientConfiguration.class}
)
public interface TelegramClient {
  @GetMapping("/sendMessage")
  public String sendMessage(@SpringQueryMap TelegramSendMessageReqI reqI);
}
