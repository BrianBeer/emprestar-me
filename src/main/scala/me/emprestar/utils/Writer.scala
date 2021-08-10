package me.emprestar.utils

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions
import scala.util.{Try,Success,Failure}

object Writer :
  
  def write[T]( obj: T )( using JsonOutput[T]) : String = 
    Json.toJson(obj).toString

  def write_s[T]( obj: T )( using JsonOutput[T]) : Either[String,String] = 
    Try( Json.toJson(obj).toString ) match
      case Success( t ) => Right( t )
      case Failure( e ) => Left( s"could not write object to json!! ( ${ e.toString } )" )

  def toJson[T]( obj: T )( using JsonOutput[T]) : JsonValue = 
    Json.toJson(obj)
