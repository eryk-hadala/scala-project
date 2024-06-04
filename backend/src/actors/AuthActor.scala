package actors

import actors.UsersActor.{Command, CreateNew, GetByEmail}
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import models.User
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{ExecutionContextExecutor, Future}
import akka.util.Timeout
import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import controllers.AuthController.{SignInPayload, SignUpPayload}

import java.time.LocalDateTime
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

//final case class SignInPayload(email: String, password: String)

//final case class SignUpPayload(avatarUrl: String, username: String, email: String, password: String)

final case class UpdatePayload(avatarUrl: String, username: String)


object AuthActor {
  sealed trait Command

  final case class SignUp(payload: SignUpPayload, replyTo: ActorRef[SignUpResponse | UserExists | InternalError]) extends Command

  final case class SignIn(payload: SignInPayload, replyTo: ActorRef[SignInResponse | InternalError]) extends Command

  final case class SignUpResponse(user: User)

  final case class UserExists()

  final case class InternalError()

  final case class SignInResponse(user: Option[User])

  final case class UserResponse(id: Int, email: String, username: String, avatarUrl: String,
                                modifiedAt: LocalDateTime, createdAt: LocalDateTime)

  //  private val toResponse = (user: User) => UserResponse(user.id, user.email, user.username,
  //    user.avatarUrl, user.modifiedAt, user.createdAt)
  private val toResponse = (user: User) => user

  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] = Behaviors.setup { context =>

    implicit val timeout: Timeout = 3.seconds
    implicit val scheduler: Scheduler = system.scheduler
    implicit val executionContext: ExecutionContextExecutor = system.executionContext


    Behaviors.receiveMessage {
      case SignUp(payload, replyTo) =>
        val userFuture: Future[Option[User]] = usersActor.ask(ref => UsersActor.GetByEmail(payload.email, ref))
        userFuture.onComplete {
          case Success(Some(_)) => replyTo ! UserExists()
          case Success(None) =>
            val passwordHash = BCrypt.hashpw(payload.password, BCrypt.gensalt)
            val createUserFuture: Future[User] = usersActor.ask(ref =>
              UsersActor.CreateNew(payload.username, payload.email, payload.avatarUrl, passwordHash, ref))
            createUserFuture.onComplete {
              case Success(user) => replyTo ! SignUpResponse(toResponse(user))
              case Failure(_) => replyTo ! InternalError()
            }
          case Failure(_) => replyTo ! InternalError()
        }
        Behaviors.same

      case SignIn(payload, replyTo) =>
        val future: Future[Option[User]] = usersActor ? (ref => GetByEmail(payload.email, ref))
        future.onComplete {
          case Success(Some(user)) =>
            if BCrypt.checkpw(payload.password, user.password) then
              replyTo ! SignInResponse(Some(user))
            else
              replyTo ! SignInResponse(None)
          case Success(None) => replyTo ! SignInResponse(None)
          case Failure(_) => replyTo ! InternalError()
        }
        Behaviors.same
    }
  }
}
