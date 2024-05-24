package models

import helpers.Database
import slick.jdbc.SQLiteProfile.api.*

import java.time.LocalDateTime

class UserIssues(tag: Tag) extends Table[(Int, Int, LocalDateTime)](tag, "UserIssues") {
  def userId = column[Int]("userId")

  def issueId = column[Int]("issueId")

  def pk = primaryKey("pkUserIssue", (userId, issueId))

  def user = foreignKey("userFk", userId, TableQuery[Users])(_.id)

  def issue = foreignKey("issueFk", issueId, TableQuery[Issues])(_.id)

  def createdAt = column[LocalDateTime]("createdAt")

  def * = (userId, issueId, createdAt)
}

object UserIssuesModel {
  private val userIssues = TableQuery[UserIssues]
  private val users = TableQuery[Users]

  def getIssueUsers(issueId: Int): Seq[User] = {
    val query = (for {
      (ab, a) <- userIssues join users on (_.userId === _.id)
    } yield (a, ab.issueId)).filter(_._2 === issueId).map(_._1).result
    val result: Seq[User] = Database.exec(query)
    result.map(user => User(user.id, user.avatarUrl, user.username, user.email, "", user.modifiedAt, user.createdAt))
  }

  def setAssignees(issueId: Int, userIds: Seq[Int]): Boolean = {
    val currentDateTime: LocalDateTime = LocalDateTime.now()

    val deleteQuery = userIssues.filter(_.issueId === issueId).delete
    val insertQuery = userIssues ++= userIds.map(userId => (userId, issueId, currentDateTime))
    val result: Unit = Database.exec(DBIO.seq(deleteQuery, insertQuery).transactionally)
    
    true
  }
}
