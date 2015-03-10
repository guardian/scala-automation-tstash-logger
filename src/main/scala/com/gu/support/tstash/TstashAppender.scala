package com.gu.support.tstash

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Paths, Files}
import java.util.concurrent.TimeUnit

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.ning.http.client.websocket.WebSocket
import play.api.libs.json.Json

import scala.collection.mutable

object TstashAppender {
  val sockets = mutable.Map[String, WebSocket]()
}

class TstashAppender extends UnsynchronizedAppenderBase[ILoggingEvent] {

  override def append(eventObject: ILoggingEvent): Unit = {
    val failed = """(?s)\[FAILED\](.*)""".r
    val urlExtractor = """(?s)\[StartInfo\](.*) (.*)""".r

    eventObject.getMessage match {
      case urlExtractor(url, testId) => sendHTMLFile(url, testId)
      case failed(m) => sendError(eventObject, m)
      case "[SCREENSHOT]" => sendScreenShot(eventObject)
      case _ => sendMessage(eventObject)
    }
  }

  private def sendMessage(eventObject: ILoggingEvent): Unit = {
    val json = Json.obj(
      "testName" -> eventObject.getMDCPropertyMap.get("testName"),
      "testDate" -> eventObject.getMDCPropertyMap.get("testDate"),
      "setName" -> eventObject.getMDCPropertyMap.get("setName"),
      "setDate" -> eventObject.getMDCPropertyMap.get("setDate"),
      "message" -> eventObject.getFormattedMessage
    ).toString()
    sendJson(eventObject, json)
  }

  private def sendError(eventObject: ILoggingEvent, error: String): Unit = {
    val json = Json.obj(
      "testName" -> eventObject.getMDCPropertyMap.get("testName"),
      "testDate" -> eventObject.getMDCPropertyMap.get("testDate"),
      "setName" -> eventObject.getMDCPropertyMap.get("setName"),
      "setDate" -> eventObject.getMDCPropertyMap.get("setDate"),
      "error" -> error
    ).toString()
    sendJson(eventObject, json)
  }

  private def sendJson(eventObject: ILoggingEvent, body: String): Unit = {
    val request = HttpClient.httpClient.preparePost(HttpClient.urlReport)
      .addHeader("Content-Type", "application/json")
      .setBody(body)
      .build()
    val result = HttpClient.httpClient.executeRequest(request).get(15, TimeUnit.SECONDS)
    if (result.getStatusCode != 200) {
      println(s"[TSTASH-Logger] Could not report test message for test: ${eventObject.getMDCPropertyMap.get("testName")}")
//      println(result.getStatusText); println(result.getResponseBody)
    }
  }

  def sendHTMLFile(tstashURL: String, testId: String): Unit = {
    val tstashReportHtml = s"<html><head><meta <meta http-equiv='refresh' content='0; url=$tstashURL' /></head><body><a href='$tstashURL'>Test report</a></body></html>"
    if (testId.startsWith("Some")) {
      val testIdCut = testId.drop(5).dropRight(1)
      Files.write(Paths.get(s"TstashReport-$testIdCut.html"), tstashReportHtml.getBytes(StandardCharsets.UTF_8))
    }
    else {
      Files.write(Paths.get(s"TstashReport.html"), tstashReportHtml.getBytes(StandardCharsets.UTF_8))
    }
  }

  private def sendScreenShot(eventObject: ILoggingEvent): Unit = {
    val request = HttpClient.httpClient.preparePost(HttpClient.urlSS)
      .addQueryParameter("testName", eventObject.getMDCPropertyMap.get("testName"))
      .addQueryParameter("testDate", eventObject.getMDCPropertyMap.get("testDate"))
      .addQueryParameter("setName", eventObject.getMDCPropertyMap.get("setName"))
      .addQueryParameter("setDate", eventObject.getMDCPropertyMap.get("setDate"))
      .setBody(getScreenShot(eventObject))
      .build()
    val result = HttpClient.httpClient.executeRequest(request).get(15, TimeUnit.SECONDS)
    if (result.getStatusCode != 200) {
      println(s"[TSTASH-Logger] Could not report test screen shot for test: ${eventObject.getMDCPropertyMap.get("testName")}")
//      println(result.getStatusText); println(result.getResponseBody)
    }
  }

  private def getScreenShot(eventObject: ILoggingEvent) = {
    if (eventObject.getArgumentArray()(0).isInstanceOf[File]) {
      eventObject.getArgumentArray()(0).asInstanceOf[File]
    } else {
      // scala logging framework implementation specific
      eventObject.getArgumentArray()(0).asInstanceOf[scala.collection.mutable.WrappedArray[_]].head.asInstanceOf[File]
    }
  }

}
