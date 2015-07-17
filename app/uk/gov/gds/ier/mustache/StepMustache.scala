package uk.gov.gds.ier.mustache

import play.api.templates.Html
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}
import play.api.http.{ContentTypeOf, MimeTypes}
import play.api.mvc.Content
import uk.gov.gds.ier.langs.Language
import play.api.mvc.Request

trait StepMustache extends MustacheModel {
  self: WithRemoteAssets
    with WithConfig =>

  def Mustache = org.jba.Mustache
  type MessagesForMustache = uk.gov.gds.ier.langs.MessagesForMustache
  type Lang = play.api.i18n.Lang

  implicit def requestToLang(request: Request[Any]): Lang = Language.getLang(request)

  implicit def mustachioContentType:ContentTypeOf[Mustachio] = ContentTypeOf(Some(MimeTypes.HTML))
  implicit def renderMustachioToHtml[T <: Mustachio](mustache:T):Html = mustache.render()

  abstract class Mustachio(mustachePath:String) extends Content {
    def render():Html = {
      Mustache.render(mustachePath, this)
    }

    def body: String = this.render().toString() match {
      case s:String => s
      case _ => ""
    }

    def contentType: String = MimeTypes.HTML
  }
}

