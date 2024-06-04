package app

import actors.{AuthActor, UsersActor}

import scala.io.StdIn
import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import routes.AuthRoutes

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
    val usersActor = context.spawn(UsersActor(), "UsersActor")
    val authActor = context.spawn(AuthActor(usersActor)(context.system), "AuthActor")
    context.watch(usersActor)
    context.watch(authActor)

    //    val routes = AuthRoutes.routes
    //    startHttpServer(routes)(context.system)

    val routes = AuthRoutes.routes(authActor, usersActor, context.system) // Pass the usersActor to AuthRoutes
    startHttpServer(routes)(context.system)

    Behaviors.empty
  }
  val system = ActorSystem[Nothing](rootBehavior, "web-server")
//  implicit val scheduler: Scheduler = system.scheduler
