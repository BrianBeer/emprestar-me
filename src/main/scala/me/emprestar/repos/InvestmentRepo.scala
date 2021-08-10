
package me.emprestar.repos

import scala.collection.mutable.HashMap
import me.emprestar.models.Investment
import me.emprestar.utils.IO

object InvestmentRepo extends Repo[Investment] :

  def invested_in_project( projectId: Int ) : IO[ Either[String,Int] ] = 
    IO {
      val invested = data.foldLeft( 0 )( (akk, key_value)  => 
          var value = key_value._2 
          if value.projectId == projectId then
            akk + value.amount
          else
            akk
      )
      Right( invested )
    }

  def investments : IO[ List[Investment] ] =
    IO{
      data.toList.map( ( a , b ) => b )
    }
