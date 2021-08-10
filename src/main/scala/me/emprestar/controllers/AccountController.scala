package me.emprestar.controllers

import little.json.*
import little.json.Implicits.{ *, given }
import scala.language.implicitConversions
import me.emprestar.utils.Writer
import me.emprestar.utils.RepoUtils
import me.emprestar.repos.AccountRepo
import me.emprestar.models.Account
import me.emprestar.models.Deposit
import me.emprestar.repos.DepositRepo
import me.emprestar.models.Investment
import me.emprestar.repos.InvestmentRepo
import me.emprestar.utils.IO
import me.emprestar.utils.IO.unit

object AccountController : 
  
  import me.emprestar.models.Account.given
  import me.emprestar.utils.Parser
  import dto.given
  import dto.Amount 
  import dto.AccountDTO 

  def create_new_account() : IO[ Either[String,String] ]  =
    for {
      id <- RepoUtils.random_id 
      account = Account( id ) 
      a <- AccountRepo.save( account );
      s = a.map( Writer.write( _ ) )
    } yield s

  def deposit(account_id: Int, body: String ) : IO[ Either[ String , String ] ] =
    for {
      dep <- deposit_account( account_id, body )
      e <- dep match
        case Left( error ) => IO{ Left( error ) }
        case Right( _ ) => fetch_account( account_id )
    } yield e

  def deposit_account(account_id: Int, body: String ) : IO[ Either[ String , Deposit ] ]   =
    for {
      either_deposit <- create_deposit( account_id, body )
      inserted <- either_deposit match
        case Left( error ) => IO{ Left( error ) }
        case Right( deposit ) => insert_deposit( deposit )
    } yield inserted

  def create_deposit(account_id: Int, body: String ) : IO[ Either[ String , Deposit ] ]   =
    val eamount : Either[String,Amount] = Parser.parse[Amount]( body )
    eamount match
      case Left( error ) => IO{ Left( error ) }
      case Right( amount ) =>
        for {
          ei_account <- AccountRepo.fetch_err( account_id , "Could not find Account" )
          id <- RepoUtils.random_id
          ei_deposit = ei_account.map( a => Deposit( id, a.getId, amount.amount ) )
        } yield ei_deposit

  def insert_deposit( deposit: Deposit ) : IO[ Either[ String, Deposit] ] =
    DepositRepo.save( deposit )

  def fetch_account( account_id: Int ) : IO[ Either[ String, String ] ] =
    for {
      ei_account <- AccountRepo.fetch_err( account_id , "Could not find Account" )
      deposits <- DepositRepo.deposits 
      investments <- InvestmentRepo.investments
      i = account_investments( account_id, investments )
      d = account_deposits( account_id, deposits )
      a = d - i
      s = ei_account.map( acc =>
        val dto = AccountDTO( acc.id, a , i )
        Writer.write( dto )
      )
    } yield s

  def account_investments( account_id: Int, invs : List[Investment] ) : Int =
    invs.filter( i => i.accountId == account_id ).foldLeft( 0 )( ( akk, i ) => akk + i.amount )

  def account_deposits( account_id: Int, deps : List[Deposit] ) : Int =
    deps.filter( i => i.accountId == account_id ).foldLeft( 0 )( ( akk, i ) => akk + i.amount )

  object dto :
    case class Amount( amount: Int )

    given jsonToAmount: JsonInput[Amount] with
      def apply(json: JsonValue) = Amount( json("amount") )
    

    case class AccountDTO( id: Int, available: Int, invested: Int )

    given accountDTO2Json: JsonOutput[AccountDTO] with
      def apply(a: AccountDTO) =
        Json.obj("id" -> a.id, "available" -> a.available, "invested" -> a.invested )
