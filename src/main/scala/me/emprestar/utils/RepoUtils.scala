package me.emprestar.utils


import me.emprestar.utils.IO

object RepoUtils :
    
  def random_id : IO[Integer] =
    IO {
      val r = scala.util.Random
      r.nextInt( 100000000 )
    }
