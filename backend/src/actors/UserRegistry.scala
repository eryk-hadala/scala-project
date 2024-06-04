package actors

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import slick.jdbc.H2Profile.api.*

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}
import models.{User, Users}

object UserRegistry {

  // actor protocol
  sealed trait Command

  final case class GetUsers(replyTo: ActorRef[Seq[User]]) extends Command

  final case class CreateUser(user: User, replyTo: ActorRef[ActionPerformed]) extends Command

  final case class GetUser(id: Int, replyTo: ActorRef[GetUserResponse]) extends Command

  final case class GetUserResponse(maybeUser: Option[User])

  final case class ActionPerformed(description: String)

  private val users = TableQuery[Users]

  def apply(): Behavior[Command] = Behaviors.setup { context =>
    val db = Database.forConfig("slick.dbs.default")

    context.executionContext.execute(() => db.run(users.schema.create))

    registry(db)
  }

  private def registry(db: Database): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      implicit val ec: ExecutionContextExecutor = context.executionContext
      message match {
        case GetUsers(replyTo) =>
          val usersFuture: Future[Seq[User]] = db.run(users.result)
          usersFuture.onComplete {
            case Success(users) => replyTo ! users
            case Failure(ex) => replyTo ! Seq.empty
          }
          Behaviors.same

        case CreateUser(user, replyTo) =>
          val insertAction = users += user
          val insertFuture = db.run(insertAction)
          insertFuture.onComplete {
            case Success(_) => replyTo ! ActionPerformed(s"User ${user.id} created.")
            case Failure(ex) => replyTo ! ActionPerformed(s"Failed to create user ${user.id}.")
          }
          Behaviors.same

        case GetUser(id, replyTo) =>
          val query = users.filter(_.id === id).result.headOption
          val userFuture = db.run(query)
          userFuture.onComplete {
            case Success(maybeUser) => replyTo ! GetUserResponse(maybeUser)
            case Failure(ex) => replyTo ! GetUserResponse(None)
          }
          Behaviors.same
      }
    }

  //  private final case class WrappedGetUsersResponse(response: Users) extends Command
}
