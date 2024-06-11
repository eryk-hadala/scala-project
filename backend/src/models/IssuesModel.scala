package models

import slick.jdbc.SQLiteProfile.api.*
import slick.lifted.ProvenShape
import upickle.default.*

import java.time.LocalDateTime

class Issue(val id: Int, val ownerId: Int, val workspaceId: Int, val status: String, val label: String, val priority: String, val title: String, val content: String, val modifiedAt: LocalDateTime, val createdAt: LocalDateTime)

object Issue {
  implicit val rw: ReadWriter[Issue] = readwriter[ujson.Value].bimap[Issue](
    issue => ujson.Obj(
      "id" -> issue.id,
      "ownerId" -> issue.ownerId,
      "workspaceId" -> issue.workspaceId,
      "title" -> issue.title,
      "content" -> issue.content,
      "status" -> issue.status,
      "label" -> issue.label,
      "priority" -> issue.priority,
      "modifiedAt" -> issue.modifiedAt.toString,
      "createdAt" -> issue.createdAt.toString
    ),
    json => Issue(
      json("id").num.toInt,
      json("ownerId").num.toInt,
      json("workspaceId").num.toInt,
      json("status").str,
      json("label").str,
      json("priority").str,
      json("title").str,
      json("content").str,
      LocalDateTime.parse(json("modifiedAt").str),
      LocalDateTime.parse(json("createdAt").str)
    )
  )

  def apply(id: Int,
            ownerId: Int,
            workspaceId: Int,
            status: String,
            label: String,
            priority: String,
            title: String,
            content: String,
            modifiedAt: LocalDateTime,
            createdAt: LocalDateTime): Issue =
    new Issue(id, ownerId, workspaceId, status, label, priority, title, content, modifiedAt, createdAt)

  def unapply(issue: Issue): Option[(Int, Int, Int, String, String, String, String, String, LocalDateTime, LocalDateTime)] =
    Some((
      issue.id,
      issue.ownerId,
      issue.workspaceId,
      issue.status,
      issue.label,
      issue.priority,
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

  def status = column[String]("status")
  
  def label = column[String]("label")
  
  def priority = column[String]("priority")

  def workspace = foreignKey("workspaceFk", workspaceId, TableQuery[Workspaces])(_.id)

  def title = column[String]("title")

  def content = column[String]("content")

  def modifiedAt = column[LocalDateTime]("modifiedAt")

  def createdAt = column[LocalDateTime]("createdAt")

  def * : ProvenShape[Issue] = (id, ownerId, workspaceId, status, label, priority, title, content, modifiedAt, createdAt) <> (Issue.apply, Issue.unapply)
}
