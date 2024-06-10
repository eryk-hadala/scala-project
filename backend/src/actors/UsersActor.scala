package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import helpers.Database
import models.{User, Users}
import slick.jdbc.SQLiteProfile.api.*

import java.time.LocalDateTime


object UsersActor {
  sealed trait Command

  final case class GetById(id: Int, replyTo: ActorRef[Option[User]]) extends Command

  final case class GetByIds(ids: Seq[Int], replyTo: ActorRef[Seq[User]]) extends Command

  final case class GetByEmail(email: String, replyTo: ActorRef[Option[User]]) extends Command

  final case class CreateNew(username: String, email: String,
                             avatarUrl: String, passwordHash: String,
                             replyTo: ActorRef[User]) extends Command

  final case class Update(id: Int, avatarUrl: String, username: String, replyTo: ActorRef[User]) extends Command


  def apply(): Behavior[Command] = Behaviors.setup { context =>
    val users = TableQuery[Users]

    def getUserById(id: Int): Option[User] =
      val query = users.filter(_.id === id).result.headOption
      val result: Option[User] = Database.exec(query)
      result

    Behaviors.receiveMessage {
      case GetById(id, replyTo) =>
        replyTo ! getUserById(id)
        Behaviors.same

      case GetByIds(ids, replyTo) =>
        replyTo ! ids.map(getUserById).filter(_.isDefined).map(_.get)
        Behaviors.same

      case GetByEmail(email, replyTo) =>
        val query = users.filter(_.email === email).result.headOption
        val result: Option[User] = Database.exec(query)
        replyTo ! result
        Behaviors.same

      case CreateNew(username, email, avatarUrl, passwordHash, replyTo) =>
        val user = User(0, avatarUrl, username, email, passwordHash, LocalDateTime.now(), LocalDateTime.now())
        val addQuery = (users returning users.map(_.id)) += user
        val id: Int = Database.exec(addQuery)

        replyTo ! getUserById(id).get
        Behaviors.same

      case Update(id: Int, avatarUrl: String, username: String, replyTo) =>
        val updateQuery = users.filter(_.id === id)
          .map(oldUser => (oldUser.avatarUrl, oldUser.username, oldUser.modifiedAt))
          .update((avatarUrl, username, LocalDateTime.now()))
        Database.exec(updateQuery)

        replyTo ! getUserById(id).get
        Behaviors.same
    }
  }
}
