package actors

import actors.UsersActor.GetByIds
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector}
import controllers.WorkspacesController.{InviteUserPayload, UpdatePayload}
import helpers.Database
import helpers.Timeout.timeout
import models.*
import slick.jdbc.SQLiteProfile.api.*

import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object WorkspacesActor {
  sealed trait Command

  final case class GetUserWorkspaces(user: User, replyTo: ActorRef[Seq[Workspace]]) extends Command

  final case class CreateWorkspace(name: String, user: User, replyTo: ActorRef[Workspace]) extends Command

  final case class GetById(workspaceId: Int, replyTo: ActorRef[Option[Workspace]]) extends Command

  final case class GetMembers(workspaceId: Int, replyTo: ActorRef[Seq[User] | InternalError]) extends Command

  final case class AddMember(workspaceId: Int, payload: InviteUserPayload, replyTo: ActorRef[InvitedSuccessfully]) extends Command

  final case class GetOwner(workspaceId: Int, replyTo: ActorRef[User | WorkspaceNotFound | InternalError]) extends Command

  final case class UpdateWorkspace(workspaceId: Int, payload: UpdatePayload, replyTo: ActorRef[Workspace | WorkspaceNotFound]) extends Command

  final case class DeleteWorkspace(workspaceId: Int, replyTo: ActorRef[DeletedSuccessfully]) extends Command

  final case class InternalError()

  final case class WorkspaceNotFound()

  final case class DeletedSuccessfully()

  final case class InvitedSuccessfully()

  private val userWorkspaces = TableQuery[UserWorkspaces]
  private val workspaces = TableQuery[Workspaces]

  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case GetUserWorkspaces(user, replyTo) =>
          replyTo ! getUserWorkspaces(user.id)
          Behaviors.same

        case CreateWorkspace(name, user, replyTo) =>
          val workspace = Workspace(0, name, user.id, LocalDateTime.now(), LocalDateTime.now())

          val inserted = insert(workspace)
          addMember(user.id, inserted.id)

          replyTo ! inserted
          Behaviors.same

        case UpdateWorkspace(workspaceId, payload, replyTo) =>
          val workspaceOption = getById(workspaceId)
          workspaceOption match
            case None => replyTo ! WorkspaceNotFound()
            case Some(workspace) => replyTo ! update(workspaceId, payload.name)

          Behaviors.same
        case DeleteWorkspace(workspaceId, replyTo) =>
          delete(workspaceId)
          replyTo ! DeletedSuccessfully()
          Behaviors.same

        case GetById(workspaceId, replyTo) =>
          replyTo ! getById(workspaceId)
          Behaviors.same

        case GetOwner(workspaceId, replyTo) =>
          val workspaceOption = getById(workspaceId)
          workspaceOption match
            case None => replyTo ! WorkspaceNotFound()
            case Some(workspace) =>
              val future: Future[Option[User]] = usersActor ? (UsersActor.GetById(workspace.ownerId, _))
              future.onComplete {
                case Failure(_) => replyTo ! InternalError()
                case Success(Some(user)) => replyTo ! user
                case Success(None) => WorkspaceNotFound()
              }
          Behaviors.same

        case GetMembers(workspaceId, replyTo) =>
          val workspaceUserIds = getWorkspaceUserIds(workspaceId)
          val workspaceUsersFuture: Future[Seq[User]] = usersActor ? (GetByIds(workspaceUserIds, _))

          workspaceUsersFuture.onComplete {
            case Success(users) => replyTo ! users
            case Failure(_) => replyTo ! InternalError()
          }
          Behaviors.same

        case AddMember(workspaceId, payload, replyTo) =>
          val workspaceUserIds = getWorkspaceUserIds(workspaceId)
          if !workspaceUserIds.contains(payload.userId) then
            addMember(payload.userId, workspaceId)
          replyTo ! InvitedSuccessfully()
          Behaviors.same
      }
    }

  private def getUserWorkspaces(id: Int): Seq[Workspace] = {
    val query = (for {
      (ab, a) <- userWorkspaces join workspaces on (_.workspaceId === _.id)
    } yield (a, ab.userId)).filter(_._2 === id).map(_._1).result
    val result: Seq[Workspace] = Database.exec(query)
    result
  }

  private def getById(id: Int): Option[Workspace] = {
    val query = workspaces.filter(_.id === id).result.headOption
    val result: Option[Workspace] = Database.exec(query)
    result
  }

  private def insert(workspace: Workspace): Workspace = {
    val query = (workspaces returning workspaces.map(_.id)) += workspace
    val result: Int = Database.exec(query)
    getById(result).get
  }

  private def addMember(userId: Int, workspaceId: Int): Boolean = {
    val query = userWorkspaces += (userId, workspaceId, LocalDateTime.now())
    val result = Database.exec(query)
    result > 0
  }

  private def getWorkspaceUserIds(id: Int): Seq[Int] = {
    val query = userWorkspaces.filter(_.workspaceId === id).map(_.userId).result
    Database.exec(query)
  }

  private def update(id: Int, name: String): Workspace = {
    val query = workspaces.filter(_.id === id)
      .map(oldWorkspace => (oldWorkspace.name, oldWorkspace.modifiedAt))
      .update((name, LocalDateTime.now()))
    Database.exec(query)
    getById(id).get
  }

  private def delete(id: Int) = {
    val query = workspaces.filter(_.id === id).delete
    Database.exec(query)
  }
}
