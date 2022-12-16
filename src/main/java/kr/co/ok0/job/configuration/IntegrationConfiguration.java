package kr.co.ok0.job.configuration;

import kr.co.ok0.Log;
import kr.co.ok0.client.naver.NaverClient;
import kr.co.ok0.client.naver.dto.NaverNewsReqI;
import kr.co.ok0.client.telegram.TelegramClient;
import kr.co.ok0.client.telegram.dto.TelegramSendMessageReqI;
import kr.co.ok0.job.adapter.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.*;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.integration.scheduling.PollerMetadata;

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
      public String getPayload() {
        return naverClient.getNews(new NaverNewsReqI("news", "\"삼성\"+\"파산\""));
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
        .handle( message -> {
          telegramClient.sendMessage(
              new TelegramSendMessageReqI((String) message.getPayload())
          );
        }).get();
  }
}
