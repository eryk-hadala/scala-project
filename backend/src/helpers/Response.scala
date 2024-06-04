package helpers

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import upickle.default.*

object Response:
  def json(body: String): StandardRoute =
    Thread.sleep(500)
    complete(HttpEntity(ContentTypes.`application/json`, body))

  def json[T: Writer](body: T): StandardRoute =
    Thread.sleep(500)
    complete(HttpEntity(ContentTypes.`application/json`, write(body)))

  def json(status: StatusCode, body: String): StandardRoute =
    Thread.sleep(500)
    complete(status, HttpEntity(ContentTypes.`application/json`, body))
    
  def status(status: StatusCode): StandardRoute =
    complete(status)

  def json(status: StatusCode, body: Object): StandardRoute =
    Thread.sleep(500)
    complete(status, HttpEntity(ContentTypes.`application/json`, body.toString))