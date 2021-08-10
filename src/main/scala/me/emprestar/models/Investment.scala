
package me.emprestar.models

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions

case class Investment( id: Int, accountId: Int, projectId: Int,  amount: Int ) extends Model :
  def getId = id

  

  
  


