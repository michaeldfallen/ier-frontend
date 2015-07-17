package uk.gov.gds.ier.assets

import play.api.mvc.{RequestHeader, Call}
import uk.gov.gds.ier.assets.routes.{Assets => PlayAssetRouter, Template => TemplateAssetRouter}
import uk.gov.gds.ier.langs.routes.MessagesController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config

class RemoteAssets @Inject() (config : Config) {

  val gitShaRegex: String = "[0-9a-z]{40}/"

  def getAssetPath(file:String) : Call = {
    val playAsset : Call = PlayAssetRouter.at(file)
    playAsset.copy(
      url = appendAssetPath(playAsset.url)
    )
  }

  def getTemplatePath(file:String) : Call = {
    val templateAsset : Call = TemplateAssetRouter.at(
      file.stripPrefix("/")
    )
    templateAsset.copy(
      url = appendAssetPath(templateAsset.url)
    )
  }

  def messages(lang:String) : Call = {
    val playRoutedMessages = MessagesController.forLang(lang)
    playRoutedMessages.copy(
      url = appendAssetPath(playRoutedMessages.url)
    )
  }

  def assetsPath: String = getAssetPath("").url

  def templatePath: String = getTemplatePath("").url

  private def appendAssetPath(url:String):String = {
    val path = config.assetsPath.stripSuffix("/")
    val gitsha = config.revision
    val asset = url.stripPrefix("/assets/").stripPrefix("/")
    s"$path/$gitsha/$asset"
  }

  def stripGitSha(request: RequestHeader): RequestHeader = {
    request.copy(
      uri = request.uri.replaceFirst(gitShaRegex, ""),
      path = request.path.replaceFirst(gitShaRegex, "")
    )
  }

  def shouldSetNoCache(request: RequestHeader) = {
    if(request.uri.startsWith("/assets/")) {
      gitShaRegex.r.findFirstIn(request.uri) match {
        case Some(shaMatch)  => !shaMatch.contains(config.revision)
        case _ => true
      }
    } else false
  }
}
