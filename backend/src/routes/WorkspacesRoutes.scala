package routes

import actors.{IssuesActor, WorkspacesActor}
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.PathMatchers.IntNumber
import akka.http.scaladsl.server.Route
import controllers.{IssuesController, WorkspacesController}
import helpers.JsonSupport

class WorkspacesRoutes(val workspacesActor: ActorRef[WorkspacesActor.Command],
                       val issuesActor: ActorRef[IssuesActor.Command])(implicit system: ActorSystem[_]) extends JsonSupport {
  private val workspacesController = new WorkspacesController(workspacesActor)
  private val issuesController = new IssuesController(workspacesActor, issuesActor)

  def routes: Route =
    pathPrefix("workspaces") {
      concat(
        pathEndOrSingleSlash:
          concat(
            get:
              workspacesController.getWorkspaces,
            post:
              entity(as[WorkspacesController.CreatePayload]):
                workspacesController.createWorkspace,
          ),
        pathPrefix(IntNumber): workspaceId =>
          concat(
            pathEndOrSingleSlash {
              concat(
                get:
                  workspacesController.getWorkspace(workspaceId),
                put:
                  entity(as[WorkspacesController.UpdatePayload]): payload =>
                    workspacesController.updateWorkspace(workspaceId, payload),
                delete:
                  workspacesController.deleteWorkspace(workspaceId)
              )
            },
            concat(
              path("members") {
                concat(
                  get:
                    workspacesController.getMembers(workspaceId),
                  post:
                    entity(as[WorkspacesController.InviteUserPayload]): payload =>
                      workspacesController.inviteUser(workspaceId, payload)
                )
              },
              pathPrefix("issues") {
                concat(
                  pathEndOrSingleSlash:
                    concat(
                      get:
                        issuesController.getIssues(workspaceId),
                      post:
                        entity(as[IssuesController.CreatePayload]): payload =>
                          issuesController.createIssue(workspaceId, payload)
                    ),
                  pathPrefix(IntNumber): issueId =>
                    concat(
                      get:
                        issuesController.getSingleIssue(workspaceId, issueId),
                      put:
                        entity(as[IssuesController.UpdatePayload]): payload =>
                          issuesController.updateIssue(workspaceId, issueId, payload),
                      delete:
                        issuesController.deleteIssue(workspaceId, issueId),
                      path("assignees"):
                        post:
                          entity(as[IssuesController.SetAssigneesPayload]): payload =>
                            issuesController.setAssignees(workspaceId, issueId, payload)
                    )
                )
              }
            )
          )
      )
    }
}
