package controllers

import actors.AuthActor.{InternalError, SignInResponse, SignUpResponse, UserExists}
import actors.{AuthActor, UsersActor}
import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Directives.{deleteCookie, onSuccess, setCookie}
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import helpers.{Auth, Jwt, Response}
import models.User

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.concurrent.duration.*

object AuthController {
  private val cookieKey = "user"

  case class SignInPayload(email: String, password: String)

  case class SignUpPayload(avatarUrl: String, username: String, email: String, password: String)

  case class UpdatePayload(avatarUrl: String, username: String)

  private final case class UserResponse(id: Int, email: String, username: String, avatarUrl: String,
                                        modifiedAt: LocalDateTime, createdAt: LocalDateTime)
}

class AuthController(val authActor: ActorRef[AuthActor.Command], val usersActor: ActorRef[UsersActor.Command])
                    (implicit system: ActorSystem[_]) {

  import AuthController.*

  implicit val timeout: Timeout = 5.seconds

  private def setUserCookie(user: User): Route = {
    val token = Jwt.encode(user)
    setCookie(HttpCookie(cookieKey, value = token, path = Some("/"))) {
      Response.json(user)
    }
  }

  def signUp(payload: SignUpPayload): Route = {
    val future: Future[SignUpResponse | UserExists | InternalError] = authActor ? (AuthActor.SignUp(payload, _))
    onSuccess(future) {
      case SignUpResponse(user) => Response.json(user)
      case _: UserExists => Response.json(StatusCodes.BadRequest, "User already exists")
      case _: InternalError => Response.json(StatusCodes.InternalServerError, "Internal error")
    }
  }

  def signIn(payload: SignInPayload): Route = {
    val future: Future[SignInResponse | InternalError] = authActor ? (AuthActor.SignIn(payload, _))

    onSuccess(future) {
      case SignInResponse(Some(user)) => setUserCookie(user)
      case SignInResponse(None) => Response.json(StatusCodes.Unauthorized, "Unauthorized")
      case _: InternalError => Response.json(StatusCodes.InternalServerError, "Internal error")
    }
  }

  def signOut: Route = {
    deleteCookie(cookieKey, path = "/") {
      Response.json("Success")
    }
  }

  def getSignedIn: Route = Auth.userRoute(user => Response.json(user))

  def updateUser(payload: UpdatePayload): Route = Auth.userRoute { oldUser =>
    val updateUserFuture: Future[User] =
      usersActor ? (UsersActor.Update(oldUser.id, payload.avatarUrl, payload.username, _))
    onSuccess(updateUserFuture)(Response.json(_))
  }
}
