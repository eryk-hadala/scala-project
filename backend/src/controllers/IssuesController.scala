package controllers

import actors.IssuesActor.*
import actors.WorkspacesActor.{GetMembers, InternalError}
import actors.{IssuesActor, WorkspacesActor}
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.onSuccess
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import helpers.{Auth, Response}
import models.*
import upickle.default.*

import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

object IssuesController {
  case class CreatePayload(title: String, content: String)

  case class UpdatePayload(title: String, content: String)

  case class SetAssigneesPayload(userIds: Seq[Int])
}

class IssuesController(val workspacesActor: ActorRef[WorkspacesActor.Command],
                       val issuesActor: ActorRef[IssuesActor.Command])(implicit system: ActorSystem[_]) {

  import IssuesController.*

  implicit val timeout: Timeout = 5.seconds

  private def memberRouteUser(workspaceId: Int)(callback: User => Route): Route = Auth.userRoute(user => {
    val membersFuture: Future[Seq[User] | InternalError] = workspacesActor ? (ref => GetMembers(workspaceId, ref))
    val result = Await.result(membersFuture, timeout.duration)
    result match
      case error: InternalError => Response.status(StatusCodes.InternalServerError)
      case members: Seq[User] if !members.exists(_.id == user.id) => Response.status(StatusCodes.Forbidden)
      case _: Seq[User] => callback(user)
  })

  private def memberRoute(workspaceId: Int)(callback: => Route): Route = memberRouteUser(workspaceId)(_ => callback)

  def getIssues(workspaceId: Int): Route = memberRoute(workspaceId: Int) {
    val future: Future[Seq[GetIssuesResponse]] = issuesActor ? (ref => GetIssuesByWorkspaceId(workspaceId, ref))

    onSuccess(future) {
      issues => Response.json(issues)
    }
  }

  def getSingleIssue(workspaceId: Int, issueId: Int): Route = memberRoute(workspaceId: Int) {
    val future: Future[Option[GetSingleIssueResponse]] = issuesActor ? (ref => GetById(issueId, ref))

    onSuccess(future) {
      case None => Response.status(StatusCodes.BadRequest)
      case issue => Response.json(issue)
    }
  }

  def createIssue(workspaceId: Int, payload: CreatePayload): Route = memberRouteUser(workspaceId)(user => {
    val data = IssuesActor.CreatePayload(payload.title, payload.content, user.id, workspaceId)
    val future: Future[Issue] = issuesActor ? (ref => CreateIssue(data, ref))

    onSuccess(future)(Response.json(_))
  })

  def updateIssue(workspaceId: Int, issueId: Int, payload: UpdatePayload): Route = memberRoute(workspaceId) {
    val data = IssueData(payload.title, payload.content)
    val future: Future[Option[Issue]] = issuesActor ? (ref => UpdateIssue(issueId, data, ref))
    onSuccess(future) {
      case None => Response.status(StatusCodes.BadRequest)
      case issue => Response.json(issue)
    }
  }

  def deleteIssue(workspaceId: Int, issueId: Int): Route = memberRoute(workspaceId) {
    val future: Future[IssueDeleted] = issuesActor ? (ref => DeleteIssue(issueId, ref))
    onSuccess(future)(_ => Response.status(StatusCodes.OK))
  }

  def setAssignees(workspaceId: Int, issueId: Int, payload: SetAssigneesPayload): Route = memberRoute(workspaceId) {
    val future: Future[AssigneesSetSuccessfully] = issuesActor ? (ref => SetAssignees(issueId, payload, ref))
    onSuccess(future)(_ => Response.status(StatusCodes.OK))
  }
}
