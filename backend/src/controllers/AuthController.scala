package controllers

import actors.AuthActor.{InternalError, SignInResponse, SignUpResponse, UserExists}
import actors.{AuthActor, UsersActor}
import akka.actor.typed.{ActorRef, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.*
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Route
import helpers.Jwt
import helpers.Response
import org.mindrot.jbcrypt.BCrypt
import akka.http.scaladsl.server.Directives.{cookie, deleteCookie, onSuccess, setCookie}
import models.User
import akka.util.Timeout

import java.time.LocalDateTime
import scala.util.{Failure, Success}
import scala.concurrent.Future
import scala.concurrent.duration.*

object AuthController {
  private val cookieKey = "user"

  case class SignInPayload(email: String, password: String)

  case class SignUpPayload(avatarUrl: String, username: String, email: String, password: String)

  case class UpdatePayload(avatarUrl: String, username: String)

  private final case class UserResponse(id: Int, email: String, username: String, avatarUrl: String,
                                        modifiedAt: LocalDateTime, createdAt: LocalDateTime)

  private val toResponse = (user: User) => UserResponse(user.id, user.email, user.username,
    user.avatarUrl, user.modifiedAt, user.createdAt)

  implicit val timeout: Timeout = 5.seconds

  private def setUserCookie(user: User): Route = {
    val token = Jwt.encode(user)
    setCookie(HttpCookie(cookieKey, value = token, path = Some("/"))) {
      Response.json(user)
    }
  }

  def authenticate(callback: User => Route): Route = {
    cookie(cookieKey): userCookie =>
      callback(User.parse(Jwt.decode(userCookie.value).get))
  }

  def signUp(authActor: ActorRef[AuthActor.Command], payload: SignUpPayload)(implicit scheduler: Scheduler): Route = {
    val future: Future[SignUpResponse | UserExists | InternalError] = authActor.ask(ref => AuthActor.SignUp(payload, ref))
    onSuccess(future) {
      case SignUpResponse(user) => Response.json(user)
      case _: UserExists => Response.json(StatusCodes.BadRequest, "User already exists")
      case _: InternalError => Response.json(StatusCodes.InternalServerError, "Internal error")
    }
  }

  def signIn(authActor: ActorRef[AuthActor.Command], payload: SignInPayload)(implicit scheduler: Scheduler): Route = {
    val future: Future[SignInResponse | InternalError] = authActor.ask(ref => AuthActor.SignIn(payload, ref))

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

  def getSignedIn: Route = authenticate(user => Response.json(user))

  def updateUser(usersActor: ActorRef[UsersActor.Command], payload: UpdatePayload)(implicit scheduler: Scheduler): Route = {
    authenticate { oldUser =>
      val updateUserFuture: Future[User] = usersActor.ask(ref => UsersActor.Update(oldUser.id, payload.avatarUrl, payload.username, ref))
      onSuccess(updateUserFuture)(Response.json(_))
    }
  }
}
