package routes.workspaces

import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import spray.json.{DefaultJsonProtocol, RootJsonFormat}
import controllers.workspaces.{createWorkspace, deleteWorkspace, getWorkspace, updateWorkspace}
import helpers.jwt.JWT

case class Workspace(id: Option[Int], name: Option[String], ownerId: Option[Int], updatedAt: Option[String], createdAt: Option[String])

class WorkspacesRoutes extends DefaultJsonProtocol with SprayJsonSupport:
  implicit val workspaceFormat: RootJsonFormat[Workspace] = jsonFormat5(Workspace.apply)

  def use: Route =
    pathPrefix("workspaces") {
      cookie("user"): userCookie =>
        JWT.decode(userCookie.value)
        concat(
          pathEnd:
            post:
              entity(as[Workspace]):
                createWorkspace,
          path(IntNumber): id =>
            concat(
              get:
                getWorkspace(id),
              put:
                entity(as[Workspace]): payload =>
                  updateWorkspace(id, payload),
              delete:
                deleteWorkspace(id)
            )
        )
      }