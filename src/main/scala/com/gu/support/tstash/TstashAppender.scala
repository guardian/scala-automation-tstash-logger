package com.gu.automation.api

import java.io.File

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.UnsynchronizedAppenderBase
import com.gu.support.tstash.HttpClient
import com.ning.http.client._
import com.ning.http.client.websocket.{WebSocket, WebSocketTextListener, WebSocketUpgradeHandler}

import scala.collection.mutable
import scala.concurrent.{Promise, Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object TstashAppender {
  val sockets = mutable.Map[String, WebSocket]()
}

class TstashAppender extends UnsynchronizedAppenderBase[ILoggingEvent] {

  override def append(eventObject: ILoggingEvent): Unit = {
    val failed = """\[FAILED\](.*)""".r

    // TODO: send proper screenshots
    // TODO: store messages for tests
    eventObject.getMessage match {
      case "[TEST START]" => createWebSocket(eventObject).map(TstashAppender.sockets.put(eventObject.getMDCPropertyMap.get("ID"), _))
      case "[TEST END]" => TstashAppender.sockets.get(eventObject.getMDCPropertyMap.get("ID")).map(_.close())
      case failed(m) => sendError(eventObject, m)
      case "[SCREENSHOT]" => sendScreenShot(eventObject)
      case _ => sendMessage(eventObject)
    }
  }

  private def sendMessage(eventObject: ILoggingEvent): Unit = {
    TstashAppender.sockets.get(eventObject.getMDCPropertyMap.get("ID")).map(_.sendTextMessage(
      s"""{
         |"message":"${eventObject.getFormattedMessage}",
         |"timeStamp":"${eventObject.getTimeStamp}"
         |}""".stripMargin))
  }

  private def sendError(eventObject: ILoggingEvent, error: String): Unit = {
    TstashAppender.sockets.get(eventObject.getMDCPropertyMap.get("ID")).map(_.sendTextMessage(
      s"""{
         |"error":"${error}",
         |"timeStamp":"${eventObject.getTimeStamp}"
         |}""".stripMargin))
  }

  private def sendScreenShot(eventObject: ILoggingEvent): Unit = {
    val request = HttpClient.httpClient.preparePost(HttpClient.urlSS)
      .addQueryParameter("testName", eventObject.getMDCPropertyMap.get("testName"))
      .addQueryParameter("testDate", eventObject.getMDCPropertyMap.get("testDate"))
      .addQueryParameter("setName", eventObject.getMDCPropertyMap.get("setName"))
      .addQueryParameter("setDate", eventObject.getMDCPropertyMap.get("setDate"))
      .setBody(new File("ss.png"))
      .build()

    HttpClient.httpClient.executeRequest(request)
  }

  private def createWebSocket(eventObject: ILoggingEvent): Option[WebSocket] = {
    val websocket: WebSocket = HttpClient.httpClient.prepareGet(HttpClient.urlReport)
      .addQueryParameter("testName", eventObject.getMDCPropertyMap.get("testName"))
      .addQueryParameter("testDate", eventObject.getMDCPropertyMap.get("testDate"))
      .addQueryParameter("setName", eventObject.getMDCPropertyMap.get("setName"))
      .addQueryParameter("setDate", eventObject.getMDCPropertyMap.get("setDate"))
      .execute(new WebSocketUpgradeHandler.Builder().addWebSocketListener(
      new WebSocketTextListener() {
        override def onMessage(message: String): Unit = { println("[T-Stash ws message] " + message) }
        override def onFragment(fragment: String, last: Boolean): Unit = { println("[T-Stash ws fragment] " + fragment) }
        override def onError(t: Throwable): Unit = { println("[T-Stash ws error] " + t.getMessage) }
        override def onClose(websocket: WebSocket): Unit = { println("[T-Stash ws closed]") }
        override def onOpen(websocket: WebSocket): Unit = { println("[T-Stash ws opened]") }
      }).build()).get()

    if (websocket == null) {
      println("Failed to create connection to Test-Stash for test: " + eventObject.getMDCPropertyMap.get("testName"))
      return None
    } else {
      return Some(websocket)
    }
  }

}
