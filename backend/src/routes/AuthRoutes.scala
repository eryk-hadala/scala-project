package routes

import akka.http.scaladsl.server.Directives.{as, concat, entity, get, path, pathPrefix, post}
import akka.http.scaladsl.server.Route
import controllers.AuthController
import helpers.{Database, JsonSupport, Response}
import slick.jdbc.SQLiteProfile.api.*
import models.{Issues, UserWorkspaces, Workspaces}

object AuthRoutes extends JsonSupport {
  val routes: Route = pathPrefix("auth"):
    concat(
      path("init"):
        val users = TableQuery[Issues]
        Database.exec(users.schema.create)
        Response.json("success"),
      path("sign-up"):
        post:
          entity(as[AuthController.SignUpPayload]):
            AuthController.signUp,
      path("sign-in"):
        post:
          entity(as[AuthController.SignInPayload]):
            AuthController.signIn,
      path("sign-out"):
        post:
          AuthController.signOut,
      path("me"):
        concat(
          get:
            AuthController.getSignedIn,
          post:
            entity(as[AuthController.UpdatePayload]):
              AuthController.updateUser
        )
    )
}
