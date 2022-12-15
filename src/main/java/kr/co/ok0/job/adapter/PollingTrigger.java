package kr.co.ok0.job.adapter;

import kr.co.ok0.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.monitor.IntegrationMBeanExporter;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PollingTrigger extends PeriodicTrigger implements Log {
  private final IntegrationMBeanExporter integrationMBeanExporter;
  private final ApplicationContext applicationContext;

  private final static Long SHUTDOWN_SEC = 10000L;
  public Boolean isCompleted = false;

  public PollingTrigger(
      long period,
      TimeUnit timeUnit,
      IntegrationMBeanExporter integrationMBeanExporter,
      ApplicationContext applicationContext
  ) {
    super(period, timeUnit);
    this.integrationMBeanExporter = integrationMBeanExporter;
    this.applicationContext = applicationContext;
  }

  @Override
  public Date nextExecutionTime(TriggerContext triggerContext) {
    if (this.isCompleted) {
      logger.info("Completed.");

      new Thread(() -> {
        this.integrationMBeanExporter.stopActiveComponents(SHUTDOWN_SEC);
        SpringApplication.exit(this.applicationContext, () -> 0);
      }).start();

      return null;
    } else {
      logger.info("Not Completed.");
      return super.nextExecutionTime(triggerContext);
    }
  }
}
