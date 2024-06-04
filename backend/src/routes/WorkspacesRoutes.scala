package routes

import actors.WorkspacesActor
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import controllers.{IssuesController, WorkspacesController}
import helpers.{Auth, JsonSupport, Response}
import models.UserWorkspacesModel

class WorkspacesRoutes(val workspacesActor: ActorRef[WorkspacesActor.Command])
                      (implicit system: ActorSystem[_]) extends JsonSupport {
  private val controller = new WorkspacesController(workspacesActor)

  def routes: Route =
    pathPrefix("workspaces") {
      concat(
        pathEndOrSingleSlash:
          concat(
            get:
              controller.getWorkspaces,
            post:
              entity(as[WorkspacesController.CreatePayload]):
                controller.createWorkspace,
          ),
        pathPrefix(IntNumber): workspaceId =>
          concat(
            pathEndOrSingleSlash {
              concat(
                get:
                  controller.getWorkspace(workspaceId),
                put:
                  entity(as[WorkspacesController.UpdatePayload]): payload =>
                    controller.updateWorkspace(workspaceId, payload),
                delete:
                  controller.deleteWorkspace(workspaceId)
              )
            },
            path("members") {
              concat(
                get:
                  controller.getMembers(workspaceId),
                post:
                  entity(as[WorkspacesController.InviteUserPayload]): payload =>
                    controller.inviteUser(workspaceId, payload)
              )
            },
            Auth.userRoute(user => {
              if !UserWorkspacesModel.isUserMember(user.id, workspaceId) then Response.json(StatusCodes.Unauthorized, "Unauthorized")
              else concat(
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
                }
              )
            }
            )
          )
      )
    }
}
