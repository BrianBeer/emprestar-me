package me.emprestar.repos

import scala.collection.mutable.HashMap
import me.emprestar.models.Model
import me.emprestar.utils.IO

trait Repo[T <: Model ] :

  var data : HashMap[ Int, T ] = HashMap.empty[Int,T]

  def repoData : HashMap[ Int, T ] = data 

  def save( obj: T ) : IO[ Either[String,T] ] =
    IO {
      val id = obj.getId
      data.get( id ) match 
        case Some(_) => 
          Left( "Can not save object because it already exists") 
        case None => 
          data.addOne(( id, obj ))
          Right( obj )
    }
      
  def fetch( id: Int ) : IO[Option[T]] =
    IO {
      data.get( id )
    }

  def fetch_err( id: Int, errormsg: String ) : IO[Either[String,T]] =
    IO {
      data.get( id ) match 
        case None => Left( errormsg )
        case Some( v ) => Right( v )
    }
    
