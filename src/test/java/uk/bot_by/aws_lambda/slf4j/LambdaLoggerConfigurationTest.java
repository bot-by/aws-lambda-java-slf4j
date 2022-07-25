package uk.bot_by.aws_lambda.slf4j;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Marker;
import org.slf4j.event.Level;

@Tag("fast")
class LambdaLoggerConfigurationTest {

  @DisplayName("Logger level is required")
  @Test
  void loggerLevelIsRequired() {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").requestId("request#");

    // when
    Exception exception = assertThrows(NullPointerException.class, builder::build);

    // then
    assertEquals("Logger level is null", exception.getMessage());
  }

  @DisplayName("Logger name is required")
  @Test
  void nameIsRequired() {
    // given
    var builder = LambdaLoggerConfiguration.builder().loggerLevel(Level.TRACE)
        .requestId("request#");

    // when
    Exception exception = assertThrows(NullPointerException.class, builder::build);

    // then
    assertEquals("Logger name is null", exception.getMessage());
  }

  @DisplayName("Logger name is required")
  @Test
  void requestIdIsRequired() {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE);

    // when
    Exception exception = assertThrows(NullPointerException.class, builder::build);

    // then
    assertEquals("AWS request ID is null", exception.getMessage());
  }

  @DisplayName("Date and time format")
  @ParameterizedTest(name = "[{index}] Date format: {arguments}")
  @CsvSource(value = {"null", "MM/dd/yy HH:mm"}, nullValues = "null")
  void dateTimeFormat(String dateTimeFormat) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");
    if (nonNull(dateTimeFormat)) {
      builder.dateTimeFormat(new SimpleDateFormat(dateTimeFormat));
    }

    // when
    var configuration = builder.build();

    // then
    if (nonNull(dateTimeFormat)) {
      assertNotNull(configuration.dateTimeFormat());
    } else {
      assertNull(configuration.dateTimeFormat());
    }
  }

  @DisplayName("Level in brackets")
  @ParameterizedTest(name = "[{index}] Level in brackets: {arguments}")
  @ValueSource(booleans = {true, false})
  void levelInBrackets(boolean levelInBrackets) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.levelInBrackets(levelInBrackets);

    // when
    var configuration = builder.build();

    // then
    assertEquals(levelInBrackets, configuration.levelInBrackets());
  }

  @DisplayName("Logger level")
  @ParameterizedTest(name = "[{index}] Logger level: {arguments}")
  @CsvSource(value = {"TRACE,N/A", "DEBUG,TRACE", "INFO,DEBUG", "WARN,INFO",
      "ERROR,WARN"}, nullValues = "N/A")
  void loggerLevel(Level enabledLevel, Level disabledLevel) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(enabledLevel)
        .requestId("request#");

    // when
    var configuration = builder.build();

    // then
    assertTrue(configuration.isLevelEnabled(enabledLevel));
    if (nonNull(disabledLevel)) {
      assertFalse(configuration.isLevelEnabled(disabledLevel));
    }
  }

  @DisplayName("Logger level with marker")
  @ParameterizedTest(name = "[{index}] Logger level: {arguments}")
  @CsvSource(value = {"TRACE,N/A", "DEBUG,TRACE", "INFO,DEBUG", "WARN,INFO",
      "ERROR,WARN"}, nullValues = "N/A")
  void loggerLevelWithMarker(Level enabledLevel, Level disabledLevel) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(enabledLevel)
        .requestId("request#");
    var marker = (Marker) null;

    // when
    var configuration = builder.build();

    // then
    assertTrue(configuration.isLevelEnabled(enabledLevel, marker));
    if (nonNull(disabledLevel)) {
      assertFalse(configuration.isLevelEnabled(disabledLevel, marker));
    }
  }

  @DisplayName("Name")
  @Test
  void name() {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");

    // when
    var configuration = builder.build();

    // then
    assertEquals("test", configuration.name());
  }

  @DisplayName("AWS request ID")
  @Test
  void requestId() {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE);
    builder.requestId("request-id");

    // when
    var configuration = builder.build();

    // then
    assertEquals("request-id", configuration.requestId());
  }

  @DisplayName("Show a short log name")
  @ParameterizedTest(name = "[{index}] Short log name: {arguments}")
  @ValueSource(booleans = {true, false})
  void showShortLogName(boolean showShortLogName) {
    // given
    var builder = LambdaLoggerConfiguration.builder().loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.name("abc.xyz.TestLog").showShortLogName(showShortLogName);

    // when
    var configuration = builder.build();

    // then
    if (showShortLogName) {
      assertEquals("TestLog", configuration.logName());
    } else {
      assertNull(configuration.logName());
    }
  }

  @DisplayName("Show a log name")
  @ParameterizedTest(name = "[{index}] Log name: {arguments}")
  @ValueSource(booleans = {true, false})
  void showLogName(boolean showLogName) {
    // given
    var builder = LambdaLoggerConfiguration.builder().loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.name("abc.xyz.TestLog").showLogName(showLogName);

    // when
    var configuration = builder.build();

    // then
    if (showLogName) {
      assertEquals("abc.xyz.TestLog", configuration.logName());
    } else {
      assertNull(configuration.logName());
    }
  }

  @DisplayName("Show a short log name instead of full one")
  @Test
  void showShortLogNameInsteadOfFullOne() {
    // given
    var builder = LambdaLoggerConfiguration.builder().loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.name("abc.xyz.TestLog").showShortLogName(true).showLogName(true);

    // when
    var configuration = builder.build();

    // then
    assertEquals("TestLog", configuration.logName());
  }

  @DisplayName("Show date and time")
  @ParameterizedTest(name = "[{index}] Date and time: {arguments}")
  @ValueSource(booleans = {true, false})
  void showDateTime(boolean showDateTime) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.showDateTime(showDateTime);

    // when
    var configuration = builder.build();

    // then
    assertEquals(showDateTime, configuration.showDateTime());
  }

  @DisplayName("Show a thread id")
  @ParameterizedTest(name = "[{index}] Thread Id: {arguments}")
  @ValueSource(booleans = {true, false})
  void showThreadId(boolean showThreadId) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.showThreadId(showThreadId);

    // when
    var configuration = builder.build();

    // then
    assertEquals(showThreadId, configuration.showThreadId());
  }

  @DisplayName("Show a thread name")
  @ParameterizedTest(name = "[{index}] Thread name: {arguments}")
  @ValueSource(booleans = {true, false})
  void showThreadName(boolean showThreadName) {
    // given
    var builder = LambdaLoggerConfiguration.builder().name("test").loggerLevel(Level.TRACE)
        .requestId("request#");
    builder.showThreadName(showThreadName);

    // when
    var configuration = builder.build();

    // then
    assertEquals(showThreadName, configuration.showThreadName());
  }

}
