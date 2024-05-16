package routes.authorization

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{as, entity, path, pathPrefix, get, post, concat}
import akka.http.scaladsl.server.Route
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import controllers.authorization.{signUp, signIn, signOut, getSignedIn}

case class User(id: Option[Int], username: Option[String], password: Option[String], updatedAt: Option[String], createdAt: Option[String])

class AuthorizationRoutes extends DefaultJsonProtocol with SprayJsonSupport:
  implicit val userFormat: RootJsonFormat[User] = jsonFormat5(User.apply)

  def use: Route =
    pathPrefix("auth"):
      concat(
        path("sign-up"):
          post:
            entity(as[User]):
              signUp,
        path("sign-in"):
          post:
            entity(as[User]):
              signIn,
        path("sign-out"):
          post:
            signOut(),
        path("me"):
          get:
            getSignedIn
      )
