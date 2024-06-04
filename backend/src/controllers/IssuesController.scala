package controllers

import actors.WorkspacesActor
import actors.WorkspacesActor.{GetMembers, InternalError}
import akka.actor.typed.scaladsl.AskPattern.{Askable, schedulerFromActorSystem}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import helpers.{Auth, Response}
import models.*
import upickle.default.*

import java.time.LocalDateTime
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

object IssuesController {
  case class CreatePayload(title: String, content: String)

  case class UpdatePayload(title: String, content: String)

  case class SetAssigneesPayload(userIds: Seq[Int])

  case class GetIssuesResponse(id: Int, title: String, modifiedAt: String, createdAt: String, assignees: Seq[User])derives ReadWriter

  case class GetSingleIssueResponse(id: Int, owner: User, title: String, content: String, modifiedAt: String, createdAt: String, assignees: Seq[User])derives ReadWriter
}

class IssuesController(val workspacesActor: ActorRef[WorkspacesActor.Command])
                      (implicit system: ActorSystem[_]) {

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
    val issues = IssuesModel.getByWorkspaceId(workspaceId)
    Response.json(issues.map(issue => GetIssuesResponse(issue.id, issue.title, issue.modifiedAt.toString, issue.createdAt.toString, UserIssuesModel.getIssueUsers(issue.id))))
  }

  def getSingleIssue(workspaceId: Int, id: Int): Route = memberRoute(workspaceId: Int) {
    val issue = IssuesModel.getById(id)
    Response.json(GetSingleIssueResponse(issue.id, UsersModel.getById(issue.ownerId), issue.title, issue.content, issue.modifiedAt.toString, issue.createdAt.toString, UserIssuesModel.getIssueUsers(issue.id)))
  }

  def createIssue(workspaceId: Int, payload: CreatePayload): Route = memberRouteUser(workspaceId)(user => {
    val currentDateTime = LocalDateTime.now()
    val issue = IssuesModel.insert(Issue(0, user.id, workspaceId, payload.title, payload.content, currentDateTime, currentDateTime))
    Response.json(issue)
  })

  def updateIssue(workspaceId: Int, issueId: Int, payload: UpdatePayload): Route = memberRoute(workspaceId) {
    val issue = IssuesModel.update(issueId, payload.title, payload.content)
    Response.json(issue)
  }

  def deleteIssue(workspaceId: Int, issueId: Int): Route = memberRoute(workspaceId) {
    val issue = IssuesModel.delete(issueId)
    Response.json(issue)
  }

  def setAssignees(workspaceId: Int, issueId: Int, payload: SetAssigneesPayload): Route = memberRoute(workspaceId) {
    val assigned = UserIssuesModel.setAssignees(issueId, payload.userIds)
    if !assigned then return Response.json(StatusCodes.BadRequest, "Bad Request")
    Response.json("success")
  }
}
