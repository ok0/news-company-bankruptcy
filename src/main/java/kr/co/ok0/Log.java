package kr.co.ok0;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Log {
  Logger logger = LoggerFactory.getLogger(Log.class.getSimpleName());
}
