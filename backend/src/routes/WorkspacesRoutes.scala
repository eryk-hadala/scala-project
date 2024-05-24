package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatchers.IntNumber
import controllers.{AuthController, IssuesController, WorkspacesController}
import helpers.{JsonSupport, Response}
import models.UserWorkspacesModel

object WorkspacesRoutes extends JsonSupport {
  val routes: Route =
    pathPrefix("workspaces") {
      concat(
        pathEndOrSingleSlash:
          concat(
            get:
              WorkspacesController.getWorkspaces,
            post:
              entity(as[WorkspacesController.CreatePayload]):
                WorkspacesController.createWorkspace,
          ),
        pathPrefix(IntNumber): workspaceId =>
          AuthController.authenticate(user => {
              if !UserWorkspacesModel.isUserMember(user.id, workspaceId) then Response.json(StatusCodes.Unauthorized, "Unauthorized")
              else concat(
                path("members") {
                  concat(
                    get:
                      WorkspacesController.getMembers(workspaceId),
                    post:
                      entity(as[WorkspacesController.InviteUserPayload]): payload =>
                        WorkspacesController.inviteUser(workspaceId, payload)
                  )
                },
                pathPrefix("issues") {
                  concat(
                    pathEndOrSingleSlash:
                      concat(
                        get:
                          IssuesController.getIssues(workspaceId),
                        post:
                          entity(as[IssuesController.CreatePayload]): payload =>
                            IssuesController.createIssue(workspaceId, payload)
                      ),
                    pathPrefix(IntNumber): issueId =>
                      concat(
                        get:
                          IssuesController.getSingleIssue(issueId),
                        put:
                          entity(as[IssuesController.UpdatePayload]): payload =>
                            IssuesController.updateIssue(issueId, payload),
                        delete:
                          IssuesController.deleteIssue(issueId),
                        path("assignees"):
                          post:
                            entity(as[IssuesController.SetAssigneesPayload]): payload =>
                              IssuesController.setAssignees(issueId, payload)
                      )
                  )
                },
                pathEndOrSingleSlash {
                  concat(
                    get:
                      WorkspacesController.getWorkspace(workspaceId),
                    put:
                      entity(as[WorkspacesController.UpdatePayload]): payload =>
                        WorkspacesController.updateWorkspace(workspaceId, payload),
                    delete:
                      WorkspacesController.deleteWorkspace(workspaceId)
                  )
                }
              )
            }
          )
      )
    }
}
