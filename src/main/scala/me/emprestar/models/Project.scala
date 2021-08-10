package me.emprestar.models

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions

case class Project( id: Int, name: String, goal: Int ) extends Model :
  def getId = id

object Project :

  given bla: JsonOutput[Project] with
    def apply(p: Project) =
      Json.obj("id" -> p.id, "name" -> p.name, "goal" -> p.goal )

  






