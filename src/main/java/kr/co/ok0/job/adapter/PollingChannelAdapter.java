package kr.co.ok0.job.adapter;

import kr.co.ok0.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class PollingChannelAdapter extends IntegrationObjectSupport implements Log, MessageSource<List<String>> {
  @Value("${spring.application.name}")
  private static String name;
  private final PollingTrigger pollingTrigger;

  public PollingChannelAdapter(PollingTrigger pollingTrigger) {
    this.pollingTrigger = pollingTrigger;
  }

  @Override
  public String getComponentName() {
    return name + "inbound-channel-adapter";
  }

  @Override
  public Message<List<String>> receive() {
    HashSet<String> nextKeywords = getNextKeyword();

    if (nextKeywords.isEmpty()) {
      this.pollingTrigger.isCompleted = true;
    }
    this.pollingTrigger.isCompleted = true;
    List<String> payload = this.getPayload(getNextKeyword());
    Log.logger.info("[receive] " + payload + "(" + this.pollingTrigger.isCompleted + ")");

    return this.getMessageBuilderFactory().withPayload(payload).build();
  }

  public abstract List<String> getPayload(Set<String> keywords);
  public HashSet<String> getNextKeyword() {
    HashSet<String> keywords = new HashSet<>();
    keywords.add("삼성");
    keywords.add("파산");
    return keywords;
  };
}
