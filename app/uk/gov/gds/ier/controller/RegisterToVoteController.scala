package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.mustache.{InheritedGovukMustache, GovukMustache}
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.transaction._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import com.google.inject.Singleton

@Singleton
class RegisterToVoteController @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets : RemoteAssets)
  extends Controller
  with WithSerialiser
  with WithConfig
  with WithRemoteAssets
  with Logging
  with SessionCleaner
  with WithEncryption
  with GovukMustache
  with InheritedGovukMustache {

  def templateTest = Action { implicit request =>
    Ok(TestPage())
  }

  def redirectToOrdinary = Action {
    Redirect(config.ordinaryStartUrl)
  }

  def registerToVote = Action {
    Ok(RegisterToVote.OrdinaryStartPage())
  }

  def registerToVoteStart = NewSession in Action {
    request =>
      Redirect(country.routes.CountryStep.get.url, request.queryString)
  }

  def registerToVoteOverseasStart = NewSession in Action {
    request =>
      Redirect(overseas.dateOfBirth.routes.DateOfBirthStep.get.url, request.queryString)
  }

  def registerToVoteForces = Action {
    Ok(RegisterToVote.ForcesStartPage())
  }

  def registerToVoteForcesStart = NewSession in Action {
    request =>
      Redirect(forces.statement.routes.StatementStep.get.url, request.queryString)
  }

  def registerToVoteCrown = Action {
    Ok(RegisterToVote.CrownStartPage())
  }

  def registerToVoteCrownStart = NewSession in Action {
    request =>
      Redirect(crown.statement.routes.StatementStep.get.url, request.queryString)
  }

  def privacy = Action { request =>
    Ok(RegisterToVote.PrivacyPage())
  }

  def cookies = Action { request =>
    Ok(RegisterToVote.CookiePage())
  }
}

