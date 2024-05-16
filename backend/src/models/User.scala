package models

import helpers.traits.Model

class User(var id: Int = 0, var username: String = "", var avatarSrc: String = "") extends Model {
  def this(username: String, avatarSrc: String) = this(0, username, avatarSrc)
  def this(id: Int) = {
    this(id, "", "")
    username = "username"
  }

  def get(): Unit = {

  }

  def save(): Unit = {
    println("set")
  }
}
