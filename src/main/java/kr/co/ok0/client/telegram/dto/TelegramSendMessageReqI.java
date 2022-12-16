package kr.co.ok0.client.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelegramSendMessageReqI {
  @JsonProperty("text-text")
  private String text;

  public TelegramSendMessageReqI(String message) {
    this.text = message;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}