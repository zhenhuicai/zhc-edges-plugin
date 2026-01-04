package com.zhm.edges.plugins.api.job;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.FileAppender;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MDC;

public class DefaultJobLogger implements JobLogger {

  static final org.slf4j.Logger logger = LoggerFactory.getLogger(DefaultJobLogger.class);

  // The underlying Logback logger context.
  private static final LoggerContext loggerContext =
      (LoggerContext) LoggerFactory.getILoggerFactory();
  protected final String jobId;
  protected final String sessionId;
  private final Logger newLogger;
  private final Path logFilePath;

  public DefaultJobLogger(String jobId, final String sessionId, final Path loggerPath) {
    this(jobId, sessionId, loggerPath, null);
  }

  public DefaultJobLogger(
      String jobId,
      final String sessionId,
      final Path loggerPath,
      final AppenderBase externalAppender) {
    this.jobId = jobId;
    this.sessionId = sessionId;

    logFilePath = Paths.get(loggerPath.toString(), sessionId + ".log");
    checkAndArchive(logFilePath, loggerPath);

    // 1. Create a new FileAppender programmatically.
    FileAppender fileAppender = new FileAppender<>();
    fileAppender.setContext(loggerContext);
    fileAppender.setName("FILE");

    fileAppender.setFile(logFilePath.toAbsolutePath().toString());

    // 2. Configure the layout/encoder for the appender.
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(loggerContext);
    encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [vendor=%X{vendor:-N/A}]: %msg%n");
    encoder.start();

    fileAppender.setEncoder(encoder);
    fileAppender.start(); // This is crucial! The appender must be started.

    if (externalAppender != null) {
      externalAppender.setContext(loggerContext);
      externalAppender.start();
    }

    newLogger = loggerContext.getLogger("job");
    newLogger.addAppender(fileAppender);
    if (externalAppender != null) {
      newLogger.addAppender(externalAppender);
    }
    newLogger.setLevel(Level.DEBUG); // Set the desired level for this specific logger.

    // 4. Set additivity to false. THIS IS CRITICAL.
    // It prevents the logs from propagating up to the root logger,
    // which would cause them to be duplicated in the console or a general log file.
    newLogger.setAdditive(false);
  }

  private void checkAndArchive(Path logFile, final Path loggerPath) {
    if (Files.exists(logFile)) {

      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
      Path archiveFile = Paths.get(loggerPath.toString(), "job" + "-" + timestamp + ".log");
      try {
        Files.move(logFile, archiveFile, StandardCopyOption.REPLACE_EXISTING);
      } catch (IOException e) {
        logger.warn("fail archive the log of job", e);
      }
    }
  }

  public Path getLogFilePath() {
    return logFilePath;
  }

  /**
   * Log a message at the INFO level according to the specified format and arguments. This form
   * avoids superfluous string concatenation when the logger is disabled for the INFO level.
   *
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void info(String format, Object... arguments) {
    String vendor = MDC.get("vendor");
    if (vendor != null) {
      format = "[" + vendor + "] " + format;
    }
    newLogger.info(format, arguments);
  }

  /**
   * Log an exception (throwable) at the INFO level with an accompanying message.
   *
   * @param message the message accompanying the exception
   * @param exception the exception (throwable) to log
   */
  public void info(String message, Throwable exception) {
    newLogger.info(message, exception);
  }

  /**
   * Log a message with the specific Marker at the INFO level.
   *
   * @param marker the marker specific to this log statement
   * @param msg the message string to be logged
   */
  public void info(Marker marker, String msg) {
    newLogger.info(marker, msg);
  }

  /**
   * Log a message at the INFO level according to the specified format and arguments. This form
   * avoids superfluous object creation when the logger is disabled for the INFO level.
   *
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void info(String format, Object arg1, Object arg2) {
    newLogger.info(format, arg1, arg2);
  }

  /**
   * Log a message at the INFO level according to the specified format and argument. This form
   * avoids superfluous object creation when the logger is disabled for the INFO level.
   *
   * @param format the format string
   * @param arg the argument
   */
  public void info(String format, Object arg) {
    newLogger.info(format, arg);
  }

  /**
   * Log a message at the INFO level.
   *
   * @param msg the message string to be logged
   */
  public void info(String msg) {
    newLogger.info(msg);
  }

  /**
   * Log an exception (throwable) at the INFO level with an accompanying message and marker.
   *
   * @param marker the marker data for this log statement
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */
  public void info(Marker marker, String msg, Throwable t) {
    newLogger.info(marker, msg, t);
  }

