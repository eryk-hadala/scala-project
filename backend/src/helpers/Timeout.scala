package helpers

import akka.util.Timeout

import scala.concurrent.duration.*

object Timeout {
  implicit val timeout: Timeout = 3.seconds
}
