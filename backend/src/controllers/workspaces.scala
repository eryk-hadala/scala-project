package controllers.workspaces

import akka.http.scaladsl.model.*
import akka.http.scaladsl.server.Route
import routes.workspaces.Workspace
import helpers.database.Query
import helpers.response.Response

def createWorkspace(payload: Workspace): Route =
  val name = payload.name.get
  val ownerId = payload.ownerId.get

  Query.execute(s"INSERT INTO workspaces (name, ownerId, updatedAt, createdAt) VALUES ('$name', '$ownerId', DATETIME('now'), DATETIME('now'))")

  Response.json("""{"message": "Success"}""")

def getWorkspace(id: Int): Route =
  val result = Query.get(s"SELECT w.name name, w.updatedAt updatedAt, w.createdAt createdAt, u.id userId, u.username username, u.avatarUrl avatarUrl, u.updatedAt userUpdatedAt, u.createdAt userCreatedAt FROM workspaces w JOIN users u ON w.ownerId = u.id WHERE w.id = $id")
  if !result.next() then return Response.json(StatusCodes.NotFound, """{"message": "Not Found"}""")

  val name = result.getString("name")
  val updatedAt = result.getString("updatedAt")
  val createdAt = result.getString("createdAt")
  val userId = result.getInt("userId")
  val username = result.getString("username")
  val avatarUrl = result.getString("avatarUrl")
  val userUpdatedAt = result.getString("userUpdatedAt")
  val userCreatedAt = result.getString("userCreatedAt")

  result.close()

  Response.json(s"""{"id": $id, "name": "$name", "owner": {"id": $userId, "username": "$username", "avatar": {"src": "$avatarUrl", "alt": ""}, "updatedAt": "$userUpdatedAt", "createdAt": "$userCreatedAt"}, "updatedAt": "$updatedAt", "createdAt": "$createdAt" }""")

def updateWorkspace(id: Int, payload: Workspace): Route =
  val name = payload.name.get
  if name.trim.isEmpty then return Response.json(StatusCodes.BadRequest, """{"message": "Field name is empty!"}""")

  Query.execute(s"UPDATE workspaces SET name = '$name', updatedAt = DATETIME('now') WHERE id = $id")

  Response.json("""{"message": "Success"}""")

def deleteWorkspace(id: Int): Route =
  Query.execute(s"DELETE FROM workspaces WHERE id = $id")

  Response.json("""{"message": "Success"}""")