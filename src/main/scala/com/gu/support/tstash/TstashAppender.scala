package com.gu.automation.api

import java.util.concurrent.TimeUnit

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.gu.support.tstash.HttpClient
import com.ning.http.client.websocket.WebSocket

import scala.collection.mutable

object TstashAppender {
  val sockets = mutable.Map[String, WebSocket]()
}

class TstashAppender extends UnsynchronizedAppenderBase[ILoggingEvent] {

  override def append(eventObject: ILoggingEvent): Unit = {
    val failed = """\[FAILED\](.*)""".r

    eventObject.getMessage match {
      case failed(m) => sendError(eventObject, m)
      case "[SCREENSHOT]" => sendScreenShot(eventObject)
      case _ => sendMessage(eventObject)
    }
  }

  private def sendMessage(eventObject: ILoggingEvent): Unit = {
    sendJson(eventObject, s""""message":"${eventObject.getFormattedMessage}"""")
  }

  private def sendError(eventObject: ILoggingEvent, error: String): Unit = {
    sendJson(eventObject, s""""error":"${error}"""")
  }

  private def sendJson(eventObject: ILoggingEvent, body: String): Unit = {
    val request = HttpClient.httpClient.preparePost(HttpClient.urlReport)
      .addHeader("Content-Type", "application/json")
      .setBody(s"""{
         |"testName":"${eventObject.getMDCPropertyMap.get("testName")}",
         |"testDate":"${eventObject.getMDCPropertyMap.get("testDate")}",
         |"setName":"${eventObject.getMDCPropertyMap.get("setName")}",
         |"setDate":"${eventObject.getMDCPropertyMap.get("setDate")}",
         |${body}
         |}""".stripMargin)
      .build()
    val result = HttpClient.httpClient.executeRequest(request).get(15, TimeUnit.SECONDS)
    if (result.getStatusCode != 200) {
      println(result.getStatusText)
      println(result.getResponseBody)
    }
  }

  private def sendScreenShot(eventObject: ILoggingEvent): Unit = {
    val request = HttpClient.httpClient.preparePost(HttpClient.urlSS)
      .addQueryParameter("testName", eventObject.getMDCPropertyMap.get("testName"))
      .addQueryParameter("testDate", eventObject.getMDCPropertyMap.get("testDate"))
      .addQueryParameter("setName", eventObject.getMDCPropertyMap.get("setName"))
      .addQueryParameter("setDate", eventObject.getMDCPropertyMap.get("setDate"))
      .setBody(eventObject.getArgumentArray()(0).asInstanceOf[Array[Byte]])
      .build()
    val result = HttpClient.httpClient.executeRequest(request).get(15, TimeUnit.SECONDS)
    if (result.getStatusCode != 200) {
      println(result.getStatusText)
      println(result.getResponseBody)
    }
  }

}
