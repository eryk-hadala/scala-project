package helpers

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

object Cors {
  private val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`(HttpOrigin("http://localhost:3000")),
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin","Accept", "X-Requested-With", "Content-Type", "Access-Control-Request-Method", "Access-Control-Request-Headers")
  )

  private def addCorsHeaders(): Directive0 = {
    respondWithHeaders(corsResponseHeaders)
  }

  private def preflightRequestHandler: Route = options {
    complete(
      HttpResponse(StatusCodes.OK).withHeaders(
        `Access-Control-Allow-Methods`(OPTIONS, POST, PUT, GET, DELETE),
      )
    )
  }

  def corsHandler(r: Route): Route = addCorsHeaders() {
    preflightRequestHandler ~ r
  }

  def corsHandlerWithLogging(r: Route): Route = {
    extractRequest { request =>
      println(s"Request: $request")
      corsHandler(r)
    }
  }
}
