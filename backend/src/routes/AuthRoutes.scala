package routes

import actors.{AuthActor, UsersActor}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.{as, concat, entity, get, path, pathPrefix, post}
import akka.http.scaladsl.server.Route
import controllers.AuthController
import controllers.AuthController.{SignInPayload, SignUpPayload, UpdatePayload}
import helpers.JsonSupport

class AuthRoutes(authActor: ActorRef[AuthActor.Command], usersActor: ActorRef[UsersActor.Command])
                (implicit system: ActorSystem[_]) extends JsonSupport {
  private val controller = new AuthController(authActor, usersActor)

  def routes: Route = {
    pathPrefix("auth") {
      concat(
        path("sign-up") {
          post {
            entity(as[SignUpPayload]) {
              controller.signUp
            }
          }
        },
        path("sign-in") {
          post {
            entity(as[SignInPayload]) {
              controller.signIn
            }
          }
        },
        path("sign-out") {
          post {
            controller.signOut
          }
        },
        path("me") {
          concat(
            get {
              controller.getSignedIn
            },
            post {
              entity(as[UpdatePayload]) {
                controller.updateUser
              }
            }
          )
        }
      )
    }
  }
}
