package kr.co.ok0.job.adapter;

import kr.co.ok0.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class PollingChannelAdapter extends IntegrationObjectSupport implements Log, MessageSource<List<String>> {
  @Value("${spring.application.name}")
  private static String name;
  private final PollingTrigger pollingTrigger;

  public ClassPathResource classResource = null;
  private List<HashSet<String>> resource;
  private Integer resourceLine = 0;

  public PollingChannelAdapter(PollingTrigger pollingTrigger) {
    this.pollingTrigger = pollingTrigger;
  }

  @Override
  public String getComponentName() {
    return name + "inbound-channel-adapter";
  }

  @Override
  public Message<List<String>> receive() {
    try {
      HashSet<String> nextKeywords = getNextKeyword();
      if (nextKeywords.isEmpty()) {
        pollingTrigger.isCompleted = true;
      }

      List<String> payload = getPayload(nextKeywords);
      logger(this).info("[receive] " + payload + "(" + resourceLine + "," + pollingTrigger.isCompleted + ")");

      return getMessageBuilderFactory().withPayload(payload).build();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  public abstract List<String> getPayload(Set<String> keywords);

  public abstract void setClassResource();

  private void setResource() throws IOException {
    setClassResource();
    resourceLine = 0;

    Path path = Paths.get(classResource.getURI());
    resource = Files
        .readAllLines(path)
        .stream()
        .map(o ->
            (HashSet<String>) Stream.
                of(o.trim().split("\t"))
                .collect(Collectors.toSet())
        )
        .toList();
  }

  public HashSet<String> getNextKeyword() throws IOException {
    if (resource == null) {
      setResource();
    }

    if (resourceLine > resource.size() -1) {
      return new HashSet<>();
    } else {
      return resource.get(resourceLine++);
    }
  }
}
