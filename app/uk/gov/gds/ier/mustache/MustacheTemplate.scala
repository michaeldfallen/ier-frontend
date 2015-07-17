package uk.gov.gds.ier.mustache

import uk.gov.gds.ier.validation.ErrorTransformForm
import play.api.mvc.Call
import uk.gov.gds.ier.langs.Messages
import play.api.i18n.Lang

case class Question (
    postUrl:String = "",
    number:String = "",
    title:String = "",
    contentClasses:String = "",
    errorMessages:Seq[String] = Seq.empty
) (
    implicit _lang: Lang = Lang("en")
) {
  val lang: Lang = _lang
}

trait MustacheData {
  val question: Question
  lazy val messages: Map[String,String] = {
    Messages.messagesForLang(question.lang)
  }
}

trait MustacheTemplate[T] {
  val mustachePath: String
  val data: (Lang,ErrorTransformForm[T],Call,T) => MustacheData

  def data(
      form:ErrorTransformForm[T],
      post:Call,
      application:T
  ): MustacheData = {
    data(Lang("en"), form, post, application)
  }
}

trait MustacheTemplateFactories[T] {
  class MustacheTemplateMaker(name:String) {
    def apply(
        data: (ErrorTransformForm[T], Call, T) => MustacheData
    ): MustacheTemplate[T] = {
      MustacheTemplate(
        name,
        (lang, form, post, application) => data(form, post, application)
      )
    }

    def apply(
        data: (ErrorTransformForm[T], Call) => MustacheData
    ): MustacheTemplate[T] = {
      MustacheTemplate(
        name,
        (lang, form, post, application) => data(form, post)
      )
    }

    def apply(
        data: (ErrorTransformForm[T]) => MustacheData
    ) : MustacheTemplate[T] = {
      MustacheTemplate(
        name,
        (lang, form, post, application) => data(form)
      )
    }
  }
  object MustacheTemplate {
    def apply(
        path:String,
        func: (Lang, ErrorTransformForm[T], Call, T) => MustacheData
    ) : MustacheTemplate[T] = {
      new MustacheTemplate[T] {
        val mustachePath = path
        val data = func
      }
    }
    def apply(mustachePath:String):MustacheTemplateMaker = {
      new MustacheTemplateMaker(mustachePath)
    }
  }
  class MultilingualTemplateMaker(name:String) {
    def apply(
        data: (Lang) => (ErrorTransformForm[T], Call) => MustacheData
    ): MustacheTemplate[T] = {
      MustacheTemplate(
        name,
        (lang, form, post, application) => data(lang)(form, post)
      )
    }
  }
  object MultilingualTemplate {
    def apply(mustachePath:String):MultilingualTemplateMaker = {
      new MultilingualTemplateMaker(mustachePath)
    }
  }
}
