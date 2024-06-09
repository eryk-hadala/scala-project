package controllers

import actors.WorkspacesActor
import actors.WorkspacesActor.{CreateWorkspace, GetById, GetMembers, GetOwner, GetUserWorkspaces, InternalError, WorkspaceNotFound}
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onSuccess
import akka.http.scaladsl.server.Route
import helpers.Timeout.timeout
import helpers.{Auth, Response}
import models.{User, Workspace}

import java.time.LocalDateTime
import scala.concurrent.{Await, Future}

object WorkspacesController {
  case class CreatePayload(override val name: String) extends Workspace(0, name, 0, LocalDateTime.now(), LocalDateTime.now())

  case class UpdatePayload(name: String)

  case class InviteUserPayload(userId: Int)
}

class WorkspacesController(val workspacesActor: ActorRef[WorkspacesActor.Command])
                          (implicit system: ActorSystem[_]) {

  import WorkspacesController.*

  def getWorkspaces: Route = Auth.userRoute(user => {
    val future: Future[Seq[Workspace]] = workspacesActor ? (GetUserWorkspaces(user, _))
    onSuccess(future)(Response.json(_))
  })

  def createWorkspace(payload: CreatePayload): Route = Auth.userRoute(user => {
    val future: Future[Workspace] = workspacesActor ? (CreateWorkspace(payload.name, user, _))
    onSuccess(future)(Response.json(_))
  })

  private def memberRoute(workspaceId: Int)(callback: User => Route): Route = Auth.userRoute(user => {
    val membersFuture: Future[Seq[User] | InternalError] = workspacesActor ? (GetMembers(workspaceId, _))
    val result = Await.result(membersFuture, timeout.duration)
    result match
      case error: InternalError => Response.status(StatusCodes.InternalServerError)
      case members: Seq[User] if !members.exists(_.id == user.id) => Response.status(StatusCodes.Forbidden)
      case _: Seq[User] => callback(user)
  })

  private def ownerRoute(workspaceId: Int)(callback: User => Route): Route = Auth.userRoute(user => {
    val ownerFuture: Future[User | WorkspaceNotFound | InternalError] = workspacesActor ? (GetOwner(workspaceId, _))
    val result = Await.result(ownerFuture, timeout.duration);
    result match
      case _: InternalError => Response.status(StatusCodes.InternalServerError)
      case _: WorkspaceNotFound => Response.status(StatusCodes.NotFound)
      case owner: User if owner.id != user.id => Response.status(StatusCodes.Forbidden)
      case owner: User => callback(user)
  })

  def inviteUser(workspaceId: Int, payload: InviteUserPayload): Route = ownerRoute(workspaceId)(user => {
    val inviteFuture = workspacesActor ? (WorkspacesActor.AddMember(workspaceId, payload, _))
    onSuccess(inviteFuture)(_ => Response.status(StatusCodes.OK))
  })

  def getWorkspace(workspaceId: Int): Route = memberRoute(workspaceId)(user => {
    val workspaceFuture: Future[Option[Workspace]] = workspacesActor ? (GetById(workspaceId, _))
    onSuccess(workspaceFuture) {
      case Some(workspace) => Response.json(workspace)
      case None => Response.status(StatusCodes.NotFound)
    }
  })

  def getMembers(workspaceId: Int): Route = memberRoute(workspaceId)(user => {
    val future: Future[Seq[User] | WorkspacesActor.InternalError] = workspacesActor ? (GetMembers(workspaceId, _))
    onSuccess(future) {
      case users: Seq[User] => Response.json(users)
      case _: WorkspacesActor.InternalError => Response.status(StatusCodes.InternalServerError)
    }
  })

  def updateWorkspace(workspaceId: Int, payload: UpdatePayload): Route = ownerRoute(workspaceId)(user => {
    val updateFuture = workspacesActor ? (WorkspacesActor.UpdateWorkspace(workspaceId, payload, _))
    onSuccess(updateFuture) {
      case _: WorkspaceNotFound => Response.status(StatusCodes.NotFound)
      case workspace: Workspace => Response.json(workspace)
    }
  })

  def deleteWorkspace(workspaceId: Int): Route = ownerRoute(workspaceId)(user => {
    val deleteFuture = workspacesActor ? (WorkspacesActor.DeleteWorkspace(workspaceId, _))
    onSuccess(deleteFuture)(_ => Response.status(StatusCodes.OK))
  })
}

