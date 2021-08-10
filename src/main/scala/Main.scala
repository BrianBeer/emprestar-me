import scala.language.implicitConversions

import scamper.Implicits.stringToEntity
import scamper.ResponseStatus.Registry.{ Ok , BadRequest }
import scamper.ResponseStatus.Registry.MethodNotAllowed
import scamper.server.HttpServer
import scamper.RequestMethod.Registry.{ Get, Post  }
import scamper.headers.Allow
import me.emprestar.controllers.ProjectController
import me.emprestar.controllers.AccountController
import scamper.{ BodyParser, HttpMessage, HttpResponse }
import me.emprestar.utils.IO
import scamper.server.Implicits.ServerHttpRequest

given BodyParser[String] = BodyParser.text(maxLength = 1024)

def runIOResp( ioeResp: IO[Either[String,String]] ) : HttpResponse  =
  ioeResp.run match
    case Left( error ) => BadRequest( error )
    case Right( value ) => Ok( value )

@main def hello: Unit = 
  val app = HttpServer.app()
  // configure
  app.backlogSize(50)
  app.poolSize(1) 
  app.queueSize(25)
  app.bufferSize(8192)
  app.readTimeout(3000)
  app.headerLimit(100)
  app.keepAlive(5, 10)

  // Project 
  app.post("/create-project") { req =>
    val body : String = req.as[String]
    val eioeProduct : IO[Either[String,String]] = ProjectController.create_new_project( body )
    runIOResp( eioeProduct )
  }

  app.get("/project/:id") { req =>
    val id = req.params.getInt("id")
    val ioeProduct : IO[ Either[String,String] ] = ProjectController.fetch_project(id)
    runIOResp( ioeProduct )
  }

  app.post("/project/:id/invest") { req =>
    val project_id = req.params.getInt("id")
    val body : String = req.as[String]
    val ioeProduct : IO[Either[String,String]] = ProjectController.invest( project_id, body )
    runIOResp( ioeProduct )
  }

  // Account 
  app.post("/create-account") { req => 
   val ioeAccount : IO[Either[String,String]] = AccountController.create_new_account()
   runIOResp( ioeAccount )
  }

  app.post("/account/:account_id/deposit") { req =>
   val account_id = req.params.getInt("account_id")
   val body : String = req.as[String]
   val ioeAccount : IO[Either[String,String]] = AccountController.deposit(account_id,body)
   runIOResp( ioeAccount )
  }

  val server = app.create( 8080 )
