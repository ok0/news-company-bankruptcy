package kr.co.ok0.client.telegram.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelegramSendMessageReqI {
  @JsonProperty("chat_id")
  private String chatId;

  @JsonProperty("text")
  private String text;

  public String getChatId() {
    return chatId;
  }

  public String getText() {
    return text;
  }

  public void setChatId(String chatId) {
    this.chatId = chatId;
  }

  public void setText(String text) {
    this.text = text;
  }
}