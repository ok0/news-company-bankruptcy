package kr.co.ok0.job.configuration;

import kr.co.ok0.Log;
import kr.co.ok0.client.naver.NaverClient;
import kr.co.ok0.client.naver.dto.NaverNewsConstants;
import kr.co.ok0.client.naver.dto.NaverNewsReqI;
import kr.co.ok0.client.telegram.TelegramClient;
import kr.co.ok0.client.telegram.dto.TelegramSendMessageReqI;
import kr.co.ok0.job.adapter.*;
import kr.co.ok0.job.adapter.dto.SearchKeywordsDto;
import org.jsoup.Jsoup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.integration.scheduling.PollerMetadata;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableIntegration
@EnableIntegrationMBeanExport
@EnableConfigurationProperties(PollingProperties.class)
public class BankruptcyIntegrationConfiguration implements Log {
  private final IntegrationMBeanExporter integrationMBeanExporter;
  private final ApplicationContext applicationContext;
  private final PollingProperties pollingProperties;
  private final NaverClient naverClient;
  private final TelegramClient telegramClient;

  public BankruptcyIntegrationConfiguration(
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
      public ClassPathResource getClassResource() {
        return new ClassPathResource("search-keywords/bankruptcy.json");
      }

      @Override
      public SearchKeywordsDto getNextKeyword() throws IOException {
        SearchKeywordsDto searchKeywordsDto = super.getNextKeyword();
        if (resourceLine >= 500) {
          searchKeywordsDto.isMatchTitle = false;
        }

        return searchKeywordsDto;
      }

      @Override
      public List<String> getPayload(SearchKeywordsDto searchKeywordsDto) {
        String query = "\"" + String.join("\" \"", searchKeywordsDto.keywords) + "\"";
        NaverNewsReqI naverNewsReqI = new NaverNewsReqI(
            NaverNewsConstants.REQUEST_PARAMETER_WHERE,
            NaverNewsConstants.REQUEST_PARAMETER_NSO,
            query
        );

        List<String> payload = Jsoup.parse(naverClient.getNews(naverNewsReqI))
            .body().getElementsByClass("news_tit")
            .stream()
            .filter(element -> {
              String titleLower = element.attr("title").toLowerCase();
              if (searchKeywordsDto.isMatchTitle) {
                for (String keyword : searchKeywordsDto.keywords) {
                  String keywordLower = keyword.toLowerCase();
                  if (!titleLower.contains(keywordLower))
                    return false;
                }
              }

              return true;
            })
            .map(element -> element.attr("href") + "\n" + element.attr("title"))
            .collect(Collectors.toList());

        if (!payload.isEmpty()) {
          payload.add(0, query);
        }

        return payload;
      }
    };
  }

  @Bean
  public PollingTrigger getPollingTrigger() {
    return new PollingTrigger(
        pollingProperties.getPeriod(),
        pollingProperties.getPeriodUnit(),
        integrationMBeanExporter,
        applicationContext
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
            if (!messages.isEmpty()) {
              String message = String.join("\n\n", messages);
              telegramClient.sendMessage(new TelegramSendMessageReqI(message));
            }
          }
        })
        .get();
  }
}
