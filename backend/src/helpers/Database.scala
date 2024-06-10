package helpers

import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.SQLiteProfile.api.Database as SqliteDatabase

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.util.{Failure, Success}

object Database {
  private val db = SqliteDatabase.forConfig("slick.dbs.default")

  def exec[R](action: DBIOAction[R, NoStream, Nothing]) = {
    val result = db.run(action)

    result.onComplete {
      case Success(_) => println("Success")
      case Failure(ex) => println(s"Error fetching: ${ex.getMessage}")
    }

    Await.result(result, 10.seconds)
  }
}
