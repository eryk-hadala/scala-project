package actors

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, DispatcherSelector}
import akka.util.Timeout

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.*

object IssuesActor {
  sealed trait Command


  def apply(usersActor: ActorRef[UsersActor.Command])(implicit system: ActorSystem[_]): Behavior[Command] =
    implicit val timeout: Timeout = 3.seconds
    val selector = DispatcherSelector.fromConfig("blocking-dispatcher")
    implicit val executionContext: ExecutionContextExecutor = system.dispatchers.lookup(selector)

    Behaviors.receiveMessage { _ =>
      Behaviors.same
    }
}
