package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.templates.Html
import play.api.mvc.Call
import uk.gov.gds.ier.langs.Language
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}

trait MustacheRendering[T] extends StepMustache with InheritedGovukMustache {
  self: WithRemoteAssets
    with WithConfig =>

  case class CheapHackInheritanceIntoSteps (
      mainContent: Html,
      override val lang: Lang,
      override val htmlLang: String,
      override val pageTitle: String,
      override val contentClasses: String,
      override val sourcePath: String
  ) extends InheritedMustachio("template/cheapHackInheritanceIntoSteps")

  class MustacheRenderer(
      template: MustacheTemplate[T],
      form: ErrorTransformForm[T],
      postUrl: Call,
      application: T
  ) {

    type Request[A] = play.api.mvc.Request[A]

    def html()(implicit request:Request[Any]):Html = {
      val lang = Language.getLang(request)
      val model = template.data(lang, form, postUrl, application)
      val content = Mustache.render(template.mustachePath, model)

      CheapHackInheritanceIntoSteps(
        mainContent = content,
        lang = lang,
        pageTitle = model.question.title,
        htmlLang = model.question.lang.language,
        contentClasses = model.question.contentClasses,
        sourcePath = model.question.postUrl
      ).render()
    }
  }

  implicit class MustacheTemplateWithRenderer(template:MustacheTemplate[T]) {
    def apply(
        form:ErrorTransformForm[T],
        postUrl:Call,
        application:T
    ):MustacheRenderer = {
      new MustacheRenderer(template, form, postUrl, application)
    }
  }
}
