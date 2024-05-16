package helpers.response

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute

object Response:
  def json(body: String): StandardRoute =
    complete(HttpEntity(ContentTypes.`application/json`, body))

  def json(status: StatusCode, body: String): StandardRoute =
    complete(status, HttpEntity(ContentTypes.`application/json`, body))