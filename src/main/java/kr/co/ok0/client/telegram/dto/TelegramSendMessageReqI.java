package kr.co.ok0.client.telegram.dto;

// TODO: 2022/12/16 JsonProperty
public class TelegramSendMessageReqI {
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