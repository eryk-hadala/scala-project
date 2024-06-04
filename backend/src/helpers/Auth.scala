package helpers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.cookie
import akka.http.scaladsl.server.Route
import models.User
import pdi.jwt.JwtClaim

import scala.util.{Failure, Success, Try}

object Auth {
  def userRoute(callback: User => Route): Route = {
    val cookieName = "user"
    cookie(cookieName): userCookie =>
      val decoded: Try[JwtClaim] = Jwt.decode(userCookie.value)
      decoded match
        case Success(userJwt: JwtClaim) => callback(User.parse(userJwt))
        case Failure(_) => Response.json(StatusCodes.Unauthorized, "Unauthorized")
  }
}
