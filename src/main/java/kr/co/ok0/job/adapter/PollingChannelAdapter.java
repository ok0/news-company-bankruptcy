package kr.co.ok0.job.adapter;

import kr.co.ok0.Log;
import kr.co.ok0.job.adapter.dto.SearchKeywordsDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public abstract class PollingChannelAdapter extends IntegrationObjectSupport implements Log, MessageSource<List<String>> {
  @Value("${spring.application.name}")
  private static String name;
  private final PollingTrigger pollingTrigger;

  public ClassPathResource classResource = null;
  private List<SearchKeywordsDto> resource;
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
      SearchKeywordsDto nextKeywords = getNextKeyword();
      if (nextKeywords.keywords.isEmpty()) {
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

  public abstract List<String> getPayload(SearchKeywordsDto keywords);
  public abstract ClassPathResource getClassResource();

  public SearchKeywordsDto getNextKeyword() throws IOException {
    if (resource == null) {
      resourceLine = 0;

      Path path = Paths.get(getClassResource().getURI());
      Jackson2JsonObjectMapper jackson2JsonObjectMapper = new Jackson2JsonObjectMapper();
      resource = Arrays.stream(jackson2JsonObjectMapper.fromJson(Files.readString(path), SearchKeywordsDto[].class)).toList();
    }

    if (resourceLine > resource.size() -1) {
      resource = null;
      return new SearchKeywordsDto();
    } else {
      return resource.get(resourceLine++);
    }
  }
}
