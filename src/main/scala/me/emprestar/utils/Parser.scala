package me.emprestar.utils

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions
import scala.util.{Try,Success,Failure}

object Parser :
  
  def crudeParse( str: String ) : Either[ String, JsonValue ] =
    Try( Json.parse( str ) ) match
      case Success( jv ) => Right( jv )
      case Failure( e ) => Left( "Parsing Error!!")

  def convertParse[T]( json: JsonValue )( using JsonInput[T] ) : Either[String,T] = 
    Try( json.as[T] ) match
      case Success( t ) => Right( t )
      case Failure( e ) => Left( s"could not convert parsed object!! ( ${ e.toString } )" )
  
  def parse[T]( str: String )( using JsonInput[T]) : Either[String,T] = crudeParse( str ).flatMap( convertParse )

