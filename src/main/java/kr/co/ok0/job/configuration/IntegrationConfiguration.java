package kr.co.ok0.job.configuration;

import kr.co.ok0.Log;
import kr.co.ok0.client.naver.NaverClient;
import kr.co.ok0.client.naver.dto.NaverNewsReqI;
import kr.co.ok0.client.telegram.TelegramClient;
import kr.co.ok0.client.telegram.dto.TelegramSendMessageReqI;
import kr.co.ok0.job.adapter.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.Message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableIntegration
@EnableIntegrationMBeanExport
@EnableConfigurationProperties(PollingProperties.class)
public class IntegrationConfiguration implements Log {
  private final IntegrationMBeanExporter integrationMBeanExporter;
  private final ApplicationContext applicationContext;
  private final PollingProperties pollingProperties;
  private final NaverClient naverClient;
  private final TelegramClient telegramClient;

  public IntegrationConfiguration(
      IntegrationMBeanExporter integrationMBeanExporter,
      ApplicationContext applicationContext,
      PollingProperties pollingProperties,
      NaverClient naverClient, TelegramClient telegramClient
  ) {
    this.integrationMBeanExporter = integrationMBeanExporter;
    this.applicationContext = applicationContext;
    this.pollingProperties = pollingProperties;
    this.naverClient = naverClient;
    this.telegramClient = telegramClient;
  }

  @Bean
  public PollingChannelAdapter getPollingChannelAdapter() {
    return new PollingChannelAdapter(getPollingTrigger()) {
      @Override
      public List<String> getPayload(Set<String> keywords) {
        // create query
        String queryKeyword = "\"" + String.join("\",\"", keywords) + "\"";
        NaverNewsReqI naverNewsReqI = new NaverNewsReqI("news", queryKeyword, "so:r,p:1d");

        // call
        String html = naverClient.getNews(naverNewsReqI);

        // parse html
        Document document = Jsoup.parse(html);
        Elements title = document.body().getElementsByClass("news_tit");

        // return
        return title.stream()
            //.filter(element -> element.attr("title").contains("삼성") && element.attr("title").contains("파산"))
            .map(element -> element.attr("href") + "\n" + element.attr("title"))
            .collect(Collectors.toList());
      }
    };
  }

  @Bean
  public PollingTrigger getPollingTrigger() {
    return new PollingTrigger(
        pollingProperties.getPeriod(),
        pollingProperties.getPeriodUnit(),
        this.integrationMBeanExporter,
        this.applicationContext
    );
  }

  @Bean
  public StandardIntegrationFlow pollingFlow() {
    return IntegrationFlows
        .from(getPollingChannelAdapter(), o -> {
          PollerMetadata pollerMetadata = new PollerMetadata();
          pollerMetadata.setTrigger(getPollingTrigger());
          o.poller(pollerMetadata);
        })
        .handle(new Object() {
          public void invoke(List<String> messages) {
            String message = String.join("\n\n", messages);
            telegramClient.sendMessage(new TelegramSendMessageReqI(message));
          }
        })
        .get();
  }
}
