package models

import helpers.Database
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

object UserWorkspacesModel {
  private val userWorkspaces = TableQuery[UserWorkspaces]
  private val workspaces = TableQuery[Workspaces]
  private val users = TableQuery[Users]

  def getUserWorkspaces(id: Int): Seq[Workspace] = {
    val query = (for {
      (ab, a) <- userWorkspaces join workspaces on (_.workspaceId === _.id)
    } yield (a, ab.userId)).filter(_._2 === id).map(_._1).result
    val result: Seq[Workspace] = Database.exec(query)
    result
  }
  
  def getWorkspaceUsers(id: Int): Seq[User] = {
    val query = (for {
      (ab, a) <- userWorkspaces join users on (_.userId === _.id)
    } yield (a, ab.workspaceId)).filter(_._2 === id).map(_._1).result
    val result: Seq[User] = Database.exec(query)
    result.map(user => User(user.id, user.avatarUrl, user.username, user.email, "", user.modifiedAt, user.createdAt))
  }
  
  def isUserMember(userId: Int, workspaceId: Int): Boolean = {
    val query = userWorkspaces.filter(row => row.userId === userId && row.workspaceId === workspaceId).result.headOption
    val result: Option[(Int, Int, LocalDateTime)] = Database.exec(query)
    result.isDefined
  }
  
  def addMember(userId: Int, workspaceId: Int): Boolean = {
    val query = userWorkspaces += (userId, workspaceId, LocalDateTime.now())
    val result = Database.exec(query)
    result > 0
  }
}
