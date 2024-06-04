package models

import helpers.Database
import pdi.jwt.JwtClaim
import slick.jdbc.SQLiteProfile.api.*
import slick.lifted.ProvenShape
import upickle.default.*

import java.time.LocalDateTime

class User(val id: Int, val avatarUrl: String, val username: String, val email: String,
           val password: String, val modifiedAt: LocalDateTime, val createdAt: LocalDateTime)

object User {
  implicit val rw: ReadWriter[User] = readwriter[ujson.Value].bimap[User](
    user => ujson.Obj(
      "id" -> user.id,
      "avatarUrl" -> user.avatarUrl,
      "username" -> user.username,
      "email" -> user.email,
      "password" -> user.password,
      "modifiedAt" -> user.modifiedAt.toString,
      "createdAt" -> user.createdAt.toString
    ),
    json => User(
      json("id").num.toInt,
      json("avatarUrl").str,
      json("username").str,
      json("email").str,
      json("password").str,
      LocalDateTime.parse(json("modifiedAt").str),
      LocalDateTime.parse(json("createdAt").str)
    )
  )

  def parse(claim: JwtClaim): User = {
    val json = ujson.read(claim.toJson)
    User(
      json("id").num.toInt,
      json("avatarUrl").str,
      json("username").str,
      json("email").str,
      json("password").str,
      LocalDateTime.parse(json("modifiedAt").str),
      LocalDateTime.parse(json("createdAt").str)
    )
  }

  def apply(id: Int,
            avatarUrl: String,
            username: String,
            email: String,
            password: String,
            modifiedAt: LocalDateTime,
            createdAt: LocalDateTime): User =
    new User(id, avatarUrl, username, email, password, modifiedAt, createdAt)

  def unapply(user: User): Option[(Int, String, String, String, String, LocalDateTime, LocalDateTime)] =
    Some((
      user.id,
      user.avatarUrl,
      user.username,
      user.email,
      user.password,
      user.modifiedAt,
      user.createdAt
    ))
}

class Users(tag: Tag) extends Table[User](tag, "Users") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def avatarUrl = column[String]("avatarUrl")

  def username = column[String]("username")

  def email = column[String]("email")

  def password = column[String]("password")

  def modifiedAt = column[LocalDateTime]("modifiedAt")

  def createdAt = column[LocalDateTime]("createdAt")

  def * : ProvenShape[User] = (id, avatarUrl, username, email, password, modifiedAt, createdAt) <> (User.apply, User.unapply)
}

object UsersModel {
  private val users = TableQuery[Users]

  def getById(id: Int): User = {
    val query = users.filter(_.id === id).result.headOption
    val result: Option[User] = Database.exec(query)
    val user = result.get
    User(user.id, user.avatarUrl, user.username, user.email, "", user.modifiedAt, user.createdAt)
  }
}