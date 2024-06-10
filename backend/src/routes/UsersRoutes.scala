package routes

import actors.UsersActor
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import helpers.Timeout.timeout
import helpers.{JsonSupport, Response}
import models.User

import scala.concurrent.Future

class UsersRoutes(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]) extends JsonSupport {
  def routes: Route = {
    pathPrefix("users") {
      path("find") {
        get {
          parameters("search") { search =>
            val future: Future[Seq[User]] = usersActor ? (UsersActor.FindUsers(search, _))
            onSuccess(future)(Response.json)
          }
        }
      }
    }
  }
}
