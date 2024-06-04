package controllers

import actors.UsersActor
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

import scala.concurrent.Future
import scala.concurrent.duration.*

object AuthController {
  private val cookieKey = "user"

  case class SignInPayload(email: String, password: String)

  case class SignUpPayload(avatarUrl: String, username: String, email: String, password: String)

  case class UpdatePayload(avatarUrl: String, username: String)

  implicit val timeout: Timeout = 5.seconds

  def setUserCookie(user: User): Route = {
    val token = Jwt.encode(user)
    setCookie(HttpCookie(cookieKey, value = token, path = Some("/"))) {
      Response.json(user)
    }
  }

  def authenticate(callback: User => Route): Route = {
    cookie(cookieKey): userCookie =>
      callback(User.parse(Jwt.decode(userCookie.value).get))
  }

  def signUp(userRegistry: ActorRef[UsersActor.Command], payload: SignUpPayload)(implicit scheduler: Scheduler): Route = {
    val userFuture: Future[Option[User]] = userRegistry.ask(ref => UsersActor.GetByEmail(payload.email, ref))
    onSuccess(userFuture) {
      case Some(_) =>
        Response.json(StatusCodes.BadRequest, "User already exists")
      case None =>
        val passwordHash = BCrypt.hashpw(payload.password, BCrypt.gensalt)
        val createUserFuture: Future[User] = userRegistry.ask(ref =>
          UsersActor.CreateNew(payload.username, payload.email, payload.avatarUrl, payload.password, ref))
        onSuccess(createUserFuture)(Response.json(_))
    }
  }

  def signIn(userRegistry: ActorRef[UsersActor.Command], payload: SignInPayload)(implicit scheduler: Scheduler): Route = {
    val userFuture: Future[Option[User]] = userRegistry.ask(ref => UsersActor.GetByEmail(payload.email, ref))
    onSuccess(userFuture) {
      case Some(user) if BCrypt.checkpw(payload.password, user.password) =>
        setUserCookie(user)
      case _ => Response.json(StatusCodes.Unauthorized, "Unauthorized")
    }
  }

  def signOut: Route = {
    deleteCookie(cookieKey, path = "/") {
      Response.json("Success")
    }
  }

  def getSignedIn(userRegistry: ActorRef[UsersActor.Command]): Route = authenticate(user => Response.json(user))

  def updateUser(userRegistry: ActorRef[UsersActor.Command], payload: UpdatePayload)(implicit scheduler: Scheduler): Route = {
    authenticate { oldUser =>
      val updateUserFuture: Future[User] = userRegistry.ask(ref => UsersActor.Update(oldUser.id, payload.avatarUrl, payload.username, ref))
      onSuccess(updateUserFuture)(Response.json(_))
    }
  }
}
