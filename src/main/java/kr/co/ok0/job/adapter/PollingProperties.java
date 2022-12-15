package kr.co.ok0.job.adapter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "polling.trigger")
public class PollingProperties {
  private Long period = 2L;
  private TimeUnit periodUnit = TimeUnit.SECONDS;

  public void setPeriod(Long period) {
    this.period = period;
  }

  public void setPeriodUnit(TimeUnit periodUnit) {
    this.periodUnit = periodUnit;
  }

  public Long getPeriod() {
    return period;
  }

  public TimeUnit getPeriodUnit() {
    return periodUnit;
  }
}
