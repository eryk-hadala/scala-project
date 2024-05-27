package app

import scala.io.StdIn
import scala.concurrent.ExecutionContextExecutor
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.javadsl.settings.CorsSettings
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{concat, pathPrefix}
import akka.http.scaladsl.server.Directives.*
import helpers.Cors
//import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
//import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import routes.{AuthRoutes, WorkspacesRoutes}

@main
def serve(): Unit =
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "web-server")
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val routes = Cors.corsHandler {
    pathPrefix("v1"):
      concat(
        AuthRoutes.routes,
        WorkspacesRoutes.routes,
      )
  }
    
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes)

  println(s"Server now online. Please navigate to http://localhost:8080\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