  /**
   * Log a message with the specific Marker at the INFO level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void info(Marker marker, String format, Object... arguments) {
    newLogger.info(marker, format, arguments);
  }

  /**
   * Log a message with the specific Marker at the INFO level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    newLogger.info(marker, format, arg1, arg2);
  }

  /**
   * Log a message with the specific Marker at the INFO level according to the specified format and
   * argument.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg the argument
   */
  public void info(Marker marker, String format, Object arg) {
    newLogger.info(marker, format, arg);
  }

  @Override
  public void info(LoggerType loggerType) {
    info(loggerType, StringUtils.EMPTY);
  }

  @Override
  public void info(LoggerType loggerType, Object... arguments) {
    info(loggerType.cnMessage, arguments);
  }

  /**
   * Log a message at the DEBUG level according to the specified format and arguments. This form
   * avoids superfluous string concatenation when the logger is disabled for the DEBUG level.
   *
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void debug(String format, Object... arguments) {
    String vendor = MDC.get("vendor");
    if (vendor != null) {
      format = "[" + vendor + "] " + format;
    }
    newLogger.debug(format, arguments);
  }

  /**
   * Log an exception (throwable) at the DEBUG level with an accompanying message.
   *
   * @param message the message accompanying the exception
   * @param exception the exception (throwable) to log
   */
  public void debug(String message, Throwable exception) {
    newLogger.debug(message, exception);
  }

  /**
   * Log a message with the specific Marker at the DEBUG level.
   *
   * @param marker the marker specific to this log statement
   * @param msg the message string to be logged
   */
  public void debug(Marker marker, String msg) {
    newLogger.debug(marker, msg);
  }

  /**
   * Log a message at the DEBUG level according to the specified format and arguments. This form
   * avoids superfluous object creation when the logger is disabled for the DEBUG level.
   *
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void debug(String format, Object arg1, Object arg2) {
    newLogger.debug(format, arg1, arg2);
  }

  /**
   * Log a message at the DEBUG level according to the specified format and argument. This form
   * avoids superfluous object creation when the logger is disabled for the DEBUG level.
   *
   * @param format the format string
   * @param arg the argument
   */
  public void debug(String format, Object arg) {
    newLogger.debug(format, arg);
  }

  /**
   * Log a message at the DEBUG level.
   *
   * @param msg the message string to be logged
   */
  public void debug(String msg) {
    newLogger.debug(msg);
  }

  /**
   * Log an exception (throwable) at the DEBUG level with an accompanying message and marker.
   *
   * @param marker the marker data for this log statement
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */
  public void debug(Marker marker, String msg, Throwable t) {
    newLogger.debug(marker, msg, t);
  }

  /**
   * Log a message with the specific Marker at the DEBUG level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void debug(Marker marker, String format, Object... arguments) {
    newLogger.debug(marker, format, arguments);
  }

  /**
   * Log a message with the specific Marker at the DEBUG level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    newLogger.debug(marker, format, arg1, arg2);
  }

  /**
   * Log a message with the specific Marker at the DEBUG level according to the specified format and
   * argument.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg the argument
   */
  public void debug(Marker marker, String format, Object arg) {
    newLogger.debug(marker, format, arg);
  }

  @Override
  public void debug(LoggerType loggerType) {
    debug(loggerType, StringUtils.EMPTY);
  }

  @Override
  public void debug(LoggerType loggerType, Object... args) {
    debug(loggerType.cnMessage, args);
  }

  /**
   * Log a message at the ERROR level according to the specified format and arguments. This form
   * avoids superfluous string concatenation when the logger is disabled for the ERROR level.
   *
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void error(String format, Object... arguments) {
    String vendor = MDC.get("vendor");
    if (vendor != null) {
      format = "[" + vendor + "] " + format;
    }
    newLogger.error(format, arguments);
  }

  /**
   * Log an exception (throwable) at the ERROR level with an accompanying message.
   *
   * @param message the message accompanying the exception
   * @param exception the exception (throwable) to log
   */
  public void error(String message, Throwable exception) {
    newLogger.error(message, exception);
  }

  /**
   * Log a message with the specific Marker at the ERROR level.
   *
   * @param marker the marker specific to this log statement
   * @param msg the message string to be logged
   */
  public void error(Marker marker, String msg) {
    newLogger.error(marker, msg);
  }

  /**
   * Log a message at the ERROR level according to the specified format and arguments. This form
   * avoids superfluous object creation when the logger is disabled for the ERROR level.
   *
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void error(String format, Object arg1, Object arg2) {
    newLogger.error(format, arg1, arg2);
  }

  /**
   * Log a message at the ERROR level according to the specified format and argument. This form
   * avoids superfluous object creation when the logger is disabled for the ERROR level.
   *
   * @param format the format string
   * @param arg the argument
   */
  public void error(String format, Object arg) {
    newLogger.error(format, arg);
  }

