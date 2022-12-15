package kr.co.ok0.job.configuration;

import kr.co.ok0.Log;
import kr.co.ok0.job.adapter.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.SourcePollingChannelAdapterSpec;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.jmx.config.EnableIntegrationMBeanExport;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.integration.scheduling.PollerMetadata;

import java.util.function.Consumer;

@Configuration
@EnableIntegration
@EnableIntegrationMBeanExport
@EnableConfigurationProperties(PollingProperties.class)
public class IntegrationConfiguration implements Log {
  private IntegrationMBeanExporter integrationMBeanExporter;
  private ApplicationContext applicationContext;
  private PollingProperties pollingProperties;
//  private TelegramClient telegramClient;

  public IntegrationConfiguration(IntegrationMBeanExporter integrationMBeanExporter, ApplicationContext applicationContext, PollingProperties pollingProperties
//      TelegramClient telegramClient
  ) {
    this.integrationMBeanExporter = integrationMBeanExporter;
    this.applicationContext = applicationContext;
    this.pollingProperties = pollingProperties;
//    this.telegramClient = telegramClient;
  }

  @Bean
  public PollingChannelAdapter pollingChannelAdapter() {
    return (new PollingChannelAdapter(this.pollingTrigger()) {
      @Override
      public String getPayload() {
        return "Hello, This is Payload !";
      }
    });
  }

  @Bean
  public PollingTrigger pollingTrigger() {
    return new PollingTrigger(pollingProperties.getPeriod(), pollingProperties.getPeriodUnit(), this.integrationMBeanExporter, this.applicationContext);
  }

  @Bean
  public StandardIntegrationFlow pollingFlow() {
    return IntegrationFlows
        .from(pollingChannelAdapter(), sourcePollingChannelAdapterSpec -> new PollerMetadata().setTrigger(pollingTrigger()))
        .handle(new Object() {
          public void invoke() {
            System.out.println("Hello");
          }
        }).get();
  }
}
