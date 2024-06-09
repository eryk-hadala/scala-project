package actors

import actors.UsersActor.{Command, CreateNew, GetByEmail}
import akka.actor.typed.scaladsl.AskPattern.*
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector}
import controllers.AuthController.{SignInPayload, SignUpPayload}
import helpers.Timeout.timeout
import models.User
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object AuthActor {
  sealed trait Command

  final case class SignUp(payload: SignUpPayload, replyTo: ActorRef[SignUpResponse | UserExists | InternalError]) extends Command

  final case class SignIn(payload: SignInPayload, replyTo: ActorRef[SignInResponse | InternalError]) extends Command

  final case class SignUpResponse(user: User)

  final case class UserExists()

  final case class InternalError()

  final case class SignInResponse(user: Option[User])

  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SignUp(payload, replyTo) =>
          val userFuture: Future[Option[User]] = usersActor ? (UsersActor.GetByEmail(payload.email, _))
          userFuture.onComplete {
            case Success(Some(_)) => replyTo ! UserExists()
            case Success(None) =>
              val passwordHash = BCrypt.hashpw(payload.password, BCrypt.gensalt)
              val createUserFuture: Future[User] =
                usersActor ? (UsersActor.CreateNew(payload.username, payload.email, payload.avatarUrl, passwordHash, _))
              createUserFuture.onComplete {
                case Success(user) => replyTo ! SignUpResponse(user)
                case Failure(_) => replyTo ! InternalError()
              }
            case Failure(_) => replyTo ! InternalError()
          }
          Behaviors.same

        case SignIn(payload, replyTo) =>
          val future: Future[Option[User]] = usersActor ? (GetByEmail(payload.email, _))
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
