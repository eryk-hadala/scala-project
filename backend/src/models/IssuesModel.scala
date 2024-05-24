package models

import helpers.Database
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.ProvenShape
import upickle.default.*

import java.time.LocalDateTime

class Issue(val id: Int, val ownerId: Int, val workspaceId: Int, val title: String, val content: String, val modifiedAt: LocalDateTime, val createdAt: LocalDateTime)

object Issue {
  implicit val rw: ReadWriter[Issue] = readwriter[ujson.Value].bimap[Issue](
    issue => ujson.Obj(
      "id" -> issue.id,
      "ownerId" -> issue.ownerId,
      "workspaceId" -> issue.workspaceId,
      "title" -> issue.title,
      "content" -> issue.content,
      "modifiedAt" -> issue.modifiedAt.toString,
      "createdAt" -> issue.createdAt.toString
    ),
    json => Issue(
      json("id").num.toInt,
      json("ownerId").num.toInt,
      json("workspaceId").num.toInt,
      json("title").str,
      json("content").str,
      LocalDateTime.parse(json("modifiedAt").str),
      LocalDateTime.parse(json("createdAt").str)
    )
  )

  def apply(id: Int,
            ownerId: Int,
            workspaceId: Int,
            title: String,
            content: String,
            modifiedAt: LocalDateTime,
            createdAt: LocalDateTime): Issue =
    new Issue(id, ownerId, workspaceId, title, content, modifiedAt, createdAt)

  def unapply(issue: Issue): Option[(Int, Int, Int, String, String, LocalDateTime, LocalDateTime)] =
    Some((
      issue.id,
      issue.ownerId,
      issue.workspaceId,
      issue.title,
      issue.content,
      issue.modifiedAt,
      issue.createdAt
    ))
}

class Issues(tag: Tag) extends Table[Issue](tag, "Issues") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def ownerId = column[Int]("ownerId")

  def owner = foreignKey("ownerFk", ownerId, TableQuery[Users])(_.id)

  def workspaceId = column[Int]("workspaceId")

  def workspace = foreignKey("workspaceFk", workspaceId, TableQuery[Workspaces])(_.id)

  def title = column[String]("title")

  def content = column[String]("content")

  def modifiedAt = column[LocalDateTime]("modifiedAt")

  def createdAt = column[LocalDateTime]("createdAt")

  def * : ProvenShape[Issue] = (id, ownerId, workspaceId, title, content, modifiedAt, createdAt) <> (Issue.apply, Issue.unapply)
}

object IssuesModel {
  private val issues = TableQuery[Issues]
  private val users = TableQuery[Users]
  private val userIssues = TableQuery[UserIssues]

  def getById(id: Int): Issue = {
    val query = issues.filter(_.id === id).result.headOption
    val result: Option[Issue] = Database.exec(query)
    result.get
  }

  def getByWorkspaceId(workspaceId: Int): Seq[Issue] = {
    val query = issues.filter(_.workspaceId === workspaceId).result
    val result = Database.exec(query)
    result
  }
  
  def insert(issue: Issue): Issue = {
    val query = (issues returning issues.map(_.id)) += issue
    val result: Int = Database.exec(query)
    getById(result)
  }

  def update(id: Int, title: String, content: String): Issue = {
    val query = issues.filter(_.id === id)
      .map(oldUser => (oldUser.title, oldUser.content, oldUser.modifiedAt))
      .update((title, content, LocalDateTime.now()))
    Database.exec(query)
    getById(id)
  }

  def delete(id: Int): Issue = {
    val issue = getById(id)
    val query = issues.filter(_.id === id).delete
    Database.exec(query)
    issue
  }
}
