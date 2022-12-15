package kr.co.ok0.job.adapter;

import kr.co.ok0.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;

public abstract class PollingChannelAdapter extends IntegrationObjectSupport implements Log, MessageSource<String> {
  @Value("${spring.application.name}")
  private static final String name = "";
  private final PollingTrigger pollingTrigger;
  private Integer count = 0;

  public PollingChannelAdapter(PollingTrigger pollingTrigger) {
    this.pollingTrigger = pollingTrigger;
  }

  @Override
  public String getComponentName() {
    return name + "inbound-channel-adapter";
  }

  @Override
  public Message<String> receive() {
    String payload = this.getPayload();
    Log.logger.info("[receive] " + payload + "(" + count++ + ", " + this.pollingTrigger.isCompleted + ")");
    this.pollingTrigger.isCompleted = true;
    return this.getMessageBuilderFactory().withPayload(payload).build();
  }

  public abstract String getPayload();
}
