package kr.co.ok0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Log {
  default Logger logger(Log $this) {
    return LoggerFactory.getLogger($this.getClass());
  }
}
