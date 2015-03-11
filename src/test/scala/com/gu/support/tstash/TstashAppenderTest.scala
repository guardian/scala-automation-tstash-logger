package com.gu.automation.api

import java.util
import java.util.UUID

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.{LoggerContextVO, IThrowableProxy, ILoggingEvent}
import com.gu.support.tstash.TstashAppender
import com.typesafe.scalalogging.slf4j.LazyLogging
import org.joda.time.DateTime
import org.openqa.selenium.OutputType
import org.openqa.selenium.firefox.FirefoxDriver
import org.scalatest._
import org.slf4j.{Marker, MDC}

import scala.None

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
    logger.info("""[FAILED]no such element
                  |  (Session info: chrome=38.0.2125.111)
                  |  (Driver info: chromedriver=2.11.298611 (d1120fdf51badec2f7b63a96e19a58d4783de84d),platform=Mac OS X 10.10.0 x86_64) (WARNING: The server did not provide any stacktrace information)
                  |Command duration or timeout: 25.06 seconds
                  |For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html
                  |Build info: version: '2.43.1', revision: '5163bceef1bc36d43f3dc0b83c88998168a363a0', time: '2014-09-10 09:43:55'
                  |System info: host: 'tizona', ip: '10.233.72.160', os.name: 'Mac OS X', os.arch: 'x86_64', os.version: '10.10', java.version: '1.8.0_20'
                  |Driver info: org.openqa.selenium.chrome.ChromeDriver
                  |Capabilities [{applicationCacheEnabled=false, rotatable=false, mobileEmulationEnabled=false, chrome={userDataDir=/var/folders/mq/t6c1chcd0dv7f79bv9p4vgr00000gn/T/.org.chromium.Chromium.Kok2dO}, takesHeapSnapshot=true, databaseEnabled=false, handlesAlerts=true, version=38.0.2125.111, platform=MAC, browserConnectionEnabled=false, nativeEvents=true, acceptSslCerts=true, locationContextEnabled=true, webStorageEnabled=true, browserName=chrome, takesScreenshot=true, javascriptEnabled=true, cssSelectorsEnabled=true}]
                  |Session ID: 4c3cdd37f8c926d92254d33bbdb03563""")
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
//    logger.info("[SCREENSHOT]", driver.getScreenshotAs(OutputType.FILE))
//    driver.quit()
//
//  }

  "TestInfo with testID" should "produce correct filename" in {
    val eventObject = new eventObject("[StartInfo] www.test.com testId")
    val appender = new TstashAppender
    val message = appender.prepareMessageReaction(eventObject)
    message shouldNot be (None)
    message.get._2 shouldNot be ("")
    message.get._1 shouldBe ("TstashReport-testId.html")
  }

  "TestInfo with no testId" should "produce correct filename" in {
    val eventObject = new eventObject("[StartInfo] www.test.com ")
    val appender = new TstashAppender
    val message = appender.prepareMessageReaction(eventObject)
    message shouldNot be (None)
    message.get._2 shouldNot be ("")
    message.get._1 shouldBe ("TstashReport.html")
  }

}

object SetTime {
  val time = DateTime.now
}

case class eventObject(message: String) extends ILoggingEvent {

  override def getThreadName: String = ???

  override def getLoggerName: String = ???

  override def getFormattedMessage: String = ???

  override def getMessage: String = message

  override def getLoggerContextVO: LoggerContextVO = ???

  override def getLevel: Level = ???

  override def getTimeStamp: Long = ???

  override def getCallerData: Array[StackTraceElement] = ???

  override def hasCallerData: Boolean = ???

  override def getMDCPropertyMap: util.Map[String, String] = ???

  override def getMdc: util.Map[String, String] = ???

  override def getArgumentArray: Array[AnyRef] = ???

  override def getMarker: Marker = ???

  override def getThrowableProxy: IThrowableProxy = ???

  override def prepareForDeferredProcessing(): Unit = ???

}