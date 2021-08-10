package me.emprestar.models

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions

case class Deposit( id: Int, accountId: Int, amount: Int ) extends Model :
  def getId = id

  

  






