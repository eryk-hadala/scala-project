package models

import slick.jdbc.SQLiteProfile.api.*

import java.time.LocalDateTime

class UserWorkspaces(tag: Tag) extends Table[(Int, Int, LocalDateTime)](tag, "UserWorkspaces") {
  def userId = column[Int]("userId")

  def workspaceId = column[Int]("workspaceId")

  def pk = primaryKey("pkUserWorkspace", (userId, workspaceId))

  def user = foreignKey("userFk", userId, TableQuery[Users])(_.id)

  def workspace = foreignKey("workspaceFk", workspaceId, TableQuery[Workspaces])(_.id)

  def createdAt = column[LocalDateTime]("createdAt")

  def * = (userId, workspaceId, createdAt)
}
