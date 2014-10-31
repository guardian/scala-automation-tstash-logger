package com.gu.automation.api

import java.util.UUID

import com.typesafe.scalalogging.slf4j.LazyLogging
import org.joda.time.DateTime
import org.openqa.selenium.OutputType
import org.openqa.selenium.firefox.FirefoxDriver
import org.scalatest._
import org.slf4j.MDC

class TstashAppenderTest extends FlatSpec with Matchers with LazyLogging {

  "The auth api" should "let us log in as a valid user" in {

    sys.props.put("teststash.url", "localhost:9000")
    MDC.put("ID", UUID.randomUUID().toString)
    MDC.put("testName", "test name 1")
    MDC.put("testDate", DateTime.now.getMillis.toString)
    MDC.put("setName", "set name 1")
    MDC.put("setDate", SetTime.time.getMillis.toString)
    println("setDate: " + SetTime.time.getMillis.toString)

    logger.info("Test message 1.")
    Thread.sleep(1000)
    logger.info("Test message 2.")
    logger.info("Test message 3.")
    logger.info("Test message 4.")
    logger.info("Test message 5.")
  }

  "The auth api" should "let us log in as a valid user - Failed" in {

    sys.props.put("teststash.url", "localhost:9000")
    MDC.put("ID", UUID.randomUUID().toString)
    MDC.put("testName", "test name 2")
    MDC.put("testDate", DateTime.now.getMillis.toString)
    MDC.put("setName", "set name 2")
    MDC.put("setDate", SetTime.time.getMillis.toString)
    println("setDate: " + SetTime.time.getMillis.toString)

    logger.info("Test message 1.")
    Thread.sleep(1000)
    logger.info("Test message 2.")
    logger.info("Test message 4.")
    logger.info("[FAILED]Element not found on page.")
  }

//  "The auth api " should "let us log in as a valid user - Screen Shot" in {
//
//    sys.props.put("teststash.url", "localhost:9000")
//    MDC.put("ID", UUID.randomUUID().toString)
//    MDC.put("testName", "test name 3")
//    MDC.put("testDate", DateTime.now.getMillis.toString)
//    MDC.put("setName", "set name 2")
//    MDC.put("setDate", SetTime.time.getMillis.toString)
//    println("setDate: " + SetTime.time.getMillis.toString)
//
//    logger.info("Test message 1111.")
//    Thread.sleep(1000)
//    logger.info("Test message 2222222.")
//    val driver = new FirefoxDriver()
//    driver.get("http://www.google.com")
//    logger.info("[FAILED]Element not found on page.")
//    logger.info("[SCREENSHOT]", driver.getScreenshotAs(OutputType.BYTES))
//    driver.quit()
//
//  }

}

object SetTime {
  val time = DateTime.now
}