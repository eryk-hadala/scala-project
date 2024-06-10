package app

import actors.{AuthActor, IssuesActor, UsersActor, WorkspacesActor}
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.http.scaladsl.server.Route
import helpers.Cors
import routes.{AuthRoutes, UsersRoutes, WorkspacesRoutes}

import scala.io.StdIn

def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
  import system.executionContext

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

@main
def serve(): Unit =
  val rootBehavior = Behaviors.setup[Nothing] { context =>
    implicit val system: ActorSystem[Nothing] = context.system

    val usersActor = context.spawn(UsersActor(), "UsersActor")
    val authActor = context.spawn(AuthActor(usersActor), "AuthActor")
    val workspacesActor = context.spawn(WorkspacesActor(usersActor), "WorkspacesActor")
    val issuesActor = context.spawn(IssuesActor(usersActor), "IssuesActor")

    context.watch(usersActor)
    context.watch(authActor)
    context.watch(workspacesActor)
    context.watch(issuesActor)


    val routes = Cors.corsHandler {
      pathPrefix("v1"):
        concat(
          new AuthRoutes(authActor, usersActor).routes,
          new WorkspacesRoutes(workspacesActor, issuesActor).routes,
          new UsersRoutes(usersActor).routes,
        )
    }

    startHttpServer(routes)(context.system)

    Behaviors.empty
  }
  val system = ActorSystem[Nothing](rootBehavior, "web-server")
