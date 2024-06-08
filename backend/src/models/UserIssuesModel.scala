package models

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

