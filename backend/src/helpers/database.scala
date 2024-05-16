package helpers.database

import java.sql._

def initializeDatabase(): Unit = {
//  try {
//    executeQuery("CREATE TABLE users (id INTEGER PRIMARY KEY, username VARCHAR(255), avatarUrl VARCHAR(255), updatedAt DATETIME, createdAt DATETIME)")
//    executeQuery("CREATE TABLE workspaces (id INTEGER PRIMARY KEY, name VARCHAR(255) ownerId INTEGER, updatedAt DATETIME, createdAt DATETIME, FOREIGN KEY(ownerId) REFERENCES users(id))")
//    executeQuery("CREATE TABLE statuses (id INTEGER PRIMARY KEY, name VARCHAR(255))")
//    executeQuery("CREATE TABLE assignments (id INTEGER PRIMARY KEY, statusId INTEGER, workspaceId INTEGER, title VARCHAR(255) content TEXT, updatedAt DATETIME, createdAt DATETIME, FOREIGN KEY(statusId) REFERENCES statuses(id), FOREIGN KEY(workspaceId) REFERENCES workspaces(id))")
//    executeQuery("CREATE TABLE assignmentUsers (userId INTEGER, assignmentId INTEGER, PRIMARY KEY(userId, assignmentId))")
//  }catch
//    case e: Exception => println(e)
}

object Query:
  Class.forName("org.sqlite.JDBC")
  private val connection = DriverManager.getConnection("jdbc:sqlite:database.db")

  def get(sql: String): ResultSet =
    val statement = connection.createStatement()
    statement.executeQuery(sql)
    
  def execute(sql: String): Unit =
    val statement = connection.createStatement()
    statement.execute(sql)
    statement.close()

