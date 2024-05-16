package controllers.authorization

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpCookie
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{deleteCookie, setCookie, cookie}
import helpers.database.Query
import helpers.response.Response
import routes.authorization.User
import org.mindrot.jbcrypt.BCrypt
import helpers.jwt.JWT

def signUp(payload: User): Route = {
  if payload.username.isDefined && payload.username.get.trim.isEmpty then return Response.json(StatusCodes.BadRequest, """{"message": "Field username is empty!"}""")
  val username = payload.username.get

  val result = Query.get(s"SELECT * FROM users WHERE username = '$username'")
  if result.next() then return Response.json(StatusCodes.BadRequest, """{"message": "Already exists"}""")

  if payload.password.isDefined && payload.password.get.trim.isEmpty then return Response.json(StatusCodes.BadRequest, """{"message": "Field password is empty!"}""")
  val password = payload.password.get

  val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt)

  try {
    Query.execute(s"INSERT INTO users (username, password, avatarUrl, updatedAt, createdAt) VALUES ('$username', '$passwordHash', '', DATETIME('now'), DATETIME('now'))")
  }catch
    case e: Exception => println(e)

  Response.json("""{"message": "Success"}""")
}

def signIn(payload: User): Route = {
  if payload.username.isDefined && payload.username.get.trim.isEmpty then return Response.json(StatusCodes.BadRequest, """{"message": "Field username is empty!"}""")
  val username = payload.username.get

  if payload.password.isDefined && payload.password.get.trim.isEmpty then return Response.json(StatusCodes.BadRequest, """{"message": "Field password is empty!"}""")
  val password = payload.password.get

  val result = Query.get(s"SELECT * FROM users WHERE username = '$username'")
  if !result.next() then return Response.json(StatusCodes.Unauthorized, """{"message": "Unauthorized"}""")

  val id = result.getInt("id")
  val passwordHash = result.getString("password")
  val avatarUrl = result.getString("avatarUrl")
  val updatedAt = result.getString("updatedAt")
  val createdAt = result.getString("createdAt")

  result.close()

  if !BCrypt.checkpw(password, passwordHash) then return Response.json(StatusCodes.Unauthorized, """{"message": "Unauthorized"}""")

  val token = JWT.encode(s"""{"id": $id, "username": "$username", "avatar": {"src": "$avatarUrl", "alt": ""}, "updatedAt": "$updatedAt", "createdAt": "$createdAt"}""")

  setCookie(HttpCookie("user", value = token)):
    Response.json("""{"message": "Success"}""")
}

def signOut(): Route = {
  deleteCookie("user"):
    Response.json("""{"message": "Success"}""")
}

def getSignedIn: Route = {
  cookie("user"): userCookie =>
    Response.json(JWT.decode(userCookie.value).get.toJson)
} 