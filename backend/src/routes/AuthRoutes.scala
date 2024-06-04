package routes

import actors.{AuthActor, UsersActor}
import akka.http.scaladsl.server.Directives.{as, concat, entity, get, path, pathPrefix, post}
import akka.http.scaladsl.server.Route
import akka.actor.typed.{ActorRef, ActorSystem}
import controllers.AuthController
import controllers.AuthController.{SignInPayload, SignUpPayload, UpdatePayload}
import helpers.JsonSupport

object AuthRoutes extends JsonSupport {
  def routes(authActor: ActorRef[AuthActor.Command], usersActor: ActorRef[UsersActor.Command], system: ActorSystem[_]): Route = {
    pathPrefix("auth") {
      concat(
        path("sign-up") {
          post {
            entity(as[SignUpPayload]) { payload =>
              AuthController.signUp(authActor, payload)(system.scheduler)
            }
          }
        },
        path("sign-in") {
          post {
            entity(as[SignInPayload]) { payload =>
              AuthController.signIn(authActor, payload)(system.scheduler)
            }
          }
        },
        path("sign-out") {
          post {
            AuthController.signOut
          }
        },
        path("me") {
          concat(
            get {
              AuthController.getSignedIn
            },
            post {
              entity(as[UpdatePayload]) { payload =>
                AuthController.updateUser(usersActor, payload)(system.scheduler)
              }
            }
          )
        }
      )
    }
  }
}
