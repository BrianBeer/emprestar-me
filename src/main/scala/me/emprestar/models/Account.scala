package me.emprestar.models

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions

case class Account( id: Int ) extends Model :
  def getId = id

object Account :

  given bla: JsonOutput[Account] with
    def apply(a: Account) =
      Json.obj("id" -> a.id )

  






