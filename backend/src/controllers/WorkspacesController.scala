package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import helpers.Response
import models.{UserWorkspacesModel, Workspace, WorkspacesModel}

import java.time.LocalDateTime

object WorkspacesController {
  case class CreatePayload(override val name: String) extends Workspace(0, name, 0, LocalDateTime.now(), LocalDateTime.now())
  case class UpdatePayload(name: String)
  case class InviteUserPayload(userId: Int)

  def getWorkspaces: Route = AuthController.authenticate(user => {
    val workspaces = UserWorkspacesModel.getUserWorkspaces(user.id)
    Response.json(workspaces)
  })
  
  def createWorkspace(payload: CreatePayload): Route = AuthController.authenticate(user => {
    val workspace = WorkspacesModel.insert(Workspace(payload.id, payload.name, user.id, payload.modifiedAt, payload.createdAt))
    UserWorkspacesModel.addMember(user.id, workspace.id)
    Response.json(workspace)
  })

  def inviteUser(workspaceId: Int, payload: InviteUserPayload): Route = AuthController.authenticate(user => {
    val workspace = WorkspacesModel.getById(workspaceId)
    if workspace.ownerId != user.id then return Response.json(StatusCodes.Unauthorized, "Unauthorized")
    UserWorkspacesModel.addMember(payload.userId, workspaceId)
    Response.json("success")
  })

  def getWorkspace(id: Int): Route = {
    val workspace = WorkspacesModel.getById(id)
    Response.json(workspace)
  }

  def getMembers(id: Int): Route = {
    val users = UserWorkspacesModel.getWorkspaceUsers(id)
    Response.json(users)
  }

  def updateWorkspace(id: Int, payload: UpdatePayload): Route = AuthController.authenticate(user => {
    val oldWorkspace = WorkspacesModel.getById(id)
    if oldWorkspace.ownerId != user.id then return Response.json(StatusCodes.Unauthorized, "Unauthorized")
    val workspace = WorkspacesModel.update(id, payload.name)
    Response.json(workspace)
  })

  def deleteWorkspace(id: Int): Route = AuthController.authenticate(user => {
    val workspace = WorkspacesModel.getById(id)
    if workspace.ownerId != user.id then return Response.json(StatusCodes.Unauthorized, "Unauthorized")
    WorkspacesModel.delete(id)
    Response.json(workspace)
  })
}

