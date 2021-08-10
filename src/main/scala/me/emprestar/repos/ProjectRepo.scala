
package me.emprestar.repos

import scala.collection.mutable.HashMap
import me.emprestar.models.Project
import me.emprestar.utils.IO

object ProjectRepo extends Repo[Project]

  // def create_new_project(id: Int, name: String, goal: Int ) : IO[Project] =
    // IO {
      // val p = Project( id, name , goal )
      // projects.addOne(( id , p  ))
      // p
    // }
//
  // def fetch_project( id: Int ) : IO[Option[Project]] =
    // IO {
      // projects.get( id )
    // }

  
