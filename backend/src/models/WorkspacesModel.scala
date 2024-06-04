package models

import slick.jdbc.SQLiteProfile.api.*
import slick.lifted.ProvenShape
import upickle.default.*

import java.time.LocalDateTime

class Workspace(val id: Int, val name: String, val ownerId: Int, val modifiedAt: LocalDateTime, val createdAt: LocalDateTime)

object Workspace {
  implicit val rw: ReadWriter[Workspace] = readwriter[ujson.Value].bimap[Workspace](
    workspace => ujson.Obj(
      "id" -> workspace.id,
      "name" -> workspace.name,
      "ownerId" -> workspace.ownerId,
      "modifiedAt" -> workspace.modifiedAt.toString,
      "createdAt" -> workspace.createdAt.toString
    ),
    json => new Workspace(
      json("id").num.toInt,
      json("name").str,
      json("ownerId").num.toInt,
      LocalDateTime.parse(json("modifiedAt").str),
      LocalDateTime.parse(json("createdAt").str)
    )
  )

  def apply(id: Int,
            name: String,
            ownerId: Int,
            modifiedAt: LocalDateTime,
            createdAt: LocalDateTime): Workspace =
    new Workspace(id, name, ownerId, modifiedAt, createdAt)

  def unapply(workspace: Workspace): Option[(Int, String, Int, LocalDateTime, LocalDateTime)] =
    Some((
      workspace.id,
      workspace.name,
      workspace.ownerId,
      workspace.modifiedAt,
      workspace.createdAt
    ))
}

class Workspaces(tag: Tag) extends Table[Workspace](tag, "Workspaces") {
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def ownerId = column[Int]("ownerId")

  def owner = foreignKey("ownerFk", ownerId, TableQuery[Users])(_.id)

  def modifiedAt = column[LocalDateTime]("modifiedAt")

  def createdAt = column[LocalDateTime]("createdAt")

  def * : ProvenShape[Workspace] = (id, name, ownerId, modifiedAt, createdAt) <> (Workspace.apply, Workspace.unapply)
}