  /**
   * Log a message at the ERROR level.
   *
   * @param msg the message string to be logged
   */
  public void error(String msg) {
    newLogger.error(msg);
  }

  /**
   * Log an exception (throwable) at the ERROR level with an accompanying message and marker.
   *
   * @param marker the marker data for this log statement
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */
  public void error(Marker marker, String msg, Throwable t) {
    newLogger.error(marker, msg, t);
  }

  /**
   * Log a message with the specific Marker at the ERROR level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void error(Marker marker, String format, Object... arguments) {
    newLogger.error(marker, format, arguments);
  }

  /**
   * Log a message with the specific Marker at the ERROR level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    newLogger.error(marker, format, arg1, arg2);
  }

  /**
   * Log a message with the specific Marker at the ERROR level according to the specified format and
   * argument.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg the argument
   */
  public void error(Marker marker, String format, Object arg) {
    newLogger.error(marker, format, arg);
  }

  @Override
  public void error(LoggerType loggerType) {
    error(loggerType, StringUtils.EMPTY);
  }

  @Override
  public void error(LoggerType loggerType, Object... arguments) {
    error(loggerType.cnMessage, arguments);
  }

  /**
   * Log a message at the WARN level according to the specified format and arguments. This form
   * avoids superfluous string concatenation when the logger is disabled for the WARN level.
   *
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void warn(String format, Object... arguments) {
    String vendor = MDC.get("vendor");
    if (vendor != null) {
      format = "[" + vendor + "] " + format;
    }
    newLogger.warn(format, arguments);
  }

  /**
   * Log an exception (throwable) at the WARN level with an accompanying message.
   *
   * @param message the message accompanying the exception
   * @param exception the exception (throwable) to log
   */
  public void warn(String message, Throwable exception) {
    newLogger.warn(message, exception);
  }

  /**
   * Log a message with the specific Marker at the WARN level.
   *
   * @param marker the marker specific to this log statement
   * @param msg the message string to be logged
   */
  public void warn(Marker marker, String msg) {
    newLogger.warn(marker, msg);
  }

  /**
   * Log a message at the WARN level according to the specified format and arguments. This form
   * avoids superfluous object creation when the logger is disabled for the WARN level.
   *
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void warn(String format, Object arg1, Object arg2) {
    newLogger.warn(format, arg1, arg2);
  }

  /**
   * Log a message at the WARN level according to the specified format and argument. This form
   * avoids superfluous object creation when the logger is disabled for the WARN level.
   *
   * @param format the format string
   * @param arg the argument
   */
  public void warn(String format, Object arg) {
    newLogger.warn(format, arg);
  }

  /**
   * Log a message at the WARN level.
   *
   * @param msg the message string to be logged
   */
  public void warn(String msg) {
    newLogger.warn(msg);
  }

  /**
   * Log an exception (throwable) at the WARN level with an accompanying message and marker.
   *
   * @param marker the marker data for this log statement
   * @param msg the message accompanying the exception
   * @param t the exception (throwable) to log
   */
  public void warn(Marker marker, String msg, Throwable t) {
    newLogger.warn(marker, msg, t);
  }

  /**
   * Log a message with the specific Marker at the WARN level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arguments a list of 3 or more arguments
   */
  public void warn(Marker marker, String format, Object... arguments) {
    newLogger.warn(marker, format, arguments);
  }

  /**
   * Log a message with the specific Marker at the WARN level according to the specified format and
   * arguments.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg1 the first argument
   * @param arg2 the second argument
   */
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    newLogger.warn(marker, format, arg1, arg2);
  }

  /**
   * Log a message with the specific Marker at the WARN level according to the specified format and
   * argument.
   *
   * @param marker the marker data specific to this log statement
   * @param format the format string
   * @param arg the argument
   */
  public void warn(Marker marker, String format, Object arg) {
    newLogger.warn(marker, format, arg);
  }

  @Override
  public void warn(LoggerType loggerType) {
    warn(loggerType, StringUtils.EMPTY);
  }

  @Override
  public void warn(LoggerType loggerType, Object... arguments) {
    warn(loggerType.cnMessage, arguments);
  }

  public void done() {
    try {
      newLogger.detachAndStopAllAppenders();
    } catch (Throwable throwable) {
      logger.warn("fail close the logger of job " + jobId);
    }
  }
}
