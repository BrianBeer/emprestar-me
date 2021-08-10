
package me.emprestar.repos

import scala.collection.mutable.HashMap
import me.emprestar.models.Deposit
import me.emprestar.utils.IO

object DepositRepo extends Repo[Deposit] :

  def deposits : IO[ List[Deposit] ] =
    IO{
      data.toList.map( ( a , b ) => b )
    }
