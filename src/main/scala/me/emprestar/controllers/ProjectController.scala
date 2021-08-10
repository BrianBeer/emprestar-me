package me.emprestar.controllers

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions
import me.emprestar.utils.Parser
import me.emprestar.utils.Writer
import me.emprestar.utils.RepoUtils
import me.emprestar.repos.ProjectRepo
import me.emprestar.models.Project
import me.emprestar.repos.AccountRepo
import me.emprestar.models.Account
import me.emprestar.repos.InvestmentRepo
import me.emprestar.models.Investment
import me.emprestar.repos.DepositRepo
import me.emprestar.models.Deposit
import me.emprestar.utils.IO
import me.emprestar.utils.IO.unit

object ProjectController : 
  
  import dto.given
  import dto.NewProject
  import dto.InvestProject
  import dto.ProjectDTO
  import me.emprestar.models.Project.given

  def create_new_project( strProject: String ) : IO[ Either[ String , String ] ] =
    val enp : Either[String,NewProject] = Parser.parse[NewProject]( strProject )
    enp match 
      case Left( error ) => IO{ Left( error ) }
      case Right( newProject ) => 
        for {
          id <- RepoUtils.random_id 
          project = Project( id, newProject.name, newProject.goal )
          p <- ProjectRepo.save( project )
          s = p.map( Writer.write( _ ) )
        } yield  s  

  def invest( project_id: Int, body: String ) : IO[ Either[ String , String ] ]  =
    for {
      inv <- invest_project( project_id, body )
      e <- inv match
        case Left( error ) => IO{ Left( error ) }
        case Right( _ ) => fetch_project( project_id )
    } yield e

  def invest_project(project_id: Int, body: String ) : IO[ Either[ String , Investment ] ]   =
    for {
      either_inv <- create_investment( project_id, body )
      inserted <- either_inv match
        case Left( error ) => IO{ Left( error ) }
        case Right( inv ) => insert_investment( inv )
    } yield inserted

  def create_investment(project_id: Int, body: String ) : IO[ Either[ String , Investment ] ]   =
    val einvest : Either[String,InvestProject] = Parser.parse[InvestProject]( body )
    einvest match
      case Left( error ) => IO{ Left( error ) }
      case Right( invest ) =>
        for { // IO
          ei_account <- AccountRepo.fetch_err( invest.accountId , "Could not find Account" )
          ei_project <- ProjectRepo.fetch_err( project_id , "Could not find Project" )
          id <- RepoUtils.random_id
          inv = for { // Either
            account <- ei_account
            project <- ei_project
          } yield Investment( id , account.id, project.id, invest.amount ) 

          deposits <- DepositRepo.deposits 
          investments <- InvestmentRepo.investments
          
          val_inv = for { // Either
            account <- ei_account
            project <- ei_project
            validated_inv <- inv.flatMap( i => validate_investment( i , account, project,  deposits , investments ) )
          } yield validated_inv

        } yield val_inv 

  def validate_investment( investment: Investment, account: Account, project: Project, deposits: List[Deposit], investments: List[Investment]  ) : Either[ String, Investment ] =
    val deposited = account_deposits( account.id , deposits )
    val invested = account_investments( account.id, investments )
    val wished = investment.amount
    if deposited < invested + wished then
      Left( s"Sorry, You do not have enough funds for this investment (you can max invest ${deposited - invested})!" )
    else
      Right( investment )

  def account_investments( account_id: Int, invs : List[Investment] ) : Int =
    invs.filter( i => i.accountId == account_id ).foldLeft( 0 )( ( akk, i ) => akk + i.amount )

  def account_deposits( account_id: Int, deps : List[Deposit] ) : Int =
    deps.filter( i => i.accountId == account_id ).foldLeft( 0 )( ( akk, i ) => akk + i.amount )

  def insert_investment( investment: Investment ) : IO[ Either[ String, Investment] ] =
    InvestmentRepo.save( investment )


  def fetch_project( project_id: Int ) : IO[ Either[ String, String ] ] =
    for {
      ei_project <- ProjectRepo.fetch_err( project_id , "Could not find Project" )
      investments <- InvestmentRepo.investments
      i = project_investments( project_id, investments )
      s = ei_project.map( proj  =>
        val dto = ProjectDTO( proj.id, proj.name, proj.goal , i )
        Writer.write( dto )
      )
    } yield s

  def project_investments( project_id: Int, invs : List[Investment] ) : Int =
    invs.filter( i => i.projectId == project_id ).foldLeft( 0 )( ( akk, i ) => akk + i.amount )


  object dto :
    case class NewProject( name: String, goal: Int )

    given jsonToNewProject: JsonInput[NewProject] with
      def apply(json: JsonValue) = NewProject( json("name"), json("goal") )
   
    case class InvestProject( accountId: Int, amount: Int )

    given jsonToInvestProject: JsonInput[InvestProject] with
      def apply(json: JsonValue) = InvestProject( json("accountId"), json("amount") )
   
    case class ProjectDTO( id: Int, name: String, goal: Int, founded: Int )

    given projectDTO2Json: JsonOutput[ProjectDTO] with
      def apply(p: ProjectDTO) =
        Json.obj("id" -> p.id, "name" -> p.name, "goal" -> p.goal, "founded" -> p.founded )
