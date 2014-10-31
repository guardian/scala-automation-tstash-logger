package com.gu.support.tstash

import com.ning.http.client.{AsyncHttpClient, AsyncHttpClientConfig}

/**
 * Created by ipamer on 27/08/2014.
 */
object HttpClient {

  lazy val urlReport = s"http://${sys.props.getOrElse("teststash.url", "NOTSET")}/report"
  lazy val urlSS = s"http://${sys.props.getOrElse("teststash.url", "NOTSET")}/screenShotUpload"

  private lazy val config = new AsyncHttpClientConfig.Builder()
  lazy val httpClient = new AsyncHttpClient(config.build())

}
