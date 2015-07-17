package uk.gov.gds.ier.config

import com.google.inject.Singleton
import uk.gov.gds.ier.logging.Logging

@Singleton
class Config extends Logging {
  private lazy val configuration = play.Play.application().configuration()

  def apiTimeout = configuration.getInt("api.timeout", 10).toInt
  def locateUrl = configuration.getString("locate.url")
  def locateAuthorityUrl = configuration.getString("locate.authority.url")
  def locateApiAuthorizationToken = configuration.getString("locate.api.authorization.token")
  def fakeIer = configuration.getBoolean("ier.fake")
  def fakeLocate = configuration.getBoolean("locate.fake")
  def ierApiUrl = configuration.getString("ier.api.url")
  def ierLocalAuthorityLookupUrl = configuration.getString("ier.authority.lookup.url")
  def ierLocalAuthorityPostcodeLookupUrl= configuration.getString("ier.postcode.lookup.url")
  def ierApiToken = configuration.getString("ier.api.token")
  def stripNino = configuration.getBoolean("ier.nino.strip", false)
  def sessionTimeout = configuration.getInt("session.timeout", 20).toInt

  def buildDate = configuration.getString("gds.BuildTime", "unknown")
  def buildNumber = configuration.getString("gds.BuildNumber", "unknown")
  def revision = configuration.getString("gds.GitCommit", "abcdef1234567890abcdef1234567890abcdef12")
  def branch = configuration.getString("gds.GitBranch", "unknown")

  def cookiesAesKey = configuration.getString("ier.cookies.aes.encryptionKey")
  def cookiesSecured = configuration.getBoolean("ier.cookies.secured", false)

  def assetsPath = configuration.getString("assets.path")

  def ordinaryStartUrl = configuration.getString("ier.start.ordinary")
  def crownStartUrl = configuration.getString("ier.start.crown")
  def forcesStartUrl = configuration.getString("ier.start.forces")

  def fakeFeedbackService = configuration.getBoolean("fakeFeedbackService")
  def zendeskUrl = configuration.getString("zendesk.url")
  def zendeskUsername = configuration.getString("zendesk.username", "")
  def zendeskPassword = configuration.getString("zendesk.password", "")

  def completeSurveyLink = configuration.getString("complete.survey.link")

  def availableForScotland = configuration.getBoolean("ier.availableForScotland", false)

  def logConfiguration() = {
    logger.debug(s"apiTimeout:$apiTimeout")
    logger.debug(s"locateUrl:$locateUrl")
    logger.debug(s"locateAuthorityUrl:$locateAuthorityUrl")
    logger.debug(s"locateApiAuthorizationToken:$locateApiAuthorizationToken")
    logger.debug(s"fakeIer:$fakeIer")
    logger.debug(s"ierApiUrl:$ierApiUrl")
    logger.debug(s"stripNino:$stripNino")
    logger.debug(s"buildDate:$buildDate")
    logger.debug(s"buildNumber:$buildNumber")
    logger.debug(s"revision:$revision")
    logger.debug(s"branch:$branch")
    logger.debug(s"cookiesSecured:$cookiesSecured")
    logger.debug(s"assetsPath:$assetsPath")
    logger.debug(s"zendeskUrl:$zendeskUrl")
  }
}
