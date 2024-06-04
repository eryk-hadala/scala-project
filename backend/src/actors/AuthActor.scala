package actors

import actors.UsersActor.{Command, CreateNew, GetByEmail}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import models.User
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContextExecutor
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors

import java.time.LocalDateTime
import scala.concurrent.duration.*
import scala.util.Success

final case class SignInPayload(email: String, password: String)

final case class SignUpPayload(avatarUrl: String, username: String, email: String, password: String)

final case class UpdatePayload(avatarUrl: String, username: String)


object AuthActor {
  sealed trait Command

  final case class SignUp(payload: SignUpPayload, replyTo: ActorRef[SignUpResponse]) extends Command

  final case class SignIn(payload: SignInPayload, replyTo: ActorRef[SignInResponse]) extends Command

  final case class SignUpResponse(user: Option[UserResponse])

  final case class SignInResponse(success: Boolean)

  final case class UserResponse(id: Int, email: String, username: String, avatarUrl: String,
                                modifiedAt: LocalDateTime, createdAt: LocalDateTime)

  private val mapToResponse = (user: User) => UserResponse(user.id, user.email, user.username,
    user.avatarUrl, user.modifiedAt, user.createdAt)

  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] = Behaviors.setup { context =>

    implicit val timeout: Timeout = 3.seconds
    implicit val scheduler: Scheduler = system.scheduler
    implicit val executionContext: ExecutionContextExecutor = system.executionContext

    Behaviors.receiveMessage {
      case SignUp(payload, replyTo) =>
        val createNewResponse = usersActor ?
          (ref => CreateNew(payload.username, payload.email, payload.avatarUrl, payload.password, ref))

        createNewResponse.onComplete {
          case Success(user: User) => replyTo ! SignUpResponse(Some(mapToResponse(user)))
          case _ => replyTo ! SignUpResponse(None)
        }
        Behaviors.same
      case SignIn(payload, replyTo) =>
        val getResponse = usersActor ? (ref => GetByEmail(payload.email, ref))
        getResponse.onComplete {
          case Success(user: User) => SignInResponse(BCrypt.checkpw(payload.password, user.password))
          case _ => SignInResponse(false)
        }
        Behaviors.same
    }
  }
}
