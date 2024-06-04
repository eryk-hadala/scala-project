package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import helpers.{Auth, Response}
import models.{Issue, IssuesModel, User, UserIssuesModel, UsersModel}
import upickle.default.*

import java.time.LocalDateTime


object IssuesController {
  case class CreatePayload(title: String, content: String)

  case class UpdatePayload(title: String, content: String)

  case class SetAssigneesPayload(userIds: Seq[Int])

  case class GetIssuesResponse(id: Int, title: String, modifiedAt: String, createdAt: String, assignees: Seq[User])derives ReadWriter

  case class GetSingleIssueResponse(id: Int, owner: User, title: String, content: String, modifiedAt: String, createdAt: String, assignees: Seq[User])derives ReadWriter

  def getIssues(workspaceId: Int): Route = {
    val issues = IssuesModel.getByWorkspaceId(workspaceId)
    Response.json(issues.map(issue => GetIssuesResponse(issue.id, issue.title, issue.modifiedAt.toString, issue.createdAt.toString, UserIssuesModel.getIssueUsers(issue.id))))
  }

  def getSingleIssue(id: Int): Route = {
    val issue = IssuesModel.getById(id)
    Response.json(GetSingleIssueResponse(issue.id, UsersModel.getById(issue.ownerId), issue.title, issue.content, issue.modifiedAt.toString, issue.createdAt.toString, UserIssuesModel.getIssueUsers(issue.id)))
  }

  def createIssue(workspaceId: Int, payload: CreatePayload): Route = Auth.userRoute(user => {
    val currentDateTime = LocalDateTime.now()
    val issue = IssuesModel.insert(Issue(0, user.id, workspaceId, payload.title, payload.content, currentDateTime, currentDateTime))
    Response.json(issue)
  })

  def updateIssue(id: Int, payload: UpdatePayload): Route = {
    val issue = IssuesModel.update(id, payload.title, payload.content)
    Response.json(issue)
  }

  def deleteIssue(id: Int): Route = {
    val issue = IssuesModel.delete(id)
    Response.json(issue)
  }

  def setAssignees(issueId: Int, payload: SetAssigneesPayload): Route = {
    val assigned = UserIssuesModel.setAssignees(issueId, payload.userIds)
    if !assigned then return Response.json(StatusCodes.BadRequest, "Bad Request")
    Response.json("success")
  }
}
