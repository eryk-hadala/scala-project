package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Route
import helpers.Jwt
import helpers.Response
import org.mindrot.jbcrypt.BCrypt
import akka.http.scaladsl.server.Directives.{cookie, deleteCookie, setCookie}
import models.{User, UsersModel}

import java.time.LocalDateTime
object AuthController {
  private val cookieKey = "user"
  case class SignInPayload(email: String, password: String)
  case class SignUpPayload(override val avatarUrl: String, override val username: String, override val email: String, override val password: String) extends User(0, avatarUrl, username, email, password, LocalDateTime.now(), LocalDateTime.now())
  case class UpdatePayload(avatarUrl: String, username: String)

  def setUserCookie(user: User): Route = {
    val token = Jwt.encode(user)
    setCookie(HttpCookie(cookieKey, value = token, path = Some("/"))):
      Response.json(user)
  }
  
  def authenticate(callback: User => Route): Route = {
    cookie(cookieKey): userCookie =>
      callback(User.parse(Jwt.decode(userCookie.value).get))
  }

  def signUp(payload: SignUpPayload): Route = {
    try {
      val user = UsersModel.getByEmail(payload.email)
      Response.json(StatusCodes.BadRequest, "Bad Request")
    } catch
        case _: Exception =>
          val passwordHash = BCrypt.hashpw(payload.password, BCrypt.gensalt)
          val user = UsersModel.insert(payload.copy(password = passwordHash))
          Response.json(user)

        case _ => Response.json(StatusCodes.InternalServerError, "Internal Server Error")
  }
  
  def signIn(payload: SignInPayload): Route = {
    val user = UsersModel.getByEmail(payload.email)
    if !BCrypt.checkpw(payload.password, user.password) then return Response.json(StatusCodes.Unauthorized, "Unauthorized")
    setUserCookie(user)
  }

  def signOut: Route = {
    deleteCookie(cookieKey, path = "/"):
      Response.json("Success")
  }
  
  def getSignedIn: Route = authenticate(user => Response.json(user))

  def updateUser(payload: UpdatePayload): Route = authenticate(oldUser => {
    val user = UsersModel.update(oldUser.id, payload.avatarUrl, payload.username)
    setUserCookie(user)
  })
}
