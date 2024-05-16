package app

import scala.io.StdIn
import scala.concurrent.ExecutionContextExecutor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import models.User
import routes.workspaces.WorkspacesRoutes
import routes.authorization.AuthorizationRoutes

@main
def serve(): Unit =
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "web-server")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.executionContext
  
  val routes =
    pathPrefix("v1"):
      concat(
        WorkspacesRoutes().use,
        AuthorizationRoutes().use
      )
    
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
