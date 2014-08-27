package com.gu.support.tstash

import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig}

/**
 * Created by ipamer on 27/08/2014.
 */
object HttpClient {

  lazy val urlReport = s"ws://${sys.props.getOrElse("teststash.url", "NOTSET")}/report"
  lazy val urlSS = s"http://${sys.props.getOrElse("teststash.url", "NOTSET")}/screenShotUpload"

  private lazy val config = new AsyncHttpClientConfig.Builder()
  lazy val httpClient = new AsyncHttpClient(config.build())

//  if (url.contains("NOTSET") ||
//    eventObject.getMDCPropertyMap.get("testName") == null ||
//    eventObject.getMDCPropertyMap.get("testDate") == null ||
//    eventObject.getMDCPropertyMap.get("setName") == null ||
//    eventObject.getMDCPropertyMap.get("setDate") == null) {
//    return None
//  }

//  val result = Promise[Response]()
//  val builder = new Response.ResponseBuilder()
//
//  val httpHandler = new AsyncHandler[Unit] {
//    @volatile
//    var finished = false
//
//    private def finish(body: => Unit) {
//      if (!finished)
//        try {
//          body
//        }
//        catch {
//          case t: Throwable =>
//            result.tryComplete(Failure(t))
//        }
//        finally {
//          finished = true
//          assert(result.isCompleted)
//        }
//    }
//
//    def onThrowable(t: Throwable) {
//      finish {
//        throw t
//      }
//    }
//
//    def onBodyPartReceived(bodyPart: HttpResponseBodyPart) = {
//      builder.accumulate(bodyPart)
//      AsyncHandler.STATE.CONTINUE
//    }
//
//    def onStatusReceived(responseStatus: HttpResponseStatus) = {
//      builder.accumulate(responseStatus)
//      AsyncHandler.STATE.CONTINUE
//    }
//
//    def onHeadersReceived(headers: HttpResponseHeaders) = {
//      builder.accumulate(headers)
//      AsyncHandler.STATE.CONTINUE
//    }
//
//    def onCompleted() {
//      finish {
//        val response = builder.build()
//        result.tryComplete(Success(response))
//      }
//    }
//  }
// // GET the response:
//    client.executeRequest(request, httpHandler)
//    val future = result.future
//
//    future.map { result =>
//      result.getStatusCode match {
//        case 200 => println("COMPLETED OK!")
//        case _ => println("COMPLETED ERR: " + result.getStatusText + "     " + result.getResponseBody)
//      }
//    }.recover {
//      case e: Throwable => {
//        println("COMPLETED EXC: " + e)
//      }
//    }

}
